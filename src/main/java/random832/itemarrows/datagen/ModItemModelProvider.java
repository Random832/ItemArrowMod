package random832.itemarrows.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import random832.itemarrows.ItemArrowsMod;

import java.util.Objects;

public class ModItemModelProvider extends ItemModelProvider {
    static final ResourceLocation GENERATED = new ResourceLocation("item/generated");

    public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, ItemArrowsMod.MODID, existingFileHelper);
    }

    private void generated(ItemLike item, ResourceLocation parent, ResourceLocation... layers) {
        ResourceLocation location = ForgeRegistries.ITEMS.getKey(item.asItem());
        assert location != null;
        ItemModelBuilder m = withExistingParent(location.getPath(), parent);
        if(layers.length == 0) {
            m.texture("layer0", new ResourceLocation(modid, "item/" + location.getPath()));
        } else {
            for (int i = 0; i < layers.length; i++)
                m.texture("layer" + i, layers[i]);
        }
    }

    private void standard(ItemLike item) {
        generated(item, GENERATED);
    }
    @Override
    protected void registerModels() {
        standard(ItemArrowsMod.ENVELOPE_ITEM.get());
        standard(ItemArrowsMod.ITEM_ARROW_ITEM.get());
    }
}
