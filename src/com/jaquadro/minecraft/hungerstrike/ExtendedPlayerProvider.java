package com.jaquadro.minecraft.hungerstrike;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Created by asie on 4/29/16.
 */
public class ExtendedPlayerProvider implements ICapabilityProvider, INBTSerializable<NBTTagCompound> {
    private final ExtendedPlayer extendedPlayer;

    public ExtendedPlayerProvider(EntityPlayer player) {
        extendedPlayer = new ExtendedPlayer(player);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == ExtendedPlayer.EXTENDED_PLAYER_CAPABILITY;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return capability == ExtendedPlayer.EXTENDED_PLAYER_CAPABILITY ? (T) extendedPlayer : (T) null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        extendedPlayer.saveNBTData(compound);
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        extendedPlayer.loadNBTData(nbt);
    }
}
