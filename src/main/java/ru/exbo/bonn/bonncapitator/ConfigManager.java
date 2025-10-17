package ru.exbo.bonn.bonncapitator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;


public class ConfigManager {
    private static final String mainConfigPath = "config\\bonncapitator-main.json";
    private static final String shuffleConfigPath = "config\\bonncapitator-shuffle-bags.json";
    private static final String fillersConfigPath = "config\\bonncapitator-fillers.json";

    private record MainConfig(List<String> applicableLogs, List<String> applicableLeaves, List<String> applicableAxes,
                              List<String> applicableShears, int maxLeafDist, int treeHeightForCasinoActivation,
                              int casinoLooseChance, List<Casino.ItemWithWeight> casinoItems) { }

    private record ShuffleConfig(Hashtable<String, ShuffleBagItem[]> shuffleBags) { }

    private record ShuffleBagItem(@Nullable Integer amount, @Nullable Stack stack,
                                  @Nullable String loot, @Nullable String bag) { }
    private record Stack(String id, int stackSize, @Nullable Double weight) { }

    static MainConfig mainConfig;
    static ShuffleConfig shuffleConfig;
    static ShuffleConfig fillersConfig;

    public static void loadConfig() {
        Gson gson = new Gson();

        try {
            JsonReader reader = new JsonReader(new FileReader(mainConfigPath));
            mainConfig = gson.fromJson(reader, MainConfig.class);
        }
        catch (FileNotFoundException e) {
            createMainConfig();
        }

        try {
            JsonReader reader = new JsonReader(new FileReader(shuffleConfigPath));
            shuffleConfig = gson.fromJson(reader, ShuffleConfig.class);
        }
        catch (FileNotFoundException e) {
            createShuffleConfig();
        }

        try {
            JsonReader reader = new JsonReader(new FileReader(fillersConfigPath));
            fillersConfig = gson.fromJson(reader, ShuffleConfig.class);
        }
        catch (FileNotFoundException e) {
            createFillersConfig();
        }
    }

    private static void createMainConfig() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        List<String> applicableLogs = Arrays.asList(
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
        );

        List<String> applicableLeaves = Arrays.asList(
                "minecraft:oak_leaves",
                "minecraft:spruce_leaves",
                "minecraft:birch_leaves",
                "minecraft:jungle_leaves",
                "minecraft:acacia_leaves",
                "minecraft:dark_oak_leaves",
                "minecraft:mangrove_leaves",
                "minecraft:cherry_leaves",
                "minecraft:pale_oak_leaves",
                "minecraft:azalea_leaves",
                "minecraft:flowering_azalea_leaves"
        );

        List<String> applicableAxes = Arrays.asList(
                "minecraft:wooden_axe",
                "minecraft:stone_axe",
                "minecraft:iron_axe",
                "minecraft:diamond_axe",
                "minecraft:golden_axe",
                "minecraft:netherite_axe"
        );

        List<String> applicableShears = List.of(
                "minecraft:shears"
        );

        int maxLeafDist = 3;
        int treeHeightForCasinoActivation = 3;
        int casinoLooseChance = 0;

        List<Casino.ItemWithWeight> casinoItems = Arrays.asList(
                new Casino.ItemWithWeight("minecraft:diamond", 80, 1, 5),
                new Casino.ItemWithWeight("minecraft:coal", 20, 10, 50)
        );

        mainConfig = new MainConfig(applicableLogs, applicableLeaves, applicableAxes, applicableShears, maxLeafDist,
                treeHeightForCasinoActivation, casinoLooseChance, casinoItems);

        try (Writer writer = new FileWriter(mainConfigPath)) {
            gson.toJson(mainConfig, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createShuffleConfig() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        ShuffleBagItem gold = new ShuffleBagItem(50, new Stack("minecraft:gold_ingot", 1, null),
                null, null);
        ShuffleBagItem copper = new ShuffleBagItem(50, new Stack("minecraft:copper_ingot", 2, null),
                null, null);
        ShuffleBagItem iron = new ShuffleBagItem(10, new Stack("minecraft:iron_ingot", 3, null),
                null, null);

        ShuffleBagItem fillers = new ShuffleBagItem(1, null, "fillers", null);

        ShuffleBagItem bag = new ShuffleBagItem(1, null, null, "bag_rare");

        ShuffleBagItem[] example = {
                gold,
                copper,
                iron,
                fillers,
                bag
        };

        Hashtable<String, ShuffleBagItem[]> shuffle_bags = new Hashtable<>();
        shuffle_bags.put("example_bag", example);

        shuffleConfig = new ShuffleConfig(shuffle_bags);

        try (Writer writer = new FileWriter(shuffleConfigPath)) {
            gson.toJson(shuffleConfig, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createFillersConfig() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        ShuffleBagItem apple = new ShuffleBagItem(null, new Stack("minecraft:apple", 7, 0.3),
                null, null);
        ShuffleBagItem stick = new ShuffleBagItem(null, new Stack("minecraft:stick", 8, 0.6),
                null, null);
        ShuffleBagItem kelp = new ShuffleBagItem(null, new Stack("minecraft:kelp", 9, 0.1),
                null, null);

        ShuffleBagItem[] fillers = {
                apple,
                stick,
                kelp
        };

        Hashtable<String, ShuffleBagItem[]> casino_fillers = new Hashtable<>();
        casino_fillers.put("casino_fillers", fillers);

        fillersConfig = new ShuffleConfig(casino_fillers);

        try (Writer writer = new FileWriter(fillersConfigPath)) {
            gson.toJson(fillersConfig, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Casino.ItemWithWeight> getCasinoItems() {
        return mainConfig.casinoItems();
    }

    public static int getTreeHeightForCasinoActivation() {
        return mainConfig.treeHeightForCasinoActivation();
    }

    public static int getCasinoLooseChance() {
        return mainConfig.casinoLooseChance();
    }
    public static Integer getMaxLeafDist() {
        return mainConfig.maxLeafDist();
    }

    public static List<String> getApplicableShears() {
        return mainConfig.applicableShears();
    }

    public static List<String> getApplicableAxes() {
        return mainConfig.applicableAxes();
    }

    public static List<String> getApplicableLeaves() {
        return mainConfig.applicableLeaves();
    }

    public static List<String> getApplicableLogs() {
        return mainConfig.applicableLogs();
    }
}
