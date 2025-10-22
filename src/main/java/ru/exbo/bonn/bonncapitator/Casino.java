package ru.exbo.bonn.bonncapitator;

import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.*;

public class Casino {
    private record Probability (double probability, int alias) { }
    private static final HashMap<String, Probability[]> lootTable = new HashMap<>();

    // Не уверен насколько хорошая идея делать их публичными. Будто бы тогда лучше просто голыми отдельными классами хранить
    public record ShuffleBagItem(@Nullable Integer amount, @Nullable Stack stack,
                                  @Nullable String loot, @Nullable String bag) { }
    public record Stack(String id, int stackSize, @Nullable Double weight) { }

    private static Probability[] getLootTable(String shuffleBagId, ShuffleBagItem[] shuffleItems) {
        // https://blog.bruce-hill.com/a-faster-weighted-random-choice
        // https://en.wikipedia.org/wiki/Alias_method

        if (lootTable.containsKey(shuffleBagId)) {
            return lootTable.get(shuffleBagId);
        }

        double totalWeight = 0;

        List<Stack> items = new ArrayList<>();
        for (ShuffleBagItem item : shuffleItems) {
            items.add(item.stack());
        }

        for (Stack item : items) {
            totalWeight += item.weight();
        }

        int[] alias = new int[items.size()];

        double avg = totalWeight / items.size();

        double[] probabilities = new double[items.size()];
        for (int i = 0; i < items.size(); i++) {
            probabilities[i] = items.get(i).weight();
        }

        Deque<Integer> small = new ArrayDeque<>();
        Deque<Integer> big = new ArrayDeque<>();

        for (int i = 0; i < items.size(); ++i) {
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

        Probability[] table = new Probability[items.size()];
        for (int i = 0; i < items.size(); i++) {
            table[i] = new Probability(probabilities[i], alias[i]);
        }

        lootTable.put(shuffleBagId, table);
        return table;
    }

    private static void shuffleArray(List<ShuffleBagItem> bag, Random random)
    {
        int index;
        ShuffleBagItem temp;
        for (int i = bag.size() - 1; i > 0; i--)
        {
            index = random.nextInt(i + 1);
            temp = bag.get(index);
            bag.set(index, bag.get(i));
            bag.set(i, temp);
        }
    }

    public static Stack getRandomLoot(Player player, String shuffleBagId) {
        ShuffleBagItem[] items = ConfigManager.getShuffleBagItems(shuffleBagId);

        List<ShuffleBagItem> bag = new LinkedList<>();
        for (ShuffleBagItem item : items) {
            int copies = 1;
            if (item.amount != null) {
                copies = item.amount;
            }

            for (int i = 0; i < copies; i++) {
                bag.add(item);
            }
        }

        String playerId = player.getStringUUID();
        SaveManager sm = player.getCapability(SaveManagerProvider.CASINO_SAVE).resolve().get();

        long seed = sm.getSeed(playerId, shuffleBagId);

        Random random = new Random(seed);
        shuffleArray(bag, random);

        if (sm.getCurrentAttempt(playerId, shuffleBagId) + 1 > bag.size()) {
            sm.resetShuffleBag(playerId, shuffleBagId);
        }

        int prizeIndex = sm.newAttempt(playerId, shuffleBagId);

        ShuffleBagItem prize = bag.get(prizeIndex);

        if (prize.stack() != null) {
            return prize.stack();
        }

        if (prize.bag() != null) {
            return getRandomLoot(player, prize.bag());
        }

        ShuffleBagItem[] fillers = ConfigManager.getFillerBagItems(prize.loot());
        Probability[] table = getLootTable(shuffleBagId, fillers);

        Random coin = new Random();
        double r = coin.nextDouble() * table.length;
        int i = (int)r;
        Probability prob = table[i];

        int lootId = (r - i) > prob.probability() ? prob.alias() : i;
        return fillers[lootId].stack();
    }
}