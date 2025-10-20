package ru.exbo.bonn.bonncapitator;

import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public interface ICasinoCapability {
    void resetShuffleBag(String playerId, String shuffleBagId);
    long getSeed(String playerId, String shuffleBagId);
    int getCurrentAttempt(String playerId, String shuffleBagId);
    int newAttempt(String playerId, String shuffleBagId);
}
