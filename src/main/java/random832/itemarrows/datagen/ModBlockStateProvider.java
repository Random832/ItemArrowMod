package random832.itemarrows.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import random832.itemarrows.ItemArrowsMod;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, ItemArrowsMod.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(ItemArrowsMod.DISPENSER_BLOCK.get(),
                models().singleTexture(ItemArrowsMod.DISPENSER_BLOCK.getId().getPath(), new ResourceLocation("block/block"),
                        "particle", new ResourceLocation("block/furnace_top")));
        simpleBlock(ItemArrowsMod.COLLECTOR_BLOCK.get(), cubeAll(ItemArrowsMod.COLLECTOR_BLOCK.get()));
        simpleBlockItem(ItemArrowsMod.DISPENSER_BLOCK.get(), new ModelFile.ExistingModelFile(new ResourceLocation("block/dispenser"), models().existingFileHelper));
        simpleBlockItem(ItemArrowsMod.COLLECTOR_BLOCK.get(), cubeAll(ItemArrowsMod.COLLECTOR_BLOCK.get()));
    }
}