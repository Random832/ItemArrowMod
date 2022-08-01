package random832.itemarrows.blocks.dispenser;

import net.minecraft.Util;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

/**
 * For some reason Mojang wraps this in an opaque lambda.
 *
 * The advanced dispenser needs to access these methods directly on pojectile dispense behaviors, and this is the only way to do it
 */
public class PotionDispenseItemBehavior extends AbstractProjectileDispenseBehavior {
    public Projectile getProjectile(Level level, Position pos, ItemStack stack) {
        return Util.make(new ThrownPotion(level, pos.x(), pos.y(), pos.z()), e -> e.setItem(stack));
    }

    public float getUncertainty() {
        return super.getUncertainty() * 0.5F;
    }

    public float getPower() {
        return super.getPower() * 1.25F;
    }
}