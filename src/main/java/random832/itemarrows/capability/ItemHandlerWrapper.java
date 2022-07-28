package random832.itemarrows.capability;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import random832.itemarrows.blocks.ArrowCollectorBlockEntity;

public class ItemHandlerWrapper implements IItemHandler {
    private final IItemHandler wrapped;

    public ItemHandlerWrapper(IItemHandler wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public int getSlots() {
        return wrapped.getSlots();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return wrapped.getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return wrapped.insertItem(slot, stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return wrapped.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return wrapped.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return wrapped.isItemValid(slot, stack);
    }
}
