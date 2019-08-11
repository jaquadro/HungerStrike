package com.jaquadro.minecraft.hungerstrike;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class ExtendedPlayerHandler
{
    @CapabilityInject(ExtendedPlayer.class)
    public static Capability<ExtendedPlayer> EXTENDED_PLAYER_CAPABILITY = null;

    public static void register () {
        CapabilityManager.INSTANCE.register(ExtendedPlayer.class, new Capability.IStorage<ExtendedPlayer>()
        {
            @Override
            public INBT writeNBT (Capability<ExtendedPlayer> capability, ExtendedPlayer instance, Direction side) {
                return new CompoundNBT();
            }

            @Override
            public void readNBT (Capability<ExtendedPlayer> capability, ExtendedPlayer instance, Direction side, INBT nbt) {

            }
        }, () -> {
            return new ExtendedPlayer(null);
        });
    }
}
