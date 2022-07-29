package random832.itemarrows.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import random832.itemarrows.ItemArrowsMod;
import random832.itemarrows.capability.ItemHandlerWrapper;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ArrowCollectorBlockEntity extends BlockEntity {
    private final AABB searchVolume;
    private final AABB nearVolume;
    static final int SEARCH_INTERVAL = 100;
    int searchCountdown = 0;
    Set<ItemEntity> targetEntities = new HashSet<>();
    Vec3 myPos;
    ItemStackHandler inventory = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    LazyOptional<IItemHandler> itemCap = LazyOptional.of(() -> new ItemHandlerWrapper(inventory) {
        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return stack;
        }
    });

    public ArrowCollectorBlockEntity(BlockPos pos, BlockState state) {
        super(ItemArrowsMod.COLLECTOR_BE.get(), pos, state);
        searchVolume = new AABB(pos).inflate(24,8,24);
        nearVolume = new AABB(pos).inflate(1);
        myPos = Vec3.atCenterOf(pos);
        searchCountdown = hashCode() % SEARCH_INTERVAL;
    }

    void tick() {
        Iterator<ItemEntity> iterator = targetEntities.iterator();
        while(iterator.hasNext()) {
            ItemEntity arrow = iterator.next();
            if(arrow.isRemoved()) {
                iterator.remove();
            } else if(arrow.getBoundingBox().intersects(nearVolume)) {
                ItemStack remainder = ItemHandlerHelper.insertItem(inventory, arrow.getItem(), false);
                if(remainder.isEmpty()) {
                    arrow.discard();
                    iterator.remove();
                } else {
                    arrow.setItem(remainder);
                }
            } else {
                Vec3 aPos = arrow.position().add(0, arrow.getBbHeight() * .5, 0);
                Vec3 toMe = myPos.subtract(aPos);
                double distance = toMe.length();
                double speed = 0.2 / (distance);
                arrow.setDeltaMovement(arrow.getDeltaMovement().add(toMe.normalize().scale(speed)));
                PacketDistributor.TRACKING_ENTITY.with(() -> arrow).send(new ClientboundSetEntityMotionPacket(arrow)); // ????
            }
        }

        if(--searchCountdown < 0) {
            searchForArrows();
            searchCountdown = SEARCH_INTERVAL;
        }
    }

    private void searchForArrows() {
        assert level != null;
        for(ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class, searchVolume)) {
            if (!targetEntities.contains(itemEntity) && (itemEntity.getPersistentData().getBoolean("itemarrows:arrowcollectorspawn") || itemEntity.getItem().is(ItemTags.ARROWS))) {
                targetEntities.add(itemEntity);
            }
        }
        for (AbstractArrow arrow : level.getEntitiesOfClass(AbstractArrow.class, searchVolume)) {
            if(arrow.pickup != AbstractArrow.Pickup.ALLOWED) continue;
            if(arrow.life < 200) continue; // ???
            //ItemEntity itemEntity = new ArrowItemEntity(arrow); // TODO renderer doesn't work
            ItemEntity itemEntity = arrow.spawnAtLocation(arrow.getPickupItem());
            itemEntity.getPersistentData().putBoolean("itemarrows:arrowcollectorspawn", true);
            targetEntities.add(itemEntity);
            level.addFreshEntity(itemEntity);
            arrow.discard();
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, itemCap);
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemCap.invalidate();
    }
}
