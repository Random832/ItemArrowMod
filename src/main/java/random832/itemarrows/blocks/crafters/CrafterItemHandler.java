package random832.itemarrows.blocks.crafters;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import random832.itemarrows.ItemArrowsMod;
import random832.itemarrows.items.ItemHelper;

import java.util.function.Predicate;

abstract class CrafterItemHandler extends ItemStackHandler {
    private Predicate<ItemStack> containerItem;

    public CrafterItemHandler() {
        super(3);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        if (slot == 1)
            return isItemContainer(stack);
        else return true;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (slot == 0 && !simulate) onInputSlotChanged();
        return super.insertItem(slot, stack, simulate);
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        super.setStackInSlot(slot, stack);
        if (slot == 0) onInputSlotChanged();
    }

    protected void onInputSlotChanged() {};

    protected abstract boolean isItemContainer(ItemStack stack);

    AutomationWrapper automationWrapper() { return new AutomationWrapper(); }

    class AutomationWrapper implements IItemHandler {
        @Override
        public int getSlots() {
            return CrafterItemHandler.this.getSlots();
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return CrafterItemHandler.this.getStackInSlot(slot);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (slot != 2)
                return CrafterItemHandler.this.insertItem(slot, stack, simulate);
            else
                return stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot == 2)
                return CrafterItemHandler.this.extractItem(slot, amount, simulate);
            else
                return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return CrafterItemHandler.this.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return CrafterItemHandler.this.isItemValid(slot, stack);
        }
    }
}
