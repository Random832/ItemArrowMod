package random832.itemarrows;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

public class ItemArrow extends AbstractArrow implements ItemSupplier {
    ItemStack containedItem = ItemStack.EMPTY;

    @Nullable
    BlockPos hitPos;
    @Nullable
    Direction hitFace;

    protected ItemArrow(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public ItemArrow(Level level, LivingEntity shooter) {
        super(ItemArrows.ITEM_ARROW_ENTITY.get(), shooter, level);
    }

    @Override
    protected void onHitBlock(BlockHitResult hit) {
        super.onHitBlock(hit);
        hitPos = hit.getBlockPos();
        hitFace = hit.getDirection();
    }

    @Override
    public void tick() {
        super.tick();
        if(hitPos != null && !containedItem.isEmpty()) {
            LazyOptional<IItemHandler> cap = CapabilityHelper.getCapability(level, hitPos, hitFace, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            cap.ifPresent(handler -> {
                containedItem = ItemHandlerHelper.insertItem(handler, containedItem, false);
            });
        }
    }

    @Override
    protected ItemStack getPickupItem() {
        ItemStack stack = new ItemStack(ItemArrows.ITEM_ARROW_ITEM.get());
        stack.addTagElement("stack", containedItem.serializeNBT());
        return stack;
    }

    @Override
    public ItemStack getItem() {
        return containedItem;
    }

    // TODO packets, saving
}