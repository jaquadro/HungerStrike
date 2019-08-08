package com.jaquadro.minecraft.hungerstrike.network;

import com.jaquadro.minecraft.hungerstrike.ConfigManager;
import com.jaquadro.minecraft.hungerstrike.HungerStrike;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSyncConfig
{
    private String mode;

    public static void encode(PacketSyncConfig msg, PacketBuffer buf) {
        buf.writeString(msg.mode, 128);
    }

    public static PacketSyncConfig decode(PacketBuffer buf) {
        return new PacketSyncConfig(buf.readString(128));
    }

    private PacketSyncConfig(String mode) {
        this.mode = mode;
    }

    PacketSyncConfig() {
        this(HungerStrike.config.getMode().toString());
    }

    public static void handle(PacketSyncConfig message, Supplier<NetworkEvent.Context> ctx) {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> handle(message));
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void handle(PacketSyncConfig message) {
        HungerStrike.config.setModeSoft(ConfigManager.Mode.valueOf(message.mode));
    }
}
