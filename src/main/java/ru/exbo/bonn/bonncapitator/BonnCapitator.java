package ru.exbo.bonn.bonncapitator;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Objects;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BonnCapitator.MODID)
public final class BonnCapitator {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "bonncapitator";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    // TODO: Отправить в конфиг
    private static final String[] APPLICABLE_LOGS = {
        "minecraft:oak_log",
        "minecraft:spruce_log",
        "minecraft:birch_log",
        "minecraft:jungle_log",
        "minecraft:acacia_log",
        "minecraft:dark_oak_log",
        "minecraft:mangrove_log",
        "minecraft:cherry_log",
        "minecraft:pale_oak_log",
        "minecraft:crimson_stem",
        "minecraft:warped_stem"
    };
//    // Create a Deferred Register to hold Blocks which will all be registered under the "bonncapitator" namespace
//    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
//    // Create a Deferred Register to hold Items which will all be registered under the "bonncapitator" namespace
//    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
//    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "bonncapitator" namespace
//    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Creates a new Block with the id "bonncapitator:example_block", combining the namespace and path
//    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block",
//        () -> new Block(BlockBehaviour.Properties.of()
//            .setId(BLOCKS.key("example_block"))
//            .mapColor(MapColor.STONE)
//        )
//    );
//    // Creates a new BlockItem with the id "bonncapitator:example_block", combining the namespace and path
//    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block",
//        () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties().setId(ITEMS.key("example_block")))
//    );
//
//    // Creates a new food item with the id "bonncapitator:example_id", nutrition 1 and saturation 2
//    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item",
//        () -> new Item(new Item.Properties()
//            .setId(ITEMS.key("example_item"))
//            .food(new FoodProperties.Builder()
//                .alwaysEdible()
//                .nutrition(1)
//                .saturationModifier(2f)
//                .build()
//            )
//        )
//    );

    // Creates a creative tab with the id "bonncapitator:example_tab" for the example item, that is placed after the combat tab
//    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
//            .withTabsBefore(CreativeModeTabs.COMBAT)
//            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
//            .displayItems((parameters, output) -> {
//                output.accept(EXAMPLE_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
//            }).build());

    public BonnCapitator(FMLJavaModLoadingContext context) {
        var modBusGroup = context.getModBusGroup();

        // Register the commonSetup method for modloading

//        // Register the Deferred Register to the mod event bus so blocks get registered
//        BLOCKS.register(modBusGroup);
//        // Register the Deferred Register to the mod event bus so items get registered
//        ITEMS.register(modBusGroup);
//        // Register the Deferred Register to the mod event bus so tabs get registered
//        CREATIVE_MODE_TABS.register(modBusGroup);

        // Register the item to a creative tab
//        BuildCreativeModeTabContentsEvent.getBus(modBusGroup).addListener(BonnCapitator::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    private static String getBlockName(Block blockToCheck) {
        var blockToCheckNameKey = ForgeRegistries.BLOCKS.getKey(blockToCheck);
        if (Objects.isNull(blockToCheckNameKey)) {
            return null;
        }

        return blockToCheckNameKey.toString();
    }

    private static Boolean isLog(String blockToCheckName) {
        return Arrays.asList(APPLICABLE_LOGS).contains(blockToCheckName);
    }

    // TODO: Только если у игрока в руках топор; Подумать про оптимизацию рекурсии; Задефайнить дерево
    private static void treeDestructor(Level lvl, BlockPos blockPos, String blockName) {
        // Из-за диагональных деревьев - выебываемся и смотрим все окружающие блоки
        Vec3i[] search_box = {
                new Vec3i(-1, 1, -1),
                new Vec3i(-1, 1, 0),
                new Vec3i(-1, 1, 1),
                new Vec3i(-1, 0, -1),
                new Vec3i(-1, 0, 0),
                new Vec3i(-1, 0, 1),
                new Vec3i(-1, -1, -1),
                new Vec3i(-1, -1, 0),
                new Vec3i(-1, -1, 1),
                new Vec3i(0, 1, -1),
                new Vec3i(0, 1, 0),
                new Vec3i(0, 1, 1),
                new Vec3i(0, 0, -1),
                new Vec3i(0, 0, 1),
                new Vec3i(0, -1, -1),
                new Vec3i(0, -1, 0),
                new Vec3i(0, -1, 1),
                new Vec3i(1, 1, -1),
                new Vec3i(1, 1, 0),
                new Vec3i(1, 1, 1),
                new Vec3i(1, 0, -1),
                new Vec3i(1, 0, 0),
                new Vec3i(1, 0, 1),
                new Vec3i(1, -1, -1),
                new Vec3i(1, -1, 0),
                new Vec3i(1, -1, 1)
        };

        for (Vec3i relative_position : search_box) {
            BlockPos blockToCheckPos = blockPos.offset(relative_position);
            var blockToCheck = lvl.getBlockState(blockToCheckPos).getBlock();

            if (isLog(getBlockName(blockToCheck))) {
                lvl.destroyBlock(blockToCheckPos, true);
                treeDestructor(lvl, blockToCheckPos, blockName);
            }
        }
    }

    // Add the example block item to the building blocks tab
//    private static void addCreative(BuildCreativeModeTabContentsEvent event) {
//        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
//            event.accept(EXAMPLE_BLOCK_ITEM);
//    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
//    @Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
//    public static class ClientModEvents {
//        @SubscribeEvent
//        public static void onClientSetup(FMLClientSetupEvent event) {
//            // Some client setup code
//            LOGGER.info("HELLO FROM CLIENT SETUP");
//            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
//        }
//    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class StaticServerOnlyEventHandler {
        @SubscribeEvent
        public static void blockBreakEvent(BlockEvent.BreakEvent event) {
            // Получаем блок сломанный игроком, если это бревно - отправляем на уничтожение все дерево
            Level lvl = event.getPlayer().level();
            var blockPos = event.getPos();
            var blockName = getBlockName(event.getState().getBlock());

            if (isLog(blockName)) {
                treeDestructor(lvl, blockPos, blockName);
            }
        }
    }
}
