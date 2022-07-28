package random832.itemarrows;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.SpectralArrowRenderer;
import net.minecraft.client.renderer.entity.TippableArrowRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ContainerScreenEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import random832.itemarrows.dispenser.AdvancedDispenserRenderer;
import random832.itemarrows.entities.ArrowItemEntity;
import random832.itemarrows.entities.PreciseBluntArrow;
import random832.itemarrows.entities.ItemArrow;
import random832.itemarrows.gui.AdvancedDispenserMenu;
import random832.itemarrows.gui.AdvancedDispenserScreen;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = ItemArrowsMod.MODID)
public class ClientSetup {
    @SubscribeEvent
    static void registerRenderers(EntityRenderersEvent.RegisterRenderers e) {
        e.registerEntityRenderer(ItemArrowsMod.ITEM_ARROW_ENTITY.get(), c -> new ArrowRenderer<>(c) {
            @Override
            public ResourceLocation getTextureLocation(ItemArrow arrow) {
                return TippableArrowRenderer.NORMAL_ARROW_LOCATION;
            }
        });
        e.registerEntityRenderer(ItemArrowsMod.PRECISE_BLUNT_ARROW.get(), c -> new ArrowRenderer<>(c) {
            @Override
            public ResourceLocation getTextureLocation(PreciseBluntArrow arrow) {
                return TippableArrowRenderer.NORMAL_ARROW_LOCATION;
            }
        });
        e.registerEntityRenderer(ItemArrowsMod.ARROW_ITEM_ENTITY.get(), c -> new EntityRenderer<ItemEntity>(c) {
            @Override
            public void render(ItemEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
                entityRenderDispatcher.render(arrowFor(pEntity), pEntity.getX(), pEntity.getY(), pEntity.getZ(), pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
            }

            @Override
            public ResourceLocation getTextureLocation(ItemEntity pEntity) {
                return entityRenderDispatcher.getRenderer(arrowFor(pEntity)).getTextureLocation(arrowFor(pEntity));
            }

            private AbstractArrow arrowFor(ItemEntity pEntity) {
                // TODO support rendering as other types of arrows
                // somehow prevent this from creating a new entity every frame?
                // not sure how to do this without leaking a ClientLevel
                return new Arrow(EntityType.ARROW, Minecraft.getInstance().level);
            }
        });
        e.registerBlockEntityRenderer(ItemArrowsMod.DISPENSER_BE.get(), AdvancedDispenserRenderer::new);
    }


    @SubscribeEvent
    static void clientSetup(FMLClientSetupEvent e) {
        MenuScreens.register(ItemArrowsMod.DISPENSER_MENU.get(), AdvancedDispenserScreen::new);
    }

    @SubscribeEvent
    static void textureAtlasStitch(TextureStitchEvent.Pre e) {
        if(e.getAtlas().location() != InventoryMenu.BLOCK_ATLAS) return;
        e.addSprite(AdvancedDispenserMenu.EMPTY_GUNPOWDER_SLOT);
    }
}