package random832.itemarrows.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;
import random832.itemarrows.ItemArrowsMod;
import random832.itemarrows.items.ItemHelper;

public class EnvelopeRecipe extends CustomRecipe {
    public EnvelopeRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        int indexEnvelope = -1;
        int indexFirstItem = -1;
        ItemStack stackItem = null;
        int finalCount = 0;
        for(int i=0; i<container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if(stack.is(ItemArrowsMod.ENVELOPE_ITEM.get())) {
                indexEnvelope = i;
                ItemStack contained = ItemHelper.getContainedItem(stack);
                if (!contained.isEmpty()) {
                    stackItem = contained;
                    finalCount = contained.getCount();
                }
                break;
            }
        }
        if(indexEnvelope == -1) return false; // fast non match if no envelope

        for(int i=0; i<container.getContainerSize(); i++) {
            if(i == indexEnvelope) continue;
            ItemStack stack = container.getItem(i);
            if(!stack.isEmpty()) {
                if(indexFirstItem == -1) indexFirstItem = i;
                if(stack.is(ItemArrowsMod.ENVELOPE_ITEM.get())) return false; // TODO allow re-enveloping, envelopes of empty envelopes
                if(stackItem == null) {
                    stackItem = stack;
                } else {
                    if(!ItemStack.isSameItemSameTags(stackItem, stack)) return false;
                }
                finalCount++;
            }
        }
        if(indexFirstItem >= 0 && indexFirstItem < indexEnvelope && container.getItem(indexFirstItem).is(ItemArrowsMod.PRECISE_ARROW_ITEM.get())) return false; // disambiguate with arrow recipe
        return stackItem != null && finalCount <= stackItem.getMaxStackSize();
    }

    @Override
    public ItemStack assemble(CraftingContainer container) {
        int indexEnvelope = -1;
        ItemStack stackItem = null;
        int finalCount = 0;
        for(int i=0; i<container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if(stack.is(ItemArrowsMod.ENVELOPE_ITEM.get())) {
                indexEnvelope = i;
                ItemStack contained = ItemHelper.getContainedItem(stack);
                if (!contained.isEmpty()) {
                    stackItem = contained;
                    finalCount = contained.getCount();
                }
                break;
            }
        }

        for(int i=0; i<container.getContainerSize(); i++) {
            if(i == indexEnvelope) continue;
            ItemStack stack = container.getItem(i);
            if(!stack.isEmpty()) {
                if(stackItem == null) {
                    stackItem = stack;
                } else {
                    if(!ItemStack.isSameItemSameTags(stackItem, stack)) return ItemStack.EMPTY;
                }
                finalCount++;
            }
        }
        ItemStack result = new ItemStack(ItemArrowsMod.ENVELOPE_ITEM.get());
        if(stackItem != null)
            ItemHelper.setContainedItem(result, ItemHandlerHelper.copyStackWithSize(stackItem, finalCount));
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return false;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ItemArrowsMod.ENVELOPE_RS.get();
    }

    @Override
    public ItemStack getResultItem() {
        return ItemArrowsMod.ENVELOPE_ITEM.get().getDefaultInstance();
    }
}
