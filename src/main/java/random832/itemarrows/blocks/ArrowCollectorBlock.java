package random832.itemarrows.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;
import random832.itemarrows.ItemArrowsMod;
import random832.itemarrows.capability.CapabilityHelper;

public class ArrowCollectorBlock extends BaseEntityBlock {
    public ArrowCollectorBlock(Properties props) {
        super(props);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ArrowCollectorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (!level.isClientSide)
            return createTickerHelper(type, ItemArrowsMod.COLLECTOR_BE.get(), (l, p, s, e) -> e.tick());
        else
            return null;
    }

    @Override
    public RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.MODEL;
    }

    @Override
    public void onProjectileHit(Level pLevel, BlockState pState, BlockHitResult pHit, Projectile pProjectile) {
        if(pProjectile instanceof AbstractArrow arrow && arrow.pickup == AbstractArrow.Pickup.ALLOWED) {
            if(pLevel.getBlockEntity(pHit.getBlockPos()) instanceof ArrowCollectorBlockEntity be) {
                ItemStack stack = arrow.getPickupItem();
                ItemStack stack2 = ItemHandlerHelper.insertItem(be.inventory, stack, false);
                if(stack2.isEmpty())
                    arrow.discard();
            }
        }
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.is(pNewState.getBlock())) return;
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        if (pLevel.getBlockEntity(pPos) instanceof ArrowCollectorBlockEntity be) {
            CapabilityHelper.dropContents(pLevel, pPos, be.inventory);
            //pLevel.updateNeighbourForOutputSignal(pPos, this);
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }
}
