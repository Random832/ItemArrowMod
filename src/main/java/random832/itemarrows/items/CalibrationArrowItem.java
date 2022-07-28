package random832.itemarrows.items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import random832.itemarrows.entities.PreciseBluntArrow;

public class CalibrationArrowItem extends ArrowItem {
    public CalibrationArrowItem(Properties p_40512_) {
        super(p_40512_);
    }

    @Override
    public AbstractArrow createArrow(Level pLevel, ItemStack pStack, LivingEntity pShooter) {
        // TODO can we get the perfect precision here
        return new PreciseBluntArrow(pLevel, pShooter);
    }
}
