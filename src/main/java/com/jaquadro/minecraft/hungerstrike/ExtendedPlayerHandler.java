package com.jaquadro.minecraft.hungerstrike;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class ExtendedPlayerHandler
{
    @CapabilityInject(ExtendedPlayer.class)
    public static Capability<ExtendedPlayer> EXTENDED_PLAYER_CAPABILITY = null;

    public static void register () {
        CapabilityManager.INSTANCE.register(ExtendedPlayer.class);
    }
}
