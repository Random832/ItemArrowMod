package random832.itemarrows;

import com.mojang.authlib.GameProfile;
import net.minecraft.Util;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SaddleItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;

public class EntityHelper {
    public static ItemStack giveItems(ItemStack stack, @Nullable Player player, Entity entity) {
        if(entity instanceof Player player2) {
            player2.addItem(stack);
            return ItemStack.EMPTY;
        }
        if(entity instanceof Animal animal && animal.isFood(stack)) {
            stack = sneakyClick(animal, orFake(player, entity.getLevel()), stack);
            if(stack.isEmpty()) return ItemStack.EMPTY;
            // TODO feed nearby animals of same type
            return stack;
        }
        if(entity instanceof ContainerEntity container) {
            InvWrapper handler = new InvWrapper(container);
            return ItemHandlerHelper.insertItem(handler, stack, false);
        }
        if(entity instanceof AbstractChestedHorse horse) {
            // TODO
        }
        if(entity instanceof LivingEntity living && entity instanceof Saddleable && stack.getItem() instanceof SaddleItem saddle) {
            saddle.interactLivingEntity(stack, orFake(player, entity.getLevel()), living, InteractionHand.MAIN_HAND);
            return stack;
        }
        return stack;
    }

    private static Player orFake(@Nullable Player player, Level level) {
        if(player != null) return player;
        else if(level instanceof ServerLevel serverLevel) {
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
