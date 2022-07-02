package random832.itemarrows;

import net.minecraft.data.DataGenerator;

public class EnglishLanguageProvider extends net.minecraftforge.common.data.LanguageProvider {
    public EnglishLanguageProvider(DataGenerator gen) {
        super(gen, ItemArrows.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(ItemArrows.ITEM_ARROW_ITEM.get(), "Item Arrow");
        add(ItemArrows.ITEM_ARROW_ENTITY.get(), "Item Arrow");
        add(ItemArrows.MODID+".info.contains", "Contains %s %s");
        add(ItemArrows.MODID+".info.empty", "Empty");
    }
}
