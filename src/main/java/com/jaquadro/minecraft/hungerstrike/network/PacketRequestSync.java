package com.jaquadro.minecraft.hungerstrike.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketRequestSync
{
    public static void encode(PacketRequestSync msg, FriendlyByteBuf buf) {}

    public static PacketRequestSync decode(FriendlyByteBuf buf) {
        return new PacketRequestSync();
    }

    public static void handle(PacketRequestSync message, Supplier<NetworkEvent.Context> ctx) {
        DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                PacketHandler.INSTANCE.sendTo(new PacketSyncExtendedPlayer(player), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
                PacketHandler.INSTANCE.sendTo(new PacketSyncConfig(), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
