package com.jaquadro.minecraft.hungerstrike.network;

import com.jaquadro.minecraft.hungerstrike.ExtendedPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSyncExtendedPlayer
{
    private boolean hungerStrikeEnabled;

    public static void encode(PacketSyncExtendedPlayer msg, PacketBuffer buf) {
        buf.writeBoolean(msg.hungerStrikeEnabled);
    }

    public static PacketSyncExtendedPlayer decode(PacketBuffer buf) {
        return new PacketSyncExtendedPlayer(buf.readBoolean());
    }

    private PacketSyncExtendedPlayer(boolean hungerStrikeEnabled) {
        this.hungerStrikeEnabled = hungerStrikeEnabled;
    }

    public PacketSyncExtendedPlayer(PlayerEntity player) {
        this(getHungerStrikeFromPlayer(player));
    }

    private static boolean getHungerStrikeFromPlayer(PlayerEntity player) {
        ExtendedPlayer ep = ExtendedPlayer.get(player);
        return (ep != null) && ep.isOnHungerStrike();
    }

    public static void handle(PacketSyncExtendedPlayer message, Supplier<NetworkEvent.Context> ctx) {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> handle(message, ctx.get().getSender()));
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void handle(PacketSyncExtendedPlayer message, ServerPlayerEntity mp) {
        ExtendedPlayer ep = ExtendedPlayer.get(mp);
        if (ep != null)
            ep.loadState(message.hungerStrikeEnabled);
    }
}
