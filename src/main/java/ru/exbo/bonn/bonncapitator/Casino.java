package ru.exbo.bonn.bonncapitator;

import java.util.*;

public class Casino {
    // https://blog.bruce-hill.com/a-faster-weighted-random-choice
    // https://en.wikipedia.org/wiki/Alias_method

    public record ItemWithWeight (String id, int weight, int minAmount, int maxAmount) { }
    private record Probability (double probability, int alias) { }
    private static final HashMap<String, Probability[]> lootTables = new HashMap<>();
    private static final HashMap<String, List<ItemWithWeight>> loot = new HashMap<>();
    private static final Random random = new Random();

    static {
        ItemWithWeight poppy = new ItemWithWeight("minecraft:poppy",             40,  1, 3);
        ItemWithWeight diamond = new ItemWithWeight("minecraft:diamond",         25,  4, 6);
        ItemWithWeight stone = new ItemWithWeight("minecraft:stone",             15,  7, 10);
        ItemWithWeight sand = new ItemWithWeight("minecraft:sand",               10, 11, 14);
        ItemWithWeight cobblestone = new ItemWithWeight("minecraft:cobblestone", 10, 15, 18);

        List<ItemWithWeight> lootTable = new ArrayList<>();
        lootTable.add(poppy);
        lootTable.add(diamond);
        lootTable.add(stone);
        lootTable.add(sand);
        lootTable.add(cobblestone);

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

    private static Probability[] getLootTable(String logId) {
        if (lootTables.containsKey(logId)) {
            return lootTables.get(logId);
        }

        List<ItemWithWeight> lootTable = loot.get(logId);

        int totalWeight = 0;
        for (ItemWithWeight item : lootTable) {
            totalWeight += item.weight();
        }

        int[] alias = new int[lootTable.size()];

        double avg = (double) totalWeight / lootTable.size();

        double[] probabilities = new double[lootTable.size()];
        for (int i = 0; i < lootTable.size(); i++) {
            probabilities[i] = lootTable.get(i).weight();
        }

        Deque<Integer> small = new ArrayDeque<>();
        Deque<Integer> big = new ArrayDeque<>();

        for (int i = 0; i < lootTable.size(); ++i) {
            if (probabilities[i] >= avg)
                big.add(i);
            else
                small.add(i);

            probabilities[i] /= avg;
        }

        int less = small.removeFirst();
        int more = big.removeFirst();

        while (less != -1 && more != -1) {
            alias[less] = more;

            probabilities[more] -= (1 - probabilities[less]);
            if (probabilities[more] < 1) {
                less = more;
                if (!big.isEmpty()) {
                    more = big.removeFirst();
                }
                else {
                    more = -1;
                }
            }
            else {
                if (!small.isEmpty()) {
                    less = small.removeFirst();
                }
                else {
                    less = -1;
                }
            }
        }

        Probability[] table = new Probability[lootTable.size()];
        for (int i = 0; i < lootTable.size(); i++) {
            table[i] = new Probability(probabilities[i], alias[i]);
        }
        lootTables.put(logId, table);
        return table;
    }

    public static ItemWithWeight getRandomLoot(String logId) {
        Probability[] table = getLootTable(logId);

        double r = random.nextDouble() * table.length;
        int i = (int)r;
        Probability prob = table[i];

        int lootId = (r - i) > prob.probability() ? prob.alias() : i;
        return loot.get(logId).get(lootId);
    }

    public static int getLootAmount(ItemWithWeight item) {
        return random.nextInt(item.minAmount(), item.maxAmount());
    }
}