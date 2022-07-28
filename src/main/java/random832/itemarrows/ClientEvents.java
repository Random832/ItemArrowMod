package random832.itemarrows;

import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ItemArrowsMod.MODID)
public class ClientEvents {
    @SubscribeEvent
    static void onInitScreen(ScreenEvent.Init.Post event) {
        if(event.getScreen() instanceof AdvancementsScreen advancementsScreen) {
            for (AdvancementTab tab : advancementsScreen.tabs.values()) {
                ComponentContents title = tab.getTitle().getContents();
                if(title.equals(new TranslatableContents("advancements.item_arrows.envelope.title"))) {
                    tab.title = Component.translatable("advancements.item_arrows.tab_title").withStyle(tab.getTitle().getStyle());
                }
            }
        }
    }
}
