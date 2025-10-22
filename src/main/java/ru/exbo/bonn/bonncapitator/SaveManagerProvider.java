package ru.exbo.bonn.bonncapitator;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SaveManagerProvider implements ICapabilitySerializable<CompoundTag> {
    public static Capability<SaveManager> CASINO_SAVE = CapabilityManager.get(new CapabilityToken<SaveManager>() { });

    private SaveManager manager = null;
    private final LazyOptional<SaveManager> optional = LazyOptional.of(this::createSaveManager);

    private SaveManager createSaveManager() {
        if (this.manager == null) {
            this.manager = new SaveManager();
        }
        return this.manager;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CASINO_SAVE) {
            return optional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider registryAccess) {
        return createSaveManager().serializeNBT();
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider registryAccess, CompoundTag nbt) {
        createSaveManager().deserializeNBT(nbt);
    }
}
