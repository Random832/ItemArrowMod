package random832.itemarrows;

import com.mojang.logging.LogUtils;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import random832.itemarrows.blocks.*;
import random832.itemarrows.blocks.crafters.ArrowCrafterBlock;
import random832.itemarrows.blocks.crafters.ArrowCrafterBlockEntity;
import random832.itemarrows.blocks.crafters.EnvelopeCrafterBlock;
import random832.itemarrows.blocks.crafters.EnvelopeCrafterBlockEntity;
import random832.itemarrows.blocks.dispenser.*;
import random832.itemarrows.crafting.ArrowRecipe;
import random832.itemarrows.crafting.EnvelopeRecipe;
import random832.itemarrows.datagen.*;
import random832.itemarrows.entities.ArrowItemEntity;
import random832.itemarrows.entities.ItemArrow;
import random832.itemarrows.entities.PreciseBluntArrow;
import random832.itemarrows.blocks.crafters.CrafterMenu;
import random832.itemarrows.items.EnvelopeItem;
import random832.itemarrows.items.ItemArrowItem;
import random832.itemarrows.items.CalibrationArrowItem;
import random832.itemarrows.items.RemoteItem;
import random832.itemarrows.network.ModPackets;

@Mod(ItemArrowsMod.MODID)
public class ItemArrowsMod
{
    public static final String MODID = "itemarrows";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);

    private static final Item.Properties props = new Item.Properties().tab(new CreativeModeTab("item_arrows") {
        @Override
        public ItemStack makeIcon() {
            return ENVELOPE_ITEM.get().getDefaultInstance();
        }
    });
    public static final RegistryObject<Item> ITEM_ARROW_ITEM = ITEMS.register("item_arrow", () -> new ItemArrowItem(props));
    public static final RegistryObject<Item> PRECISE_ARROW_ITEM = ITEMS.register("calibration_arrow", () -> new CalibrationArrowItem(props));
    public static final RegistryObject<Item> ENVELOPE_ITEM = ITEMS.register("envelope", () -> new EnvelopeItem(props));
    public static final RegistryObject<EntityType<PreciseBluntArrow>> PRECISE_BLUNT_ARROW = ENTITIES.register("calibration_arrow", () -> EntityType.Builder.<PreciseBluntArrow>of(PreciseBluntArrow::new, MobCategory.MISC).build("calibration_arrow"));;
    public static final RegistryObject<EntityType<ItemArrow>> ITEM_ARROW_ENTITY = ENTITIES.register("item_arrow", () -> EntityType.Builder.<ItemArrow>of(ItemArrow::new, MobCategory.MISC).build("item_arrow"));
    public static final RegistryObject<EntityType<ArrowItemEntity>> ARROW_ITEM_ENTITY = ENTITIES.register("arrow_item", () -> EntityType.Builder.<ArrowItemEntity>of(ArrowItemEntity::new, MobCategory.MISC).build("arrow_item"));;
    public static final RegistryObject<SimpleRecipeSerializer<?>> ARROW_RS = RECIPES.register("attach_arrow", () -> new SimpleRecipeSerializer<>(ArrowRecipe::new));
    public static final RegistryObject<SimpleRecipeSerializer<?>> ENVELOPE_RS = RECIPES.register("fill_envelope", () -> new SimpleRecipeSerializer<>(EnvelopeRecipe::new));
    public static final RegistryObject<Block> DISPENSER_BLOCK = BLOCKS.register("advanced_dispenser", () -> new AdvancedDispenserBlock(Block.Properties.copy(Blocks.DISPENSER).noOcclusion()));
    public static final RegistryObject<Block> COLLECTOR_BLOCK = BLOCKS.register("arrow_collector", () -> new ArrowCollectorBlock(Block.Properties.copy(Blocks.TARGET)));
    private static final RegistryObject<BlockItem> DISPENSER_ITEM = ITEMS.register("advanced_dispenser", () -> new AdvancedDispenserItem(DISPENSER_BLOCK.get(), props));
    private static final RegistryObject<BlockItem> COLLECTOR_ITEM = ITEMS.register("arrow_collector", () -> new BlockItem(COLLECTOR_BLOCK.get(), props));
    public static final RegistryObject<Item> REMOTE = ITEMS.register("dispenser_remote", () -> new RemoteItem(props));
    public static final RegistryObject<BlockEntityType<AdvancedDispenserBlockEntity>> DISPENSER_BE = BLOCK_ENTITIES.register("advanced_dispenser", () -> BlockEntityType.Builder.of(AdvancedDispenserBlockEntity::new, DISPENSER_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<ArrowCollectorBlockEntity>> COLLECTOR_BE = BLOCK_ENTITIES.register("arrow_collector", () -> BlockEntityType.Builder.of(ArrowCollectorBlockEntity::new, COLLECTOR_BLOCK.get()).build(null));
    public static final RegistryObject<MenuType<AdvancedDispenserMenu>> DISPENSER_MENU = MENUS.register("advanced_dispenser", () -> IForgeMenuType.create(AdvancedDispenserMenu::new));

    public static final RegistryObject<Block> ARROW_CRAFTER_BLOCK = BLOCKS.register("arrow_crafter", () -> new ArrowCrafterBlock(Block.Properties.copy(Blocks.CRAFTING_TABLE)));
    public static final RegistryObject<BlockEntityType<ArrowCrafterBlockEntity>> ARROW_CRAFTER_BE = BLOCK_ENTITIES.register("arrow_crafter", () -> BlockEntityType.Builder.of(ArrowCrafterBlockEntity::new, ARROW_CRAFTER_BLOCK.get()).build(null));;
    public static final RegistryObject<Block> ENVELOPE_CRAFTER_BLOCK = BLOCKS.register("envelope_crafter", () -> new EnvelopeCrafterBlock(Block.Properties.copy(Blocks.CRAFTING_TABLE)));
    public static final RegistryObject<BlockEntityType<EnvelopeCrafterBlockEntity>> ENVELOPE_STUFFER_BE = BLOCK_ENTITIES.register("envelope_crafter", () -> BlockEntityType.Builder.of(EnvelopeCrafterBlockEntity::new, ENVELOPE_CRAFTER_BLOCK.get()).build(null));;
    private static final RegistryObject<BlockItem> ARROW_CRAFTER_ITEM = ITEMS.register("arrow_crafter", () -> new BlockItem(ARROW_CRAFTER_BLOCK.get(), props));
    private static final RegistryObject<BlockItem> ENVELOPE_CRAFTER_ITEM = ITEMS.register("envelope_crafter", () -> new BlockItem(ENVELOPE_CRAFTER_BLOCK.get(), props));
    public static final RegistryObject<MenuType<CrafterMenu>> ARROW_CRAFTER_MENU = MENUS.register("arrow_crafter", () -> IForgeMenuType.create(CrafterMenu::createArrowMenu));
    public static final RegistryObject<MenuType<CrafterMenu>> ENVELOPE_CRAFTER_MENU = MENUS.register("envelope_crafter", () -> IForgeMenuType.create(CrafterMenu::createEnvelopeMenu));


    public ItemArrowsMod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::gatherData);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        ENTITIES.register(modEventBus);
        RECIPES.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        MENUS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(new Events());
        ModPackets.registerMessages();
    }

    void commonSetup(final FMLCommonSetupEvent event)
    {
        DispenserBlock.registerBehavior(ITEM_ARROW_ITEM.get(), new ItemArrowDispenseBehavior());
        DispenserBlock.registerBehavior(PRECISE_ARROW_ITEM.get(), new CalibrationArrowDispenseBehavior());
        EntityItemBehaviors.bootstrap();
    }

    void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        generator.addProvider(event.includeServer(), new ItemTagProvider(generator, Registry.ITEM, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModRecipeProvider(generator));
        generator.addProvider(event.includeServer(), new ModAdvancementProvider(generator, existingFileHelper));
        generator.addProvider(event.includeClient(), new EnglishLanguageProvider(generator));
        generator.addProvider(event.includeClient(), new ModBlockStateProvider(generator, existingFileHelper));
        generator.addProvider(event.includeClient(), new ModItemModelProvider(generator, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModLootProvider(generator));
    }
}
