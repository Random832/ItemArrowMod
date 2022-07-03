package random832.itemarrows.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import random832.itemarrows.ItemArrowsMod;

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
            Entity arrow = iterator.next();
            if(arrow.isRemoved()) {
                iterator.remove();
            } else if(arrow.getBoundingBox().intersects(nearVolume)) {
                // TODO put the item in our inventory
                arrow.discard();
                iterator.remove();
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
            ItemEntity itemEntity = arrow.spawnAtLocation(arrow.getPickupItem());
            if(itemEntity != null) {
                itemEntity.getPersistentData().putBoolean("itemarrows:arrowcollectorspawn", true);
                targetEntities.add(itemEntity);
            }
            arrow.discard();
        }
    }
}
