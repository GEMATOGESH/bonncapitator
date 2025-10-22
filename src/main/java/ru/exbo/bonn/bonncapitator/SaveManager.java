package ru.exbo.bonn.bonncapitator;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

import java.util.HashMap;
import java.util.Random;

@AutoRegisterCapability
public class SaveManager {
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

        public int newAttempt() {
            this.attempt += 1;
            return this.attempt;
        }
    }

    private HashMap<String, HashMap<String, ShuffleBagSave>> playerHandler = new HashMap<>();

    public void resetShuffleBag(String playerId, String shuffleBagId) {
        Random rand = new Random();
        long seed = rand.nextLong();

        rand.setSeed(seed);

        ShuffleBagSave save = new ShuffleBagSave(seed, -1);

        HashMap<String, ShuffleBagSave> shuffleBag;
        if (playerHandler.containsKey(playerId)) {
            shuffleBag = playerHandler.get(playerId);
        }
        else {
            shuffleBag = new HashMap<>();
        }

        shuffleBag.put(shuffleBagId, save);

        if (!playerHandler.containsKey(playerId)) {
            playerHandler.put(playerId, shuffleBag);
        }
    }

    public long getSeed(String playerId, String shuffleBagId) {
        if (!playerHandler.containsKey(playerId) || !playerHandler.get(playerId).containsKey(shuffleBagId)) {
            resetShuffleBag(playerId, shuffleBagId);
        }

        return playerHandler.get(playerId).get(shuffleBagId).getSeed();
    }

    public int getCurrentAttempt(String playerId, String shuffleBagId) {
        if (!playerHandler.containsKey(playerId) || !playerHandler.get(playerId).containsKey(shuffleBagId)) {
            resetShuffleBag(playerId, shuffleBagId);
        }

        return playerHandler.get(playerId).get(shuffleBagId).getAttempt() + 1;
    }

    public int newAttempt(String playerId, String shuffleBagId) {
        if (!playerHandler.containsKey(playerId) || !playerHandler.get(playerId).containsKey(shuffleBagId)) {
            resetShuffleBag(playerId, shuffleBagId);
        }

        return playerHandler.get(playerId).get(shuffleBagId).newAttempt();
    }

    public void copyFrom(SaveManager old) {
        this.playerHandler = old.playerHandler;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        Gson gson = new Gson();
        String ser = gson.toJson(playerHandler);

        tag.putString("playerHandler", ser);
        return tag;
    }

    public void deserializeNBT(Tag nbt) {
        if (nbt.asCompound().isPresent()) {
            String res = nbt.asCompound().get().getString("playerHandler").get();

            Gson gson = new Gson();
            TypeToken<HashMap<String, HashMap<String, ShuffleBagSave>>> mapType = new TypeToken<>() { };
            playerHandler = gson.fromJson(res, mapType.getType());
        }
    }
}