package random832.itemarrows.entities;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import random832.itemarrows.ItemArrowsMod;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = ItemArrowsMod.MODID)
public class AllayTools {
    @SubscribeEvent
    static void allayTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Allay allay)) return;
        Level level = allay.level;
        level.getProfiler().push("allay_arrow_pickup");
        Vec3i reach = new Vec3i(1, 1, 1); // allay.getPickupReach();

        if (!level.isClientSide && allay.canPickUpLoot() && allay.isAlive() && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(level, allay)) {
            for (AbstractArrow arrow : level.getEntitiesOfClass(AbstractArrow.class, allay.getBoundingBox().inflate(reach.getX(), reach.getY(), reach.getZ()))) {
                ItemStack item = arrow.getPickupItem();
                if (!item.isEmpty() && !arrow.isRemoved() && arrow.pickup == AbstractArrow.Pickup.ALLOWED && allay.wantsToPickUp(item)) {
                    doPickUpArrow(allay, arrow, item);
                }
            }
        }

        level.getProfiler().pop();
    }

    private static void doPickUpArrow(Allay allay, AbstractArrow arrow, ItemStack stack) {
        SimpleContainer inventory = allay.getInventory();
        boolean flag = inventory.canAddItem(stack);
        if (!flag) {
            return;
        }

        Player player = arrow.getOwner() != null ? (Player) arrow.getOwner() : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.THROWN_ITEM_PICKED_UP_BY_ENTITY.trigger((ServerPlayer) player, stack, allay);
        }

        int i = stack.getCount();
        ItemStack remainder = inventory.addItem(stack);
        allay.take(arrow, i - remainder.getCount());
        if (remainder.isEmpty()) {
            arrow.discard();
        } else {
            // TODO ???
            stack.setCount(remainder.getCount());
        }
    }

    static MemoryModuleType<AbstractArrow> getTargetArrowMemory() {
        return null; // TODO
    }

    public static class GoToArrow<E extends LivingEntity> extends Behavior<E> {
        private final Predicate<E> predicate;
        private final int maxDistToWalk;
        private final float speedModifier;

        public GoToArrow(float speedModifier, boolean registered, int maxDistToWalk) {
            this(e -> true, speedModifier, registered, maxDistToWalk);
        }

        public GoToArrow(Predicate<E> predicate, float speedModifier, boolean registered, int maxDistToWalk) {
            super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.WALK_TARGET, registered ? MemoryStatus.REGISTERED : MemoryStatus.VALUE_ABSENT, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryStatus.VALUE_PRESENT));
            this.predicate = predicate;
            this.maxDistToWalk = maxDistToWalk;
            this.speedModifier = speedModifier;
        }

        protected boolean checkExtraStartConditions(ServerLevel pLevel, E pOwner) {
            return !this.isOnPickupCooldown(pOwner) && this.predicate.test(pOwner) && this.getTarget(pOwner).closerThan(pOwner, this.maxDistToWalk);
        }

        protected void start(ServerLevel pLevel, E pEntity, long pGameTime) {
            BehaviorUtils.setWalkAndLookTargetMemories(pEntity, this.getTarget(pEntity), this.speedModifier, 0);
        }

        private boolean isOnPickupCooldown(E p_217254_) {
            return p_217254_.getBrain().checkMemory(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemoryStatus.VALUE_PRESENT);
        }

        private Entity getTarget(E entity) {
            return entity.getBrain().getMemory(getTargetArrowMemory()).get();
        }
    }

    static class NearestArrowSensor extends Sensor<Mob> {
        private static final long XZ_RANGE = 32L;
        private static final long Y_RANGE = 16L;
        public static final int MAX_DISTANCE_TO_WANTED_ITEM = 32;

        public Set<MemoryModuleType<?>> requires() {
            return ImmutableSet.of(getTargetArrowMemory());
        }

        protected void doTick(ServerLevel pLevel, Mob pEntity) {
            Brain<?> brain = pEntity.getBrain();
            List<AbstractArrow> list = pLevel.getEntitiesOfClass(AbstractArrow.class, pEntity.getBoundingBox().inflate(32.0D, 16.0D, 32.0D), a -> a.pickup == AbstractArrow.Pickup.ALLOWED);
            list.sort(Comparator.comparingDouble(pEntity::distanceToSqr));
            Optional<AbstractArrow> optional = Optional.empty();
            for (AbstractArrow arrow : list) {
                if (pEntity.wantsToPickUp(arrow.getPickupItem()) && arrow.closerThan(pEntity, 32.0D) && pEntity.hasLineOfSight(arrow)) {
                    brain.setMemory(getTargetArrowMemory(), Optional.of(arrow));
                    return;
                }
            }
            brain.setMemory(getTargetArrowMemory(), Optional.empty());
        }
    }
}
