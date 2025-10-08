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

    public static void reloadCasino() {
        List<Casino.ItemWithWeight> items = BonnCapitator.getCasinoItems();
        String[] logs = BonnCapitator.getLogs();

        for (String log: logs) {
            loot.put(log, items);
        }
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