package com.jaquadro.minecraft.hungerstrike;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExtendedPlayerProvider implements ICapabilityProvider, INBTSerializable<CompoundNBT> {
    private final ExtendedPlayer extendedPlayer;
    private LazyOptional<?> playerHandler;

    public ExtendedPlayerProvider(PlayerEntity player) {
        extendedPlayer = new ExtendedPlayer(player);
        playerHandler = LazyOptional.of(() -> extendedPlayer);
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> capability, final @Nullable Direction facing) {
        return capability == ExtendedPlayerHandler.EXTENDED_PLAYER_CAPABILITY ? playerHandler.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = new CompoundNBT();
        extendedPlayer.saveNBTData(compound);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        extendedPlayer.loadNBTData(nbt);
    }
}
