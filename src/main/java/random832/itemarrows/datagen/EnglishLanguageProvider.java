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
        add(ItemArrowsMod.ENVELOPE_ITEM.get(), "Item Envelope");
        add(ItemArrowsMod.PRECISE_ARROW_ITEM.get(), "Blank Precision Arrow");
        add(info("contains"), "Contains %s %s");
        add(info("contains.stacked"), "Contains %s %s (%s total)");
        add(info("empty"), "Empty");
        add(info("empty.envelope"), "Empty. Fill in crafting grid");
        add(info("remote.no_coords"), "No dispenser coordinates configured");
        add(info("remote.no_dispenser"), "No dispenser found at coordinates %s");
        add(info("remote.set_dispenser"), "Configured remote to control dispenser at %s");
        add(info("remote.set_target"), "Configured dispenser at %s to aim at %s");
        add(info("slider_label.yangle"), "Traverse: ");
        add(info("slider_label.xangle"), "Elevation: ");
        add(info("slider_label.power"), "Power: ");
        add("advancements.item_arrows.envelope.title", "Slimed, Sealed, Delivered");
        add("advancements.item_arrows.envelope.description", "Craft an envelope");
        add("advancements.item_arrows.arrow.title", "Arrow Dynamics");
        add("advancements.item_arrows.arrow.description", "Attach an item to an arrow");
        add("container." + ItemArrowsMod.MODID + ".advanced_dispenser", "Advanced Dispenser");
    }
}
