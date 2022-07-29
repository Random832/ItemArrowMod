package random832.itemarrows.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import random832.itemarrows.ItemArrowsMod;
import random832.itemarrows.gui.CrafterMenu;

public class ArrowCrafterBlockEntity extends BlockEntity implements MenuProvider {
    public ArrowCrafterBlockEntity(BlockPos pPos, BlockState pState) {
        super(ItemArrowsMod.ARROW_CRAFTER_BE.get(), pPos, pState);
    }

    public void tick() {
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container." + ItemArrowsMod.MODID + ".arrow_crafter");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new CrafterMenu(ItemArrowsMod.ARROW_CRAFTER_MENU.get(), pContainerId, pPlayerInventory, new ItemStackHandler(3));
    }
}
