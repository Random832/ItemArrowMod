package random832.itemarrows.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import random832.itemarrows.ItemArrowsMod;

import java.util.List;
import java.util.Optional;

public abstract class ItemHelper extends Item {
    public ItemHelper(Properties props) {
        super(props);
    }

    public static ItemStack getContainedItem(ItemStack stack) {
        return Optional.ofNullable(stack.getTagElement("stack")).map(ItemStack::of).orElse(ItemStack.EMPTY);
    }

    public static void setContainedItem(ItemStack stack, ItemStack containedItem) {
        stack.addTagElement("stack", containedItem.serializeNBT());
    }

    public static int getItemStackLimit(int baseStackLimit, ItemStack containedItem) {
        if(containedItem.isEmpty()) return baseStackLimit;
        return Math.min(baseStackLimit, containedItem.getMaxStackSize() / containedItem.getCount());
    }

    public static void appendHoverText(ItemStack stack, List<Component> tooltip, TooltipFlag flag) {
        ItemStack item = ItemHelper.getContainedItem(stack);
        if(item.isEmpty()) {
            tooltip.add(Component.translatable("info."+ItemArrowsMod.MODID+".empty"));
        } else {
            int count = item.getCount();
            int count_total = count * stack.getCount();
            Component name = item.getHoverName();
            if(count == count_total) {
                tooltip.add(Component.translatable("info." + ItemArrowsMod.MODID + ".contains", count, name));
            } else {
                tooltip.add(Component.translatable("info." + ItemArrowsMod.MODID + ".contains", count, name, count_total));
            }
        }
    }
}
