package random832.itemarrows;

import com.mojang.authlib.GameProfile;
import net.minecraft.Util;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SaddleItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.RangedWrapper;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.function.BiPredicate;

public class EntityItemBehaviors {
    record Entry(BiPredicate<Entity, ItemStack> predicate, EntityItemBehavior behavior, int priority, int registeredOrder) implements Comparable<Entry> {
        @Override
        public int compareTo(Entry o) {
            return Comparator.comparingInt(Entry::priority).reversed().thenComparingInt(Entry::registeredOrder).compare(this, o);
        }
    }

    static TreeSet<Entry> registry = new TreeSet<>();
    static int registryCounter = 0;
    static void register(BiPredicate<Entity, ItemStack> predicate, EntityItemBehavior behavior, int priority) {
        registry.add(new Entry(predicate, behavior, priority, registryCounter++));
    }

    static void bootstrap() {
        // Multiples of 10 are used for the priority so other mods can slot behaviors between them if necessary
        // In theory, entries of the same priority should not overlap [e.g. saddles are not likely to be food, an entity isn't likely to implement both ContainerEntity an AbstractChestedHorse], but registration order is used as a fallback.

        // Special behaviors specific to an entity or item.
        register((e, i) -> e instanceof Animal a && a.isFood(i), EntityItemBehaviors::feed, 10);
        register((e, i) -> e instanceof LivingEntity && e instanceof Saddleable && i.getItem() instanceof SaddleItem, EntityItemBehaviors::saddle, 10);
        register((e, i) -> e instanceof Llama && i.getItem() instanceof BlockItem bi && bi.getBlock() instanceof WoolCarpetBlock, EntityItemBehaviors::saddleLlama, 10);

        // Basic priority for simply putting an item in an inventory.
        register((e, i) -> e instanceof Player, EntityItemBehaviors::givePlayer, 0);
        register((e, i) -> e instanceof ContainerEntity, EntityItemBehaviors::putContainer, 0);
        register((e, i) -> e instanceof AbstractChestedHorse, EntityItemBehaviors::putHorseChest, 0);
    }

    private static ItemStack givePlayer(ItemStack stack, Entity entity, @Nullable Player player) {
        ((Player)entity).addItem(stack);
        return ItemStack.EMPTY;
    }

    private static ItemStack putContainer(ItemStack stack, Entity entity, @Nullable Player player) {
        InvWrapper handler = new InvWrapper((ContainerEntity)entity);
        return ItemHandlerHelper.insertItem(handler, stack, false);
    }

    private static ItemStack putHorseChest(ItemStack stack, Entity entity, @Nullable Player player) {
        final ItemStack fstack = stack;
        return entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(h -> {
            IItemHandler chest = new RangedWrapper((IItemHandlerModifiable) h, 2, h.getSlots());
            return ItemHandlerHelper.insertItem(chest, fstack, false);
        }).orElse(stack);
    }

    private static ItemStack feed(ItemStack stack, Entity entity, @Nullable Player player) {
        stack = sneakyClick((Animal)entity, orFake(player, entity.getLevel()), stack);
        if (stack.isEmpty()) return ItemStack.EMPTY;
        // TODO feed nearby animals of same type
        return stack;
    }

    private static ItemStack saddle(ItemStack stack, Entity entity, @Nullable Player player) {
        stack.getItem().interactLivingEntity(stack, orFake(player, entity.getLevel()), (LivingEntity)entity, InteractionHand.MAIN_HAND);
        return stack;
    }

    private static ItemStack saddleLlama(ItemStack stack, Entity entity, @Nullable Player player) {
        Llama llama = (Llama)entity;
        WoolCarpetBlock carpet = (WoolCarpetBlock) ((BlockItem) stack.getItem()).getBlock();
        stack.getItem().interactLivingEntity(stack, orFake(player, entity.getLevel()), (LivingEntity)entity, InteractionHand.MAIN_HAND);
        return stack;
    }

    public static ItemStack giveItems(ItemStack stack, Entity entity, @Nullable Player player) {
        for (Entry entry : registry) {
            if(entry.predicate.test(entity, stack)) {
                stack = entry.behavior.giveItem(stack, entity, player);
                return stack.isEmpty() ? ItemStack.EMPTY : stack;
            }
        }
        return stack;
    }

    private static Player orFake(@Nullable Player player, Level level) {
        if (player != null) return player;
        else if (level instanceof ServerLevel serverLevel) {
            return FakePlayerFactory.get(serverLevel, new GameProfile(Util.NIL_UUID, "item_arrow_no_player"));
        } else {
            ItemArrowsMod.LOGGER.warn("Fake player requested on client");
            return ClientHelper.getPlayer();
        }
    }

    private static ItemStack sneakyClick(LivingEntity target, Player player, ItemStack item) {
        ItemStack realMainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        player.setItemInHand(InteractionHand.MAIN_HAND, item);
        player.interactOn(target, InteractionHand.MAIN_HAND);
        ItemStack result = player.getItemInHand(InteractionHand.MAIN_HAND);
        player.setItemInHand(InteractionHand.MAIN_HAND, realMainHand);
        return result;
    }
}