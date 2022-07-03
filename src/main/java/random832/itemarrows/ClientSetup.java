package random832.itemarrows;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.TippableArrowRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = ItemArrowsMod.MODID)
public class ClientSetup {
    @SubscribeEvent
    static void registerRenderers(EntityRenderersEvent.RegisterRenderers e) {
        e.registerEntityRenderer(ItemArrowsMod.ITEM_ARROW_ENTITY.get(), c -> new ArrowRenderer<ItemArrow>(c) {
            @Override
            public ResourceLocation getTextureLocation(ItemArrow arrow) {
                return TippableArrowRenderer.NORMAL_ARROW_LOCATION;
            }
        });
    }
}