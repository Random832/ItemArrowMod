package random832.itemarrows.blocks.crafters;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
import org.jetbrains.annotations.Nullable;
import random832.itemarrows.ItemArrowsMod;
import random832.itemarrows.items.ItemHelper;

public class EnvelopeCrafterBlockEntity extends BlockEntity implements MenuProvider {
    CrafterItemHandler inventory = new CrafterItemHandler() {
        @Override
        protected void onInputSlotChanged() {
            cooldown = 20;
        }

        @Override
        protected boolean isItemContainer(ItemStack stack) {
            return stack.is(ItemArrowsMod.ENVELOPE_ITEM.get()) && ItemHelper.getContainedItem(stack).isEmpty();
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    LazyOptional<IItemHandler> rawCap = LazyOptional.of(() -> inventory);
    LazyOptional<IItemHandler> automationWrapper = LazyOptional.of(inventory::automationWrapper);

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
        return ItemArrowsMod.ENVELOPE_CRAFTER_BLOCK.get().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new CrafterMenu(ItemArrowsMod.ENVELOPE_CRAFTER_MENU.get(), pContainerId, pPlayerInventory, inventory);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("inventory", inventory.serializeNBT());
        pTag.putInt("cooldown", cooldown);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        inventory.deserializeNBT(pTag.getCompound("inventory"));
        cooldown = pTag.getInt("cooldown");
    }
}
