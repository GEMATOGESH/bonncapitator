package ru.exbo.bonn.bonncapitator;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.*;


public class ConfigManager {
    private static final String mainConfigPath = "config\\bonncapitator-main.json";
    private static final String shuffleConfigPath = "config\\bonncapitator-shuffle-bags.json";
    private static final String fillersConfigPath = "config\\bonncapitator-fillers.json";

    private record MainConfig(List<String> applicableLogs, List<String> applicableLeaves, List<String> applicableAxes,
                              List<String> applicableShears, int maxLeafDist, int treeHeightForCasinoActivation,
                              int casinoLooseChance, HashMap<String, String> casinoItems) { }


    static MainConfig mainConfig;
    static HashMap<String, Casino.ShuffleBagItem[]> shuffleConfig;
    static HashMap<String, Casino.ShuffleBagItem[]> fillersConfig;

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
            TypeToken<HashMap<String, Casino.ShuffleBagItem[]>> mapType = new TypeToken<>() { };
            shuffleConfig = gson.fromJson(reader, mapType.getType());
        }
        catch (FileNotFoundException e) {
            createShuffleConfig();
        }

        try {
            JsonReader reader = new JsonReader(new FileReader(fillersConfigPath));
            TypeToken<HashMap<String, Casino.ShuffleBagItem[]>> mapType = new TypeToken<>() { };
            fillersConfig = gson.fromJson(reader, mapType.getType());
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

        HashMap<String, String> casinoItems = new HashMap<>();
        casinoItems.put("minecraft:oak_log", "example_bag");
        casinoItems.put("minecraft:spruce_log", "example_bag");
        casinoItems.put("minecraft:birch_log", "example_bag");
        casinoItems.put("minecraft:jungle_log", "example_bag");
        casinoItems.put("minecraft:acacia_log", "example_bag");
        casinoItems.put("minecraft:dark_oak_log", "example_bag");
        casinoItems.put("minecraft:mangrove_log", "example_bag");
        casinoItems.put("minecraft:cherry_log", "example_bag");
        casinoItems.put("minecraft:pale_oak_log", "example_bag");
        casinoItems.put("minecraft:crimson_stem", "example_bag");

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

        Casino.ShuffleBagItem gold = new Casino.ShuffleBagItem(50, new Casino.Stack("minecraft:gold_ingot", 1, null),
                null, null);
        Casino.ShuffleBagItem copper = new Casino.ShuffleBagItem(50, new Casino.Stack("minecraft:copper_ingot", 2, null),
                null, null);
        Casino.ShuffleBagItem iron = new Casino.ShuffleBagItem(10, new Casino.Stack("minecraft:iron_ingot", 3, null),
                null, null);
        Casino.ShuffleBagItem diamond = new Casino.ShuffleBagItem(7, new Casino.Stack("minecraft:diamond", 1, null),
                null, null);
        Casino.ShuffleBagItem netherite = new Casino.ShuffleBagItem(2, new Casino.Stack("minecraft:netherite_ingot", 1, null),
                null, null);
        Casino.ShuffleBagItem bedrock = new Casino.ShuffleBagItem(1, new Casino.Stack("minecraft:bedrock", 1, null),
                null, null);

        Casino.ShuffleBagItem fillers = new Casino.ShuffleBagItem(89, null, "fillers", null);

        Casino.ShuffleBagItem bag = new Casino.ShuffleBagItem(1, null, null, "bag_rare");

        Casino.ShuffleBagItem[] example = {
                gold,
                copper,
                iron,
                fillers,
                bag
        };

        Casino.ShuffleBagItem[] rare = {
                diamond,
                netherite,
                bedrock
        };

        shuffleConfig = new HashMap<>();
        shuffleConfig.put("bag_default", example);
        shuffleConfig.put("bag_rare", rare);

        try (Writer writer = new FileWriter(shuffleConfigPath)) {
            gson.toJson(shuffleConfig, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createFillersConfig() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Casino.ShuffleBagItem apple = new Casino.ShuffleBagItem(null, new Casino.Stack("minecraft:apple", 7, 0.3),
                null, null);
        Casino.ShuffleBagItem stick = new Casino.ShuffleBagItem(null, new Casino.Stack("minecraft:stick", 8, 0.6),
                null, null);
        Casino.ShuffleBagItem kelp = new Casino.ShuffleBagItem(null, new Casino.Stack("minecraft:kelp", 9, 0.1),
                null, null);

        Casino.ShuffleBagItem[] fillers = {
                apple,
                stick,
                kelp
        };

        fillersConfig = new HashMap<>();
        fillersConfig.put("casino_fillers", fillers);

        try (Writer writer = new FileWriter(fillersConfigPath)) {
            gson.toJson(fillersConfig, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public static String getShuffleBagName(String logId) {
        return mainConfig.casinoItems.get(logId);
    }

    public static Casino.ShuffleBagItem[] getShuffleBagItems(String shuffleBagId) {
        return shuffleConfig.get(shuffleBagId);
    }

    public static Casino.ShuffleBagItem[] getFillerBagItems(String fillerBagId) {
        return fillersConfig.get(fillerBagId);
    }
}
