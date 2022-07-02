package random832.itemarrows;

import com.mojang.logging.LogUtils;
import io.netty.util.Attribute;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(ItemArrows.MODID)
public class ItemArrows
{
    public static final String MODID = "itemarrows";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);

    public static final RegistryObject<Item> ITEM_ARROW_ITEM = ITEMS.register("item_arrow", () -> new ItemArrowItem(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS)));
    public static final RegistryObject<EntityType<ItemArrow>> ITEM_ARROW_ENTITY = ENTITIES.register("item_arrow", () -> EntityType.Builder.<ItemArrow>of(ItemArrow::new, MobCategory.MISC).build("item_arrow"));

    public ItemArrows()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::gatherData);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        ENTITIES.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(new Events());
    }

    void commonSetup(final FMLCommonSetupEvent event)
    {
    }

    void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(event.includeServer(),  new ItemTagProvider(generator, Registry.ITEM, event.getExistingFileHelper()));
    }
}
