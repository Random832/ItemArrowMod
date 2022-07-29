package random832.itemarrows.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
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
        simpleCube(ItemArrowsMod.COLLECTOR_BLOCK.get());
        simpleCube(ItemArrowsMod.ENVELOPE_CRAFTER_BLOCK.get());
        simpleCube(ItemArrowsMod.ARROW_CRAFTER_BLOCK.get());
        simpleBlockItem(ItemArrowsMod.DISPENSER_BLOCK.get(), new ModelFile.ExistingModelFile(new ResourceLocation("block/dispenser"), models().existingFileHelper));
    }

    private void simpleCube(Block block) {
        ModelFile model = cubeAll(block);
        simpleBlock(block, model);
        simpleBlockItem(block, model);
    }
}