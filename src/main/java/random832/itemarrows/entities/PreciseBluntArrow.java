package random832.itemarrows.entities;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import random832.itemarrows.ItemArrowsMod;

public class PreciseBluntArrow extends AbstractArrow {
    public PreciseBluntArrow(EntityType<? extends AbstractArrow> pType, Level pLevel) {
        super(pType, pLevel);
        setBaseDamage(0);
    }

    public PreciseBluntArrow(Level level, double x, double y, double z) {
        this(ItemArrowsMod.PRECISE_BLUNT_ARROW.get(), x, y, z, level);
    }

    public PreciseBluntArrow(Level pLevel, LivingEntity pShooter) {
        this(ItemArrowsMod.PRECISE_BLUNT_ARROW.get(), pShooter, pLevel);
    }

    public PreciseBluntArrow(EntityType<? extends PreciseBluntArrow> type, LivingEntity shooter, Level level) {
        super(type, shooter, level);
        setBaseDamage(0);
    }

    public PreciseBluntArrow(EntityType<? extends PreciseBluntArrow> type, double x, double y, double z, Level level) {
        super(type, x, y, z, level);
        setBaseDamage(0);
    }

    @Override
    public ItemStack getPickupItem() {
        return ItemArrowsMod.CALIBRATION_ARROW_ITEM.get().getDefaultInstance();
    }

    @Override
    public void shoot(double pX, double pY, double pZ, float pVelocity, float pInaccuracy) {
        super.shoot(pX, pY, pZ, pVelocity, 0);
    }

    @Override
    public void shootFromRotation(Entity pShooter, float pX, float pY, float pZ, float pVelocity, float pInaccuracy) {
        super.shootFromRotation(pShooter, pX, pY, pZ, pVelocity, 0);
    }
}
