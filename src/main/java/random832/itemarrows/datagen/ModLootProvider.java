package random832.itemarrows.datagen;

import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.registries.RegistryObject;
import random832.itemarrows.ItemArrowsMod;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModLootProvider extends LootTableProvider {
    public ModLootProvider(DataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        return List.of(Pair.of(ModBlockLoot::new, LootContextParamSets.BLOCK));
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
        for (Map.Entry<ResourceLocation, LootTable> entry : map.entrySet())
            LootTables.validate(validationtracker, entry.getKey(), entry.getValue());
    }

    @Override
    public String getName() {
        return super.getName();
    }

    private class ModBlockLoot extends BlockLoot {
        @Override
        protected void addTables() {
            dropSelf(ItemArrowsMod.DISPENSER_BLOCK.get());
            dropSelf(ItemArrowsMod.COLLECTOR_BLOCK.get());
            dropSelf(ItemArrowsMod.ARROW_CRAFTER_BLOCK.get());
            dropSelf(ItemArrowsMod.ENVELOPE_CRAFTER_BLOCK.get());
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return ItemArrowsMod.BLOCKS.getEntries().stream().map(RegistryObject::get).toList();
        }
    }
}