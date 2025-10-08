package ru.exbo.bonn.bonncapitator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class ConfigManager {
    private static final String configPath = "config\\bonncapitator.json";

    private record Config(List<String> applicableLogs, List<String> applicableLeaves, List<String> applicableAxes,
                          List<String> applicableShears, int maxLeafDist, int treeHeightForCasinoActivation,
                          int casinoLooseChance, List<Casino.ItemWithWeight> casinoItems) { }
    static Config conf;

    public static void loadConfig() {
        Gson gson = new Gson();

        try {
            JsonReader reader = new JsonReader(new FileReader(configPath));
            conf = gson.fromJson(reader, Config.class);
        }
        catch (FileNotFoundException e) {
            createConfig();
        }
    }

    private static void createConfig() {
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

        conf = new Config(applicableLogs, applicableLeaves, applicableAxes, applicableShears, maxLeafDist,
                treeHeightForCasinoActivation, casinoLooseChance, casinoItems);

        try (Writer writer = new FileWriter(configPath)) {
            gson.toJson(conf, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Casino.ItemWithWeight> getCasinoItems() {
        return conf.casinoItems();
    }

    public static int getTreeHeightForCasinoActivation() {
        return conf.treeHeightForCasinoActivation();
    }

    public static int getCasinoLooseChance() {
        return conf.casinoLooseChance();
    }
    public static Integer getMaxLeafDist() {
        return conf.maxLeafDist();
    }

    public static List<String> getApplicableShears() {
        return conf.applicableShears();
    }

    public static List<String> getApplicableAxes() {
        return conf.applicableAxes();
    }

    public static List<String> getApplicableLeaves() {
        return conf.applicableLeaves();
    }

    public static List<String> getApplicableLogs() {
        return conf.applicableLogs();
    }
}
