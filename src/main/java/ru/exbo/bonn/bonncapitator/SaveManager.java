package ru.exbo.bonn.bonncapitator;

import java.util.HashMap;
import java.util.Random;

public class SaveManager implements CasinoCapability {
    private static class ShuffleBagSave {
        private final long seed;
        private int attempt;

        public ShuffleBagSave(long seed, int attempt) {
            this.seed = seed;
            this.attempt = attempt;
        }

        public long getSeed() {
            return seed;
        }

        public int getAttempt() {
            return attempt;
        }

        public void newAttempt() {
            this.attempt += 1;
        }
    };

    private static final HashMap<String, HashMap<String, ShuffleBagSave>> playerHandler = new HashMap<>();

    @Override
    public void resetShuffleBag(String playerId, String shuffleBagId) {
        Random rand = new Random();
        long seed = rand.nextLong();

        rand.setSeed(seed);

        ShuffleBagSave save = new ShuffleBagSave(seed, -1);

        HashMap<String, ShuffleBagSave> shuffleBag = new HashMap<>();
        shuffleBag.put(shuffleBagId, save);

        playerHandler.put(playerId, shuffleBag);
    }

    @Override
    public long getSeed(String playerId, String shuffleBagId) {
        if (!playerHandler.containsKey(playerId) || !playerHandler.get(playerId).containsKey(shuffleBagId)) {
            resetShuffleBag(playerId, shuffleBagId);
        }

        return playerHandler.get(playerId).get(shuffleBagId).getSeed();
    }

    @Override
    public int getCurrentAttempt(String playerId, String shuffleBagId) {
        return playerHandler.get(playerId).get(shuffleBagId).getAttempt();
    }

    @Override
    public void newAttempt(String playerId, String shuffleBagId) {
        playerHandler.get(playerId).get(shuffleBagId).newAttempt();
    }
}