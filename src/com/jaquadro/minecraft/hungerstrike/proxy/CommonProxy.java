package com.jaquadro.minecraft.hungerstrike.proxy;

import com.jaquadro.minecraft.hungerstrike.ExtendedPlayer;
import com.jaquadro.minecraft.hungerstrike.HungerStrike;
import com.jaquadro.minecraft.hungerstrike.PlayerHandler;
import com.jaquadro.minecraft.hungerstrike.network.RequestSyncMessage;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy
{
    public PlayerHandler playerHandler;

    public CommonProxy () {
        playerHandler = new PlayerHandler();
    }

    public void registerNetworkHandlers () {
        HungerStrike.network.registerMessage(RequestSyncMessage.Handler.class, RequestSyncMessage.class, RequestSyncMessage.MESSAGE_ID, Side.SERVER);
    }

    @SubscribeEvent
    public void tick (TickEvent.PlayerTickEvent event) {
        playerHandler.tick(event.player, event.phase, event.side);
    }

    @SubscribeEvent
    public void entityConstructing (EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityPlayer && ExtendedPlayer.get((EntityPlayer) event.entity) == null)
            ExtendedPlayer.register((EntityPlayer) event.entity);
    }

    @SubscribeEvent
    public void livingDeath (LivingDeathEvent event) {
        if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayerMP)
            playerHandler.storeData((EntityPlayer) event.entity);
    }

    @SubscribeEvent
    public void entityJoinWorld (EntityJoinWorldEvent event) {
        if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayerMP)
            playerHandler.restoreData((EntityPlayer) event.entity);
        else if (event.entity.worldObj.isRemote && event.entity instanceof EntityPlayerSP)
            HungerStrike.network.sendToServer(new RequestSyncMessage());
    }
}
