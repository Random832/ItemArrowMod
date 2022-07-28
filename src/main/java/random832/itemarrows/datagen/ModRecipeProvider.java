package random832.itemarrows.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
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
        SpecialRecipeBuilder.special(ItemArrowsMod.ARROW_RS.get()).save(consumer, modLoc("attach_item_arrow"));
        SpecialRecipeBuilder.special(ItemArrowsMod.ENVELOPE_RS.get()).save(consumer, modLoc("fill_envelope"));
        ShapelessRecipeBuilder.shapeless(ItemArrowsMod.ENVELOPE_ITEM.get())
                .requires(Tags.Items.SLIMEBALLS)
                .requires(Items.PAPER)
                .requires(Items.PAPER)
                .requires(Items.PAPER)
                .unlockedBy("has_slime", has(Tags.Items.SLIMEBALLS))
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(consumer);
        ShapedRecipeBuilder.shaped(ItemArrowsMod.COLLECTOR_BLOCK.get())
                .pattern(" A ")
                .pattern("RHR")
                .pattern(" C ")
                .define('A', Tags.Items.GEMS_AMETHYST)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('H', Items.HAY_BLOCK)
                .define('C', Tags.Items.CHESTS_WOODEN)
                .unlockedBy("has_amethyst", has(Tags.Items.GEMS_AMETHYST))
                .save(consumer);
        ShapedRecipeBuilder.shaped(ItemArrowsMod.DISPENSER_BLOCK.get())
                .pattern("RQR")
                .pattern("QDQ")
                .pattern("SGS")
                .define('Q', Tags.Items.GEMS_QUARTZ)
                .define('G', Tags.Items.INGOTS_GOLD)
                .define('S', Items.SMOOTH_STONE_SLAB)
                .define('D', Items.DISPENSER)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .unlockedBy("has_dispenser", has(Blocks.DISPENSER))
                .save(consumer);
        ShapedRecipeBuilder.shaped(ItemArrowsMod.REMOTE.get())
                .pattern("RP")
                .pattern("SB")
                .define('B', ItemTags.BUTTONS)
                .define('S', Items.SMOOTH_STONE)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('P', Tags.Items.ENDER_PEARLS)
                .unlockedBy("has_ender_pearl", has(Tags.Items.ENDER_PEARLS))
                .save(consumer);
    }
}
