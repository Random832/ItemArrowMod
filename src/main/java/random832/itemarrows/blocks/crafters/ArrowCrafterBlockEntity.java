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
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;
import random832.itemarrows.ItemArrowsMod;
import random832.itemarrows.items.ItemHelper;

public class ArrowCrafterBlockEntity extends BlockEntity implements MenuProvider {
    int cooldown = 10;
    CrafterItemHandler inventory = new CrafterItemHandler() {
        @Override
        protected boolean isItemContainer(ItemStack stack) {
            return stack.is(ItemArrowsMod.PRECISE_ARROW_ITEM.get());
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    LazyOptional<IItemHandler> rawCap = LazyOptional.of(() -> inventory);
    LazyOptional<IItemHandler> automationWrapper = LazyOptional.of(inventory::automationWrapper);

    public ArrowCrafterBlockEntity(BlockPos pPos, BlockState pState) {
        super(ItemArrowsMod.ARROW_CRAFTER_BE.get(), pPos, pState);
    }

    public void tick() {
        cooldown--;
        if(cooldown < 0) {
            tryCraftArrow();
            cooldown = 10;
        }
    }

    private boolean tryCraftArrow() {
        ItemStack envelopes = inventory.getStackInSlot(0);
        ItemStack arrows = inventory.getStackInSlot(1);
        ItemStack results = inventory.getStackInSlot(2);
        if(envelopes.isEmpty() || arrows.isEmpty() || results.getCount() > results.getMaxStackSize())
            return false;
        if(!results.isEmpty()) {
            ItemStack envelopeContents = ItemHelper.getContainedItem(envelopes);
            ItemStack arrowContents = ItemHelper.getContainedItem(results);
            if(!ItemStack.isSameItemSameTags(envelopeContents, arrowContents)) return false;
            inventory.extractItem(0, 1, false);
            inventory.extractItem(1, 1, false);
            inventory.insertItem(2, ItemHandlerHelper.copyStackWithSize(arrows, 1), false);
        }
        else {
            inventory.extractItem(0, 1, false);
            inventory.extractItem(1, 1, false);
            ItemStack arrow = new ItemStack(ItemArrowsMod.ITEM_ARROW_ITEM.get());
            ItemStack envelopeContents = ItemHelper.getContainedItem(envelopes);
            ItemHelper.setContainedItem(arrow, envelopeContents);
            inventory.insertItem(2, arrow, false);
        }
        return true;
    }

    @Override
    public Component getDisplayName() {
        return ItemArrowsMod.ARROW_CRAFTER_BLOCK.get().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new CrafterMenu(ItemArrowsMod.ARROW_CRAFTER_MENU.get(), pContainerId, pPlayerInventory, inventory);
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
