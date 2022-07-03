package random832.itemarrows.datagen;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import random832.itemarrows.ItemArrowsMod;
import random832.itemarrows.items.ModItemTags;

public class ItemTagProvider extends TagsProvider<Item> {
    public ItemTagProvider(DataGenerator generator, Registry<Item> registry, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, registry, ItemArrowsMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(ItemTags.ARROWS).add(ItemArrowsMod.ITEM_ARROW_ITEM.get());
        tag(ModItemTags.ATTACHABLE_ARROWS).add(Items.ARROW, Items.SPECTRAL_ARROW);
    }
}
