package com.jaquadro.minecraft.hungerstrike;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import java.util.concurrent.Callable;

public class ExtendedPlayerHandler
{
    @CapabilityInject(ExtendedPlayer.class)
    public static Capability<ExtendedPlayer> EXTENDED_PLAYER_CAPABILITY = null;

    public static void register () {
        CapabilityManager.INSTANCE.register(ExtendedPlayer.class, new Capability.IStorage<ExtendedPlayer>()
        {
            @Override
            public NBTBase writeNBT (Capability<ExtendedPlayer> capability, ExtendedPlayer instance, EnumFacing side) {
                return new NBTTagCompound();
            }

            @Override
            public void readNBT (Capability<ExtendedPlayer> capability, ExtendedPlayer instance, EnumFacing side, NBTBase nbt) {

            }
        }, new Callable<ExtendedPlayer>()
        {
            @Override
            public ExtendedPlayer call () throws Exception {
                return new ExtendedPlayer(null);
            }
        });
    }
}
