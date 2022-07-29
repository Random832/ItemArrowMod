package random832.itemarrows.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import random832.itemarrows.ItemArrowsMod;
import random832.itemarrows.gui.CrafterMenu;
import random832.itemarrows.items.ItemHelper;

public class EnvelopeCrafterBlockEntity extends BlockEntity implements MenuProvider {
    ItemStackHandler inventory = new ItemStackHandler(3) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if(slot == 1)
                return stack.is(ItemArrowsMod.ENVELOPE_ITEM.get()) && ItemHelper.getContainedItem(stack).isEmpty();
            else return true;
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if(slot == 0 && !simulate) cooldown = 20;
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            super.setStackInSlot(slot, stack);
            if(slot == 0) cooldown = 20;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    LazyOptional<IItemHandler> rawCap = LazyOptional.of(() -> inventory);
    LazyOptional<IItemHandler> automationWrapper = LazyOptional.of(() -> new IItemHandler() {
        @Override
        public int getSlots() {
            return inventory.getSlots();
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return inventory.getStackInSlot(slot);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if(slot != 2)
                return inventory.insertItem(slot, stack, simulate);
            else
                return stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if(slot == 2)
                return inventory.extractItem(slot, amount, simulate);
            else
                return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return inventory.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return inventory.isItemValid(slot, stack);
        }
    });

    public EnvelopeCrafterBlockEntity(BlockPos pPos, BlockState pState) {
        super(ItemArrowsMod.ENVELOPE_STUFFER_BE.get(), pPos, pState);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        rawCap.invalidate();
        automationWrapper.invalidate();
    }

    private int cooldown = 0;

    public void tick() {
        cooldown--;
        if(cooldown < 0) {
            tryCraftEnvelope();
            cooldown = 10;
        }
    }

    private boolean tryCraftEnvelope() {
        ItemStack items = inventory.getStackInSlot(0);
        ItemStack envelopes = inventory.getStackInSlot(1);
        ItemStack filledStack = inventory.getStackInSlot(2);
        if(items.isEmpty() || envelopes.isEmpty())
            return false;
        if(!filledStack.isEmpty()) {
            // try to craft a new filled envelope matching the current filled envelopes
            if (filledStack.getCount() >= filledStack.getMaxStackSize()) return false;
            ItemStack currentContentsStack = ItemHelper.getContainedItem(filledStack);
            if (!ItemStack.isSameItemSameTags(currentContentsStack, items)) return false;
            if (items.getCount() < currentContentsStack.getCount()) return false;
            doCraftEnvelope(currentContentsStack.getCount());
            return true;
        }
        else {
            doCraftEnvelope(items.getCount());
            return true;
        }
    }

    private void doCraftEnvelope(int count) {
        // these inventory operations should never fail, I have no idea how to cope if they do
        ItemStack contents = inventory.extractItem(0, count, false);
        inventory.extractItem(1, 1, false);
        ItemStack result = new ItemStack(ItemArrowsMod.ENVELOPE_ITEM.get());
        ItemHelper.setContainedItem(result, contents);
        inventory.insertItem(2, result,false);
    }


    @Override
    public Component getDisplayName() {
        return Component.translatable("container." + ItemArrowsMod.MODID + ".envelope_crafter");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new CrafterMenu(ItemArrowsMod.ENVELOPE_CRAFTER_MENU.get(), pContainerId, pPlayerInventory, inventory);
    }

}
