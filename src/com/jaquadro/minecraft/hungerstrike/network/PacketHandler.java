package com.jaquadro.minecraft.hungerstrike.network;

import com.jaquadro.minecraft.hungerstrike.HungerStrike;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler
{
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
        .named(new ResourceLocation(HungerStrike.MOD_ID, "main_channel"))
        .networkProtocolVersion(() -> PROTOCOL_VERSION)
        .clientAcceptedVersions(PROTOCOL_VERSION::equals)
        .serverAcceptedVersions(PROTOCOL_VERSION::equals)
        .simpleChannel();

    public static void init() {
        INSTANCE.registerMessage(0, PacketRequestSync.class, PacketRequestSync::encode, PacketRequestSync::decode, PacketRequestSync::handle);
        INSTANCE.registerMessage(1, PacketSyncExtendedPlayer.class, PacketSyncExtendedPlayer::encode, PacketSyncExtendedPlayer::decode, PacketSyncExtendedPlayer::handle);
        INSTANCE.registerMessage(2, PacketSyncConfig.class, PacketSyncConfig::encode, PacketSyncConfig::decode, PacketSyncConfig::handle);
    }
}
