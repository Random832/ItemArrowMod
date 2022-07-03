package random832.itemarrows.items;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import random832.itemarrows.ItemArrow;

import java.util.List;

public class ItemArrowItem extends ArrowItem {
    public ItemArrowItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> list) {
        if(!allowedIn(tab)) return;
        ItemStack stack = new ItemStack(this);
        ItemHelper.setContainedItem(stack, new ItemStack(Items.COBBLESTONE, 4));
        list.add(stack);
    }

    @Override
    public ItemArrow createArrow(Level level, ItemStack stack, LivingEntity shooter) {
        ItemArrow arrow = new ItemArrow(level, shooter);
        arrow.containedItem = ItemHelper.getContainedItem(stack);
        return arrow;
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return ItemHelper.getItemStackLimit(super.getItemStackLimit(stack), ItemHelper.getContainedItem(stack));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        ItemHelper.appendHoverText(stack, tooltip, flag);
    }
}
