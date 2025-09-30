package ru.exbo.bonn.bonncapitator;

import java.util.*;

public class Casino {
    private record ItemWithWeight (String id, int weight) { }
    private record Probability (double probability, int alias) { }
    private static final HashMap<String, Probability[]> lootTables = new HashMap<>();
    private static final HashMap<String, List<ItemWithWeight>> loot = new HashMap<>();
    private static final Random random = new Random();

    static {
        ItemWithWeight poppy = new ItemWithWeight("minecraft:poppy", 40);
        ItemWithWeight diamond = new ItemWithWeight("minecraft:diamond", 25);
        ItemWithWeight stone = new ItemWithWeight("minecraft:stone", 15);
        ItemWithWeight sand = new ItemWithWeight("minecraft:sand", 10);
        ItemWithWeight cobblestone = new ItemWithWeight("minecraft:cobblestone", 10);

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

        var lootTable = loot.get(logId);

        int totalWeight = 0;
        for (ItemWithWeight item : lootTable) {
            totalWeight += item.weight();
        }

        int[] alias = new int[lootTable.size()];
        Arrays.fill(alias, -1);

        double avg = (double) totalWeight / lootTable.size();
        ArrayList<Double> probabilities = new ArrayList<>();

        for (ItemWithWeight loot : lootTable) {
            probabilities.add((double) loot.weight());
        }

        Deque<Integer> small = new ArrayDeque<>();
        Deque<Integer> big = new ArrayDeque<>();

        for (int i = 0; i < probabilities.size(); ++i) {
            if (probabilities.get(i) >= avg)
                big.add(i);
            else
                small.add(i);

            probabilities.set(i, probabilities.get(i) / avg);
        }

        int less = small.removeFirst();
        int more = big.removeFirst();

        while (less != -1 && more != -1) {
            alias[less] = more;

            probabilities.set(more, probabilities.get(more) - (1 - probabilities.get(less)));
            if (probabilities.get(more) < 1) {
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
            table[i] = new Probability(probabilities.get(i), alias[i]);
        }
        lootTables.put(logId, table);
        return table;
    }

    public static String getRandomLoot(String logId) {
        Probability[] table = getLootTable(logId);

        double randomio = random.nextDouble() * table.length;
        int i = (int)randomio;
        Probability prob = table[i];

        int lootId = (randomio - i) > prob.probability ? prob.alias : i;
        var res = loot.get(logId).get(lootId).id();
        return res;
    }
}