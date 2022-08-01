package random832.itemarrows.blocks.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AdvancedDispenserBlockSource extends BlockSourceImpl {
    public AdvancedDispenserBlockSource(ServerLevel pLevel, BlockPos pPos) {
        super(pLevel, pPos);
    }

    AdvancedDispenserBlockEntity getRealEntity() {
        return super.getEntity();
    }

    @SuppressWarnings("unchecked")
    @Override
    public DispenserBlockEntity getEntity() {
        return getRealEntity().vanillaDispenser;
    }

    @Override
    public BlockState getBlockState() {
        return getRealEntity().getLegacyBlockState();
    }
}
