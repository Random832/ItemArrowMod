package random832.itemarrows.entities;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import random832.itemarrows.ItemArrowsMod;

public class ArrowItemEntity extends ItemEntity {
    public ArrowItemEntity(EntityType<? extends ArrowItemEntity> type, Level level) {
        super(type, level);
    }

    public ArrowItemEntity(ItemEntity original) {
        this(ItemArrowsMod.ARROW_ITEM_ENTITY.get(), original.level);
        this.setPos(original.position());
        this.setDeltaMovement(original.getDeltaMovement());
        this.setItem(original.getItem());
        this.lifespan = original.lifespan;
    }

    public ArrowItemEntity(AbstractArrow original) {
        this(ItemArrowsMod.ARROW_ITEM_ENTITY.get(), original.level);
        this.setPos(original.position());
        this.setDeltaMovement(original.getDeltaMovement());
        this.setItem(original.getPickupItem());
        this.lifespan = 6000 - original.life;
    }
}