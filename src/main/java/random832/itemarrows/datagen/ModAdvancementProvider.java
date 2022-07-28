package random832.itemarrows.datagen;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;
import random832.itemarrows.ItemArrowsMod;

import java.util.function.Consumer;

public class ModAdvancementProvider extends AdvancementProvider {
    public ModAdvancementProvider(DataGenerator generatorIn, ExistingFileHelper fileHelperIn) {
        super(generatorIn, fileHelperIn);
    }

    @Override
    protected void registerAdvancements(Consumer<Advancement> consumer, ExistingFileHelper fileHelper) {
        Advancement envelope = Advancement.Builder.advancement()
                .display(ItemArrowsMod.ENVELOPE_ITEM.get(),
                        Component.translatable("advancements.item_arrows.envelope.title"),
                        Component.translatable("advancements.item_arrows.envelope.description"),
                        new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"),
                        FrameType.TASK, true, true, false)
                .addCriterion("envelope", InventoryChangeTrigger.TriggerInstance.hasItems(ItemArrowsMod.ENVELOPE_ITEM.get()))
                .save(consumer, modLoc("envelope"));
        Advancement arrow = Advancement.Builder.advancement()
                .parent(envelope)
                .display(ItemArrowsMod.ITEM_ARROW_ITEM.get(),
                        Component.translatable("advancements.item_arrows.arrow.title"),
                        Component.translatable("advancements.item_arrows.arrow.description"),
                        new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"),
                        FrameType.TASK, true, true, false)
                .addCriterion("arrow", InventoryChangeTrigger.TriggerInstance.hasItems(ItemArrowsMod.ITEM_ARROW_ITEM.get()))
                .save(consumer, modLoc("arrow"));
        //Advancement breed = Advancement.Builder.advancement()
        //        .parent(arrow)
        //        .display(Items.WHEAT,
        //                Component.translatable("advancements.item_arrows.arrow.title"),
        //                Component.translatable("advancements.item_arrows.arrow.description"),
        //                new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"),
        //                FrameType.CHALLENGE, true, true, false)
        //        .addCriterion("arrow", InventoryChangeTrigger.TriggerInstance.hasItems(ItemArrowsMod.ITEM_ARROW_ITEM.get()))
        //        .save(consumer, "item_arrows/root");
    }

    private static String modLoc(String name) {
        return ItemArrowsMod.MODID + ":" + name;
    }
}
