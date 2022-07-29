package random832.itemarrows.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import random832.itemarrows.capability.CapabilityHelper;
import random832.itemarrows.EntityHelper;
import random832.itemarrows.ItemArrowsMod;
import random832.itemarrows.items.ItemHelper;

import javax.annotation.Nullable;

public class ItemArrow extends PreciseBluntArrow implements ItemSupplier {
    public ItemStack containedItem = ItemStack.EMPTY;

    @Nullable
    BlockPos hitPos;
    @Nullable
    Direction hitFace;
    @Nullable
    private Entity hitEntity;

    public ItemArrow(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public ItemArrow(Level level, LivingEntity shooter) {
        super(ItemArrowsMod.ITEM_ARROW_ENTITY.get(), shooter, level);
    }

    public ItemArrow(Level level, double x, double y, double z) {
        super(ItemArrowsMod.ITEM_ARROW_ENTITY.get(), x, y, z, level);
    }

    @Override
    protected void onHitBlock(BlockHitResult hit) {
        super.onHitBlock(hit);
        hitPos = hit.getBlockPos();
        hitFace = hit.getDirection();
    }

    @Override
    protected void onHitEntity(EntityHitResult hit) {
        super.onHitEntity(hit);
        hitEntity = hit.getEntity();
    }

    @Override
    public void tick() {
        super.tick();
        if(!containedItem.isEmpty()) {
            if (hitPos != null) {
                LazyOptional<IItemHandler> cap = CapabilityHelper.getCapability(level, hitPos, hitFace, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
                cap.ifPresent(handler -> {
                    containedItem = ItemHandlerHelper.insertItem(handler, containedItem, false);
                });
            }
            if (hitEntity != null) {
                containedItem = EntityHelper.giveItems(containedItem, getOwningPlayer(), hitEntity);
            }
        }
    }

    @Nullable
    private Player getOwningPlayer() {
        if(this.getOwner() instanceof Player player)
            return player;
        return null;
    }

    @Override
    public ItemStack getPickupItem() {
        if(containedItem.isEmpty()) {
            if(random.nextFloat() < 0.4) {
                return new ItemStack(ItemArrowsMod.ITEM_ARROW_ITEM.get());
            } else {
                return new ItemStack(Items.ARROW);
            }
        } else {
            ItemStack stack = new ItemStack(ItemArrowsMod.ITEM_ARROW_ITEM.get());
            stack.addTagElement("stack", containedItem.serializeNBT());
            return stack;
        }
    }

    @Override
    public ItemStack getItem() {
        return containedItem;
    }

    @Override
    public double getBaseDamage() {
        return 0;
    }

    @Override
    protected void tickDespawn() {
        super.tickDespawn();
        if(this.isRemoved()) {
            ItemEntity itemEntity = null;
            if(containedItem.isEmpty()) {
                if(random.nextFloat() < 0.4) {
                    itemEntity = spawnAtLocation(ItemArrowsMod.ENVELOPE_ITEM.get().getDefaultInstance());
                }
            } else {
                ItemStack stack = ItemArrowsMod.ENVELOPE_ITEM.get().getDefaultInstance();
                ItemHelper.setContainedItem(stack, containedItem);
                itemEntity = spawnAtLocation(stack);
            }
            if(itemEntity != null) {
               itemEntity.lifespan -= this.life;
            }
        }
    }

    @Override
    public void shoot(double pX, double pY, double pZ, float pVelocity, float pInaccuracy) {
        super.shoot(pX, pY, pZ, pVelocity, 0);
    }

    // TODO packets, saving
}