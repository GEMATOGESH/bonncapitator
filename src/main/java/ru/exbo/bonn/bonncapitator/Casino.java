package ru.exbo.bonn.bonncapitator;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Casino {
    private record ItemWithWeight (String id, int weight) { }
    private static final HashMap<String, Set<ItemWithWeight>> loot = new HashMap<>();

    public Casino() {
        ItemWithWeight diamond = new ItemWithWeight("minecraft:diamond", 20);
        ItemWithWeight stone = new ItemWithWeight("minecraft:stone", 80);

        Set<ItemWithWeight> lootTable = new HashSet<>();
        lootTable.add(diamond);
        lootTable.add(stone);

        loot.put("minecraft:oak_log", lootTable);
        loot.put("minecraft:spruce_log", lootTable);
        loot.put("minecraft:birch_log", lootTable);
        loot.put("minecraft:jungle_log", lootTable);
        loot.put("minecraft:acacia_log", lootTable);
        loot.put("minecraft:dark_oak_log", lootTable);
        loot.put("minecraft:mangrove_log", lootTable);
        loot.put("minecraft:cherry_log", lootTable);
        loot.put("minecraft:pale_oak_log", lootTable);
        loot.put("minecraft:crimson_stem", lootTable);
        loot.put("minecraft:warped_stem", lootTable);
    }

    public static Boolean isThereLoot(String logId) {
        return loot.containsKey(logId);
    }

    public static String getRandomLoot(String logId) {
        Set<ItemWithWeight> lootTable = loot.get(logId);

        int totalWeight = 0;
        for (ItemWithWeight item : lootTable) {
            totalWeight += item.weight();
        }

        // TODO

        return "23";
    }
}











