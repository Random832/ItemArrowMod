package random832.itemarrows.blocks.dispenser;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.gui.widget.ForgeSlider;
import random832.itemarrows.ItemArrowsMod;
import random832.itemarrows.network.ModPackets;
import random832.itemarrows.network.ServerBoundSetDispenserParametersPacket;

import java.util.List;
import java.util.Optional;

public class AdvancedDispenserScreen extends AbstractContainerScreen<AdvancedDispenserMenu> {
    private static final ResourceLocation CONTAINER_LOCATION = new ResourceLocation(ItemArrowsMod.MODID, "textures/gui/container/advanced_dispenser.png");
    private GunpowderWidget gunpowderWidget;
    private MySlider yAngleSlider;
    private MySlider xAngleSlider;
    private MySlider powerSlider;
    private int timeSinceLastKeyPress = 65536;

    public AdvancedDispenserScreen(AdvancedDispenserMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageHeight = 220;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        yAngleSlider = addRenderableWidget(new MySlider(leftPos + 25, topPos + 70, 144, 18, "info." + ItemArrowsMod.MODID + ".slider_label.yangle", -180, 180, 0, 1, 0, AdvancedDispenserMenu.ValueType.Y_ANGLE));
        xAngleSlider = addRenderableWidget(new MySlider(leftPos + 25, topPos + 88, 144, 18, "info." + ItemArrowsMod.MODID + ".slider_label.xangle", -90, 90, 0, 1, 0, AdvancedDispenserMenu.ValueType.X_ANGLE));
        powerSlider = addRenderableWidget(new MySlider(leftPos + 25, topPos + 106, 144, 18, "info." + ItemArrowsMod.MODID + ".slider_label.power", 0, 3, 1, .01, 2, AdvancedDispenserMenu.ValueType.POWER));
        gunpowderWidget = new GunpowderWidget(AdvancedDispenserScreen.this.leftPos + 9, AdvancedDispenserScreen.this.topPos + 18);
        addRenderableWidget(gunpowderWidget);
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, CONTAINER_LOCATION);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(pPoseStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        if(gunpowderWidget.isShowingTooltip()) {
            gunpowderWidget.renderToolTip(pPoseStack, pMouseX, pMouseY);
        } else {
            renderTooltip(pPoseStack, pMouseX, pMouseY);
        }
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        // re-enable the standard screen logic for dragging ignored by AbstractDispenserScreen
        if (this.getFocused() != null && this.isDragging() && pButton == 0)
            return this.getFocused().mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    private class GunpowderWidget extends AbstractWidget {
        public GunpowderWidget(int x, int y) {
            super(x, y, 14, 86, CommonComponents.EMPTY);
        }

        @Override
        public Component getMessage() {
            return Component.literal("Gunpowder: " + menu.gunpowderSlot.get() + " gr");
        }

        @Override
        public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
            pNarrationElementOutput.add(NarratedElementType.TITLE, Component.translatable("narration.dispenser.gunpowder_meter", menu.gunpowderSlot.get()));
        }

        @Override
        public void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY) {
            AdvancedDispenserScreen.this.renderTooltip(pPoseStack, List.of(getMessage()), Optional.empty(), pMouseX, pMouseY);
        }

        @Override
        public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            float fGunpowder = menu.gunpowderSlot.get() / (float) AdvancedDispenserBlockEntity.MAX_GUNPOWDER;
            int gunpowderPixels = Math.round(height * fGunpowder);
            RenderSystem.setShaderTexture(0, CONTAINER_LOCATION);
            blit(pPoseStack, x, y+height-gunpowderPixels, 176, height - gunpowderPixels, width, gunpowderPixels);
        }

        boolean isShowingTooltip() {
            return isHovered;
        }
    }

    private class MySlider extends ForgeSlider {
        private final AdvancedDispenserMenu.ValueType param;

        public MySlider(int pX, int pY, int pWidth, int pHeight, String translationKey, double min, double max, double value, double step, int precision, AdvancedDispenserMenu.ValueType param) {
            super(pX, pY, pWidth, pHeight, Component.translatable(translationKey), CommonComponents.EMPTY, min, max, value, step, precision, true);
            this.param = param;
        }

        @Override
        public void setValue(double value) {
            // ForgeSlider has some weird jank around the keyboard events here?
            double former = this.value;
            super.setValue(value);
            if(former != this.value)
                applyValue();
        }

        @Override
        protected void applyValue() {
            if(!suppressUpdates)
                ModPackets.INSTANCE.sendToServer(new ServerBoundSetDispenserParametersPacket(menu.containerId, param, (float)this.getValue()));
        }
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        timeSinceLastKeyPress = 0;
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    boolean suppressUpdates = false;
    @Override
    protected void containerTick() {
        super.containerTick();
        if(!(isDragging() || timeSinceLastKeyPress < 10)) {
            suppressUpdates = true;
            xAngleSlider.setValue(menu.xAngleSlot.get() / 180f);
            yAngleSlider.setValue(menu.yAngleSlot.get() / 180f);
            powerSlider.setValue(menu.powerSlot.get() / 10000f);
            suppressUpdates = false;
        }
        timeSinceLastKeyPress += 1;
    }
}