package random832.itemarrows.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import random832.itemarrows.ItemArrowsMod;
import random832.itemarrows.items.ItemHelper;

public class ArrowRecipe extends CustomRecipe {
    public ArrowRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        int indexArrow = -1;
        int indexEnvelope = -1;
        ItemStack stackEnvelope = null;
        for(int i=0; i<container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if(stack.is(Items.ARROW)) {
                if(indexArrow != -1) return false;
                indexArrow = i;
            } else if(stack.is(ItemArrowsMod.ENVELOPE_ITEM.get())) {
                if(indexEnvelope != -1) return false;
                stackEnvelope = stack;
                indexEnvelope = i;
            } else if(!stack.isEmpty()) {
                return false;
            }
        }
        return indexArrow >= 0 && indexEnvelope >= 0 && (!ItemHelper.getContainedItem(stackEnvelope).isEmpty() || indexArrow < indexEnvelope);
    }

    @Override
    public ItemStack assemble(CraftingContainer container) {
        ItemStack stackEnvelope = null;
        for(int i=0; i<container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if(stack.is(ItemArrowsMod.ENVELOPE_ITEM.get())) {
                stackEnvelope = stack;
                break;
            }
        }
        ItemStack result = new ItemStack(ItemArrowsMod.ITEM_ARROW_ITEM.get());
        if(stackEnvelope != null)
            ItemHelper.setContainedItem(result, ItemHelper.getContainedItem(stackEnvelope));
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return w * h > 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ItemArrowsMod.ARROW_RS.get();
    }

    @Override
    public ItemStack getResultItem() {
        return ItemArrowsMod.ITEM_ARROW_ITEM.get().getDefaultInstance();
    }
}
