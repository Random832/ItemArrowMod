package random832.itemarrows.blocks.dispenser;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class AdvancedDispenserItem extends BlockItem {
    public AdvancedDispenserItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        if(super.placeBlock(context, state)) {
            if(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof AdvancedDispenserBlockEntity be) {
                be.setPlacement(context);
            }
            return true;
        } else return false;
    }
}
