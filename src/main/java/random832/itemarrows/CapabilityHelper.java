package random832.itemarrows;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

public class CapabilityHelper {
    public static <T> LazyOptional<T> getCapability(Level level, BlockPos pos, Direction side, Capability<T> itemHandlerCapability) {
        BlockEntity be = level.getBlockEntity(pos);
        if(be != null) {
            return be.getCapability(itemHandlerCapability, side);
        }
        // TODO composters
        return LazyOptional.empty();
    }
}
