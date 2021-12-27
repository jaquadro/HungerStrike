package com.jaquadro.minecraft.hungerstrike;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class ExtendedPlayerHandler
{
    public static Capability<ExtendedPlayer> EXTENDED_PLAYER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public static void register (RegisterCapabilitiesEvent event) {
        event.register(ExtendedPlayer.class);
    }
}
