package random832.itemarrows.blocks.dispenser;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import random832.itemarrows.ItemArrowsMod;

public class AdvancedDispenserMenu extends AbstractContainerMenu {
    public static final ResourceLocation EMPTY_GUNPOWDER_SLOT = new ResourceLocation( ItemArrowsMod.MODID, "item/empty_gunpowder_slot");
    final int SLOT_GUNPOWDER = 0;
    final int DISPENSE_SLOT_START = 1;
    final int DISPENSE_SLOT_END = DISPENSE_SLOT_START + 9;
    final int INV_SLOT_START = DISPENSE_SLOT_END;
    final int INV_SLOT_END = INV_SLOT_START + 36;
    final DataSlot gunpowderSlot;
    final DataSlot xAngleSlot;
    final DataSlot yAngleSlot;
    final DataSlot powerSlot;
    @Nullable AdvancedDispenserBlockEntity be;

    public AdvancedDispenserMenu(int pContainerId, Inventory pPlayerInventory, IItemHandler itemHandler, ContainerData dataAccess) {
        super(ItemArrowsMod.DISPENSER_MENU.get(), pContainerId);
        addSlot(new SlotItemHandler(itemHandler, 0, 8, 107) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.is(Tags.Items.GUNPOWDER);
            }

            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, EMPTY_GUNPOWDER_SLOT);
            }
        });

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                addSlot(new SlotItemHandler(itemHandler, DISPENSE_SLOT_START + i * 3 + j, 26 + 18 * i, 17 + 18 * j));
        for(int i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                this.addSlot(new Slot(pPlayerInventory, j + i * 9 + 9, 8 + j * 18, 138 + i * 18));
        for(int j = 0; j < 9; ++j)
            this.addSlot(new Slot(pPlayerInventory, j, 8 + j * 18, 196));

        gunpowderSlot = addDataSlot(DataSlot.forContainer(dataAccess, AdvancedDispenserBlockEntity.DATA_GUNPOWDER));
        xAngleSlot = addDataSlot(DataSlot.forContainer(dataAccess, AdvancedDispenserBlockEntity.DATA_XANGLE));
        yAngleSlot = addDataSlot(DataSlot.forContainer(dataAccess, AdvancedDispenserBlockEntity.DATA_YANGLE));
        powerSlot = addDataSlot(DataSlot.forContainer(dataAccess, AdvancedDispenserBlockEntity.DATA_POWER));
    }

    public AdvancedDispenserMenu(int pContainerId, Inventory pPlayerInventory, FriendlyByteBuf data) {
        this(pContainerId, pPlayerInventory, new ItemStackHandler(10), new SimpleContainerData(AdvancedDispenserBlockEntity.NUM_DATA_VALUES));
    }

    public AdvancedDispenserMenu(int pContainerId, Inventory pPlayerInventory, AdvancedDispenserBlockEntity be) {
        this(pContainerId, pPlayerInventory, be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseThrow(RuntimeException::new), be.dataAccess);
        this.be = be;
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        Slot slot = this.slots.get(pIndex);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack slotStack = slot.getItem();
        ItemStack tmp = slotStack.copy();
        if (pIndex < DISPENSE_SLOT_END) {
            if (!this.moveItemStackTo(slotStack, INV_SLOT_START, INV_SLOT_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (getSlot(SLOT_GUNPOWDER).mayPlace(slotStack)) {
            if (!this.moveItemStackTo(slotStack, SLOT_GUNPOWDER, SLOT_GUNPOWDER + 1, true)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveItemStackTo(slotStack, DISPENSE_SLOT_START, DISPENSE_SLOT_END, false)) {
            return ItemStack.EMPTY;
        }

        if (slotStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (slotStack.getCount() == tmp.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(pPlayer, slotStack);
        return tmp;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        if (be == null) return true;
        Level level = be.getLevel();
        if (level != pPlayer.getLevel()) return false;
        BlockPos pos = be.getBlockPos();
        if (level.getBlockEntity(pos) != be) return false;
        return !(pPlayer.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > 64);
    }

    public static enum ValueType {
        X_ANGLE, Y_ANGLE, POWER
    }

    public void handleValueUpdate(ValueType param, float value) {
        switch(param) {
            case X_ANGLE -> be.xAngle = Mth.wrapDegrees(value);
            case Y_ANGLE -> be.yAngle = Mth.wrapDegrees(value);
            case POWER -> be.powerSetting = Math.min(Math.max(0f, value), 3f);
        }
        be.updateClientAngles();
    }

}