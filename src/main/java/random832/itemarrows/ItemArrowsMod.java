package random832.itemarrows;

import com.mojang.logging.LogUtils;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import random832.itemarrows.crafting.ArrowRecipe;
import random832.itemarrows.crafting.EnvelopeRecipe;
import random832.itemarrows.datagen.EnglishLanguageProvider;
import random832.itemarrows.datagen.ItemTagProvider;
import random832.itemarrows.datagen.ModRecipeProvider;
import random832.itemarrows.items.EnvelopeItem;
import random832.itemarrows.items.ItemArrowItem;

@Mod(ItemArrowsMod.MODID)
public class ItemArrowsMod
{
    public static final String MODID = "itemarrows";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

    private static final Item.Properties props = new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE);
    public static final RegistryObject<Item> ITEM_ARROW_ITEM = ITEMS.register("item_arrow", () -> new ItemArrowItem(props));
    public static final RegistryObject<Item> ENVELOPE_ITEM = ITEMS.register("envelope", () -> new EnvelopeItem(props));
    public static final RegistryObject<EntityType<ItemArrow>> ITEM_ARROW_ENTITY = ENTITIES.register("item_arrow", () -> EntityType.Builder.<ItemArrow>of(ItemArrow::new, MobCategory.MISC).build("item_arrow"));
    public static final RegistryObject<SimpleRecipeSerializer<?>> RS_ARROW = RECIPES.register("attach_arrow", () -> new SimpleRecipeSerializer<>(ArrowRecipe::new));
    public static final RegistryObject<SimpleRecipeSerializer<?>> RS_ENVELOPE = RECIPES.register("fill_envelope", () -> new SimpleRecipeSerializer<>(EnvelopeRecipe::new));

    public ItemArrowsMod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::gatherData);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        ENTITIES.register(modEventBus);
        RECIPES.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(new Events());
    }

    void commonSetup(final FMLCommonSetupEvent event)
    {
        DispenserBlock.registerBehavior(ITEM_ARROW_ITEM.get(), new ItemArrowDispenseBehavior());
    }

    void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(event.includeServer(), new ItemTagProvider(generator, Registry.ITEM, event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new ModRecipeProvider(generator));
        generator.addProvider(event.includeClient(), new EnglishLanguageProvider(generator));
    }
}
