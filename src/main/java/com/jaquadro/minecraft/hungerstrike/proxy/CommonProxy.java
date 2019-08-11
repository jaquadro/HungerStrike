package com.jaquadro.minecraft.hungerstrike.proxy;

import com.jaquadro.minecraft.hungerstrike.ExtendedPlayer;
import com.jaquadro.minecraft.hungerstrike.ExtendedPlayerProvider;
import com.jaquadro.minecraft.hungerstrike.PlayerHandler;
import com.jaquadro.minecraft.hungerstrike.network.PacketHandler;
import com.jaquadro.minecraft.hungerstrike.network.PacketRequestSync;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class CommonProxy
{

    public PlayerHandler playerHandler;

    public CommonProxy () {
        playerHandler = new PlayerHandler();

        MinecraftForge.EVENT_BUS.addListener(this::tick);
        MinecraftForge.EVENT_BUS.addListener(this::attachCapabilites);
        MinecraftForge.EVENT_BUS.addListener(this::livingDeath);
        MinecraftForge.EVENT_BUS.addListener(this::entityJoinWorld);
    }

    public void registerNetworkHandlers () {
        //HungerStrike.network.registerMessage(RequestSyncMessage.Handler.class, RequestSyncMessage.class, RequestSyncMessage.MESSAGE_ID, Side.SERVER);
    }

    private void tick (TickEvent.PlayerTickEvent event) {
        playerHandler.tick(event.player, event.phase, event.side);
    }

    private void attachCapabilites (AttachCapabilitiesEvent event) {
        if (event.getObject() instanceof PlayerEntity)
            event.addCapability(ExtendedPlayer.EXTENDED_PLAYER_KEY, new ExtendedPlayerProvider((PlayerEntity) event.getObject()));
    }

    private void livingDeath (LivingDeathEvent event) {
        Entity entity = event.getEntity();

        if (!entity.getEntityWorld().isRemote && entity instanceof ServerPlayerEntity)
            playerHandler.storeData((PlayerEntity) entity);
    }

    private void entityJoinWorld (EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();

        if (!entity.getEntityWorld().isRemote && entity instanceof ServerPlayerEntity)
            playerHandler.restoreData((PlayerEntity) entity);
        else if (entity.getEntityWorld().isRemote && entity instanceof ServerPlayerEntity)
           PacketHandler.INSTANCE.sendToServer(new PacketRequestSync());
    }
}
