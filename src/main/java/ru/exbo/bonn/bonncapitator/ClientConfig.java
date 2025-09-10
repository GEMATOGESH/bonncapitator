package ru.exbo.bonn.bonncapitator;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

public class ClientConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>>  APPLICABLE_LOGS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> APPLICABLE_LEAVES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> APPLICABLE_AXES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> APPLICABLE_SHEARS;
    public static final ForgeConfigSpec.ConfigValue<Integer> MAXIMUM_LEAF_DIST;

    static {
        BUILDER.push("Bonncapitator config file");
        APPLICABLE_LOGS = BUILDER.comment("Бревна сюда").define("APPLICABLE_LOGS", Arrays.asList(
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
        ));

        APPLICABLE_LEAVES = BUILDER.comment("Листья сюда").define("APPLICABLE_LEAVES", Arrays.asList(
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
        ));

        APPLICABLE_AXES = BUILDER.comment("Топоры сюда").define("APPLICABLE_AXES", Arrays.asList(
            "minecraft:wooden_axe",
            "minecraft:stone_axe",
            "minecraft:iron_axe",
            "minecraft:diamond_axe",
            "minecraft:golden_axe",
            "minecraft:netherite_axe"
        ));

        APPLICABLE_SHEARS = BUILDER.comment("Ножницы сюда").define("APPLICABLE_SHEARS", Arrays.asList(
            "minecraft:shears"
        ));

        MAXIMUM_LEAF_DIST = BUILDER.comment("Максимальную дистанцию листьев от бревна сюда").define("MAXIMUM_LEAF_DIST", 3);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}