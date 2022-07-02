package random832.itemarrows;

import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemArrowDispenseBehavior extends AbstractProjectileDispenseBehavior {
    @Override
    public Projectile getProjectile(Level level, Position pos, ItemStack stack) {
        ItemArrow arrow = new ItemArrow(level, pos.x(), pos.y(), pos.z());
        arrow.containedItem = ItemArrowItem.getContainedItem(stack);
        arrow.pickup = AbstractArrow.Pickup.ALLOWED;
        return arrow;
    }
}
