package random832.itemarrows;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ItemTagProvider extends TagsProvider<Item> {
    protected ItemTagProvider(DataGenerator generator, Registry<Item> registry, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, registry, ItemArrows.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(ItemTags.ARROWS).add(ItemArrows.ITEM_ARROW_ITEM.get());
    }
}
