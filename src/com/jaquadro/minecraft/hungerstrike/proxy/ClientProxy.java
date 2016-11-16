package com.jaquadro.minecraft.hungerstrike.proxy;

import com.jaquadro.minecraft.hungerstrike.HungerStrike;
import com.jaquadro.minecraft.hungerstrike.network.SyncConfigMessage;
import com.jaquadro.minecraft.hungerstrike.network.SyncExtendedPlayerMessage;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {

    @Override
    public void registerNetworkHandlers () {
        super.registerNetworkHandlers();

        HungerStrike.network.registerMessage(SyncExtendedPlayerMessage.Handler.class, SyncExtendedPlayerMessage.class, SyncExtendedPlayerMessage.MESSAGE_ID, Side.CLIENT);
        HungerStrike.network.registerMessage(SyncConfigMessage.Handler.class, SyncConfigMessage.class, SyncConfigMessage.MESSAGE_ID, Side.CLIENT);
    }

    @SubscribeEvent
    public void renderGameOverlay (RenderGameOverlayEvent event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.FOOD) {
            if (!HungerStrike.config.isHungerBarHidden())
                return;

            switch (HungerStrike.config.getMode()) {
                case NONE:
                    break;
                case ALL:
                    event.setCanceled(true);
                    break;
                case LIST:
                    if (playerHandler.isOnHungerStrike(Minecraft.getMinecraft().player))
                        event.setCanceled(true);
                    break;
            }
        }
    }
}
