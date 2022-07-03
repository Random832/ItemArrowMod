package random832.itemarrows.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import random832.itemarrows.ItemArrowsMod;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    static String modLoc(String name) {
        return ItemArrowsMod.MODID + ":" + name;
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        SpecialRecipeBuilder.special(ItemArrowsMod.RS_ARROW.get()).save(consumer, modLoc("attach_item_arrow"));
        SpecialRecipeBuilder.special(ItemArrowsMod.RS_ENVELOPE.get()).save(consumer, modLoc("fill_envelope"));
        ShapelessRecipeBuilder.shapeless(ItemArrowsMod.ENVELOPE_ITEM.get())
                .requires(Tags.Items.SLIMEBALLS)
                .requires(Items.PAPER)
                .requires(Items.PAPER)
                .requires(Items.PAPER)
                .unlockedBy("has_slime", has(Tags.Items.SLIMEBALLS))
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(consumer);
    }
}
