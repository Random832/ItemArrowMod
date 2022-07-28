package random832.itemarrows.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import random832.itemarrows.EntityHelper;

import java.util.List;

public class EnvelopeItem extends Item {
    public EnvelopeItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if(stack.getCount() > 1) {
            // TODO try to cope better with this
            ItemStack stack2 = stack.split(1);
            player.addItem(giveCore(stack2, player, entity, hand));
        } else {
            player.setItemInHand(hand, giveCore(stack, player, entity, hand));
        }

        return InteractionResult.sidedSuccess(player.level.isClientSide);
    }

    private ItemStack giveCore(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        ItemStack containedItem = ItemHelper.getContainedItem(stack);
        ItemStack remainder = EntityHelper.giveItems(containedItem, player, entity);
        ItemHelper.setContainedItem(stack, remainder);
        return stack;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return ItemHelper.getItemStackLimit(super.getMaxStackSize(stack), ItemHelper.getContainedItem(stack));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        ItemHelper.appendHoverText(stack, tooltip, flag);
    }
}
