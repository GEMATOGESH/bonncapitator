package ru.exbo.bonn.bonncapitator;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.*;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BonnCapitator.MODID)
public final class BonnCapitator {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "bonncapitator";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Random rand = new Random();

    public BonnCapitator(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, ClientConfig.SPEC);
    }

    static String getBlockName(Block blockToCheck) {
        var blockToCheckNameKey = ForgeRegistries.BLOCKS.getKey(blockToCheck);
        if (Objects.isNull(blockToCheckNameKey)) {
            return null;
        }

        return blockToCheckNameKey.toString();
    }

    static Item getLoot(String lootID) {
        String[] domainWithName = lootID.split(":");
        return ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath(domainWithName[0], domainWithName[1]));
    }

    public static Boolean isCasinoAllowed(int height) {
        return height >= ClientConfig.TREE_HEIGHT_FOR_CASINO_ACTIVATION.get();
    }

    public static Boolean isCasinoWon() {
        return ClientConfig.CASINO_LOOT_CHANCE.get() < rand.nextInt(100);
    }

    private static Boolean isAxe(String itemToCheckName) {
        return ClientConfig.APPLICABLE_AXES.get().contains(itemToCheckName);
    }

    public static Boolean isLeafTooFar(int number) {
         return number > ClientConfig.MAXIMUM_LEAF_DIST.get();
    }

    public static Boolean isLog(String blockToCheckName) {
        return ClientConfig.APPLICABLE_LOGS.get().contains(blockToCheckName);
    }

    public static Boolean isLeaf(String blockToCheckName) {
        return ClientConfig.APPLICABLE_LEAVES.get().contains(blockToCheckName);
    }

    public static Boolean isShears(String itemToCheckName) {
        return ClientConfig.APPLICABLE_SHEARS.get().contains(itemToCheckName);
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class StaticServerOnlyEventHandler {
        private static Boolean isValidSituation(Player player, Item tool, Block block) {
            // Проверка делал ли действие игрок, игнорируем если он в креативе
            if (player == null || player.isCreative()) {
                return false;
            }

            // Проверяем топор ли в основной руке
            if (!isAxe(tool.toString())) {
                return false;
            }

            // Проверяем с бревном ли взаимодействие
            if (!isLog(getBlockName(block))) {
                return false;
            }

            return true;
        }

        @SubscribeEvent
        public static void breakSpeedEvent(PlayerEvent.BreakSpeed event) {
            // Изменяем скорость поломки, если это дерево
            if (!isValidSituation(event.getEntity(), event.getEntity().getMainHandItem().getItem(),
                    event.getState().getBlock()) || event.getPosition().isEmpty())
            {
                return;
            }

            Level lvl = event.getEntity().level();
            BlockPos blockPos = event.getPosition().get();

            MaybeATree tree = new MaybeATree(lvl, blockPos);

            if (!tree.isATree()) {
                return;
            }

            int height = tree.getTreeHeight();

            float newSpeed = event.getOriginalSpeed() / height;
            event.setNewSpeed(newSpeed);
        }

        @SubscribeEvent
        public static void breakEvent(BlockEvent.BreakEvent event) {
            // Получаем блок, сломанный игроком, проверяем инструмент и бревно ли блок
            ItemStack mainTool = event.getPlayer().getMainHandItem();
            Block block = event.getState().getBlock();

            if (!isValidSituation(event.getPlayer(), mainTool.getItem(), block))
            {
                return;
            }

            // Проверяем дерево ли (есть хотя бы 1 бревно и 1 листик)
            Level lvl = event.getPlayer().level();
            BlockPos blockPos = event.getPos();

            MaybeATree tree = new MaybeATree(lvl, blockPos);

            if (!tree.isATree()) {
                return;
            }

            // Уничтожаем дерево, при этом проверяя листву
            ItemStack offHandTool = event.getPlayer().getOffhandItem();

            tree.breakATree(mainTool, offHandTool, lvl);
        }
    }
}

