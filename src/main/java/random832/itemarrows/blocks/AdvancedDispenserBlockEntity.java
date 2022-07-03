package random832.itemarrows.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import random832.itemarrows.ItemArrowsMod;

public class AdvancedDispenserBlockEntity extends BlockEntity {
    float xAngle = 0;
    float yAngle = 0;

    public AdvancedDispenserBlockEntity(BlockPos pos, BlockState state) {
        super(ItemArrowsMod.DISPENSER_BE.get(), pos, state);
    }

    void setPlacement(BlockPlaceContext context) {
        Player player = context.getPlayer();
        if(player != null) {
            yAngle = Mth.wrapDegrees(player.getYRot() - 180);
            xAngle = -player.getXRot();
        }
    }

}
