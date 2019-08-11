package com.jaquadro.minecraft.hungerstrike.proxy;

import com.jaquadro.minecraft.hungerstrike.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientProxy extends CommonProxy {

    @Override
    public void registerNetworkHandlers () {
        super.registerNetworkHandlers();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::renderGameOverlay);

        //HungerStrike.network.registerMessage(SyncExtendedPlayerMessage.Handler.class, SyncExtendedPlayerMessage.class, SyncExtendedPlayerMessage.MESSAGE_ID, Side.CLIENT);
        //HungerStrike.network.registerMessage(SyncConfigMessage.Handler.class, SyncConfigMessage.class, SyncConfigMessage.MESSAGE_ID, Side.CLIENT);
    }

    private void renderGameOverlay (RenderGameOverlayEvent event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.FOOD) {
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
