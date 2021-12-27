package com.jaquadro.minecraft.hungerstrike.network;

import com.jaquadro.minecraft.hungerstrike.ModConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSyncConfig
{
    private String mode;

    public static void encode(PacketSyncConfig msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.mode, 128);
    }

    public static PacketSyncConfig decode(FriendlyByteBuf buf) {
        return new PacketSyncConfig(buf.readUtf(128));
    }

    private PacketSyncConfig(String mode) {
        this.mode = mode;
    }

    PacketSyncConfig() {
        this(ModConfig.GENERAL.mode.get().toString());
    }

    public static void handle(PacketSyncConfig message, Supplier<NetworkEvent.Context> ctx) {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> handle(message));
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void handle(PacketSyncConfig message) {
        ModConfig.GENERAL.mode.set(ModConfig.Mode.valueOf(message.mode));
    }
}
