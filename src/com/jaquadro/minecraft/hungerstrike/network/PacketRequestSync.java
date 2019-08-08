package com.jaquadro.minecraft.hungerstrike.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketRequestSync
{
    public static void encode(PacketRequestSync msg, PacketBuffer buf) {}

    public static PacketRequestSync decode(PacketBuffer buf) {
        return new PacketRequestSync();
    }

    public static void handle(PacketRequestSync message, Supplier<NetworkEvent.Context> ctx) {
        DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null) {
                PacketHandler.INSTANCE.sendTo(new PacketSyncExtendedPlayer(player), player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
                PacketHandler.INSTANCE.sendTo(new PacketSyncConfig(), player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
