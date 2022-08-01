package random832.itemarrows;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface EntityItemBehavior {
    /**
     * @param stack  The item stack to give the entity
     * @param entity The entity to give an item to
     * @param player The player that fired the arrow, if available
     * @return The remaining item stack. Maybe the original stack with modifications (shrink/split), or a new stack, or ItemStack.EMPTY.
     */
    ItemStack giveItem(ItemStack stack,  Entity entity, @Nullable Player player);
}
