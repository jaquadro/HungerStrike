package com.jaquadro.minecraft.hungerstrike.proxy;

import com.jaquadro.minecraft.hungerstrike.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

    public ClientProxy() {
        super();

        MinecraftForge.EVENT_BUS.addListener(this::renderGameOverlay);
    }
    @Override
    public void registerNetworkHandlers () {
        super.registerNetworkHandlers();

        //HungerStrike.network.registerMessage(SyncExtendedPlayerMessage.Handler.class, SyncExtendedPlayerMessage.class, SyncExtendedPlayerMessage.MESSAGE_ID, Side.CLIENT);
        //HungerStrike.network.registerMessage(SyncConfigMessage.Handler.class, SyncConfigMessage.class, SyncConfigMessage.MESSAGE_ID, Side.CLIENT);
    }

    private void renderGameOverlay (RenderGameOverlayEvent.PreLayer event) {
        if (event.getOverlay() == ForgeIngameGui.FOOD_LEVEL_ELEMENT) {
            if (!ModConfig.GENERAL.hideHungerBar.get())
                return;

            switch (ModConfig.GENERAL.mode.get()) {
                case NONE:
                    break;
                case ALL:
                    event.setCanceled(true);
                    break;
                case LIST:
                    if (playerHandler.isOnHungerStrike(Minecraft.getInstance().player))
                        event.setCanceled(true);
                    break;
            }
        }
    }

}
