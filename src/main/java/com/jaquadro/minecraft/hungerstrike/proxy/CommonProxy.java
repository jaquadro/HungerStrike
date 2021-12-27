package com.jaquadro.minecraft.hungerstrike.proxy;

import com.jaquadro.minecraft.hungerstrike.ExtendedPlayer;
import com.jaquadro.minecraft.hungerstrike.ExtendedPlayerProvider;
import com.jaquadro.minecraft.hungerstrike.PlayerHandler;
import com.jaquadro.minecraft.hungerstrike.command.HungerStrikeCommand;
import com.jaquadro.minecraft.hungerstrike.network.PacketHandler;
import com.jaquadro.minecraft.hungerstrike.network.PacketRequestSync;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommonProxy
{

    public PlayerHandler playerHandler;

    public CommonProxy () {
        playerHandler = new PlayerHandler();

        MinecraftForge.EVENT_BUS.register(this);
    }

    public void registerNetworkHandlers () {
        //HungerStrike.network.registerMessage(RequestSyncMessage.Handler.class, RequestSyncMessage.class, RequestSyncMessage.MESSAGE_ID, Side.SERVER);
    }

    @SubscribeEvent
    public void tick (TickEvent.PlayerTickEvent event) {
        playerHandler.tick(event.player, event.phase, event.side);
    }

    @SubscribeEvent
    public void attachCapabilites (AttachCapabilitiesEvent event) {
        if (event.getObject() instanceof Player)
            event.addCapability(ExtendedPlayer.EXTENDED_PLAYER_KEY, new ExtendedPlayerProvider((Player) event.getObject()));
    }

    @SubscribeEvent
    public void livingDeath (LivingDeathEvent event) {
        Entity entity = event.getEntity();

        if (!entity.getCommandSenderWorld().isClientSide && entity instanceof ServerPlayer)
            playerHandler.storeData((Player) entity);
    }

    @SubscribeEvent
    public void entityJoinWorld (EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();

        if (!event.getWorld().isClientSide && entity instanceof ServerPlayer)
            playerHandler.restoreData((Player) entity);
        else if (event.getWorld().isClientSide && entity instanceof ServerPlayer)
           PacketHandler.INSTANCE.sendToServer(new PacketRequestSync());
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        HungerStrikeCommand.register(event.getDispatcher());
    }
}
