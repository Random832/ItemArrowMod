package random832.itemarrows;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class ItemArrowItem extends ArrowItem {
    public ItemArrowItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> list) {
        if(!allowedIn(tab)) return;
        ItemStack stack = new ItemStack(this);
        stack.addTagElement("stack", new ItemStack(Items.COBBLESTONE, 4).serializeNBT());
        list.add(stack);
    }

    @Override
    public ItemArrow createArrow(Level level, ItemStack stack, LivingEntity shooter) {
        ItemArrow arrow = new ItemArrow(level, shooter);
        arrow.containedItem = Optional.ofNullable(stack.getTagElement("stack")).map(ItemStack::of).orElse(ItemStack.EMPTY);
        return arrow;
    }
}
