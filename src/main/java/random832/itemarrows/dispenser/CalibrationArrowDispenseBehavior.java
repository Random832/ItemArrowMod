package random832.itemarrows.dispenser;

import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import random832.itemarrows.entities.PreciseBluntArrow;

public class CalibrationArrowDispenseBehavior  extends AbstractProjectileDispenseBehavior {
    @Override
    public Projectile getProjectile(Level level, Position pos, ItemStack stack) {
        PreciseBluntArrow arrow = new PreciseBluntArrow(level, pos.x(), pos.y(), pos.z());
        arrow.pickup = AbstractArrow.Pickup.ALLOWED;
        return arrow;
    }

    @Override
    public float getUncertainty() {
        return 0;
    }
}