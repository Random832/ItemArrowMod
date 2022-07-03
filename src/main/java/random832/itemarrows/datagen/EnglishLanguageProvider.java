package random832.itemarrows.datagen;

import net.minecraft.data.DataGenerator;
import random832.itemarrows.ItemArrowsMod;

public class EnglishLanguageProvider extends net.minecraftforge.common.data.LanguageProvider {
    public EnglishLanguageProvider(DataGenerator gen) {
        super(gen, ItemArrowsMod.MODID, "en_us");
    }

    private static String info(String s) {
        return "info." + ItemArrowsMod.MODID + "." + s;
    }

    @Override
    protected void addTranslations() {
        add(ItemArrowsMod.ITEM_ARROW_ITEM.get(), "Item Arrow");
        add(ItemArrowsMod.ITEM_ARROW_ENTITY.get(), "Item Arrow");
        add(info("contains"), "Contains %s %s");
        add(info("contains.stacked"), "Contains %s %s (%s total)");
        add(info("empty"), "Empty");
    }
}
