package random832.itemarrows.items;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import random832.itemarrows.ItemArrowsMod;

public class ModItemTags {
    public static final TagKey<Item> ATTACHABLE_ARROWS = register("attachable_arrows");

    @NotNull
    private static TagKey<Item> register(String name) {
        return ForgeRegistries.ITEMS.tags().createTagKey(new ResourceLocation(ItemArrowsMod.MODID, name));
    }
}
