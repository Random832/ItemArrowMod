package random832.itemarrows.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class CapabilityHelper {
    public static <T> LazyOptional<T> getCapability(Level level, BlockPos pos, Direction side, Capability<T> itemHandlerCapability) {
        BlockEntity be = level.getBlockEntity(pos);
        if(be != null) {
            return be.getCapability(itemHandlerCapability, side);
        }
        // TODO composters
        return LazyOptional.empty();
    }

    public static void dropContents(Level level, BlockPos worldPosition, IItemHandler items) {
        NonNullList<ItemStack> list = NonNullList.create();
        for (int i = 0; i < items.getSlots(); i++) {
            final ItemStack stackInSlot = items.getStackInSlot(i);
            if (!stackInSlot.isEmpty())
                list.add(stackInSlot);
        }
        Containers.dropContents(level, worldPosition, list);
    }
}
