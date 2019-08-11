package com.jaquadro.minecraft.hungerstrike.proxy;

public class ServerProxy extends CommonProxy
{
    @Override
    public void registerNetworkHandlers () {
        super.registerNetworkHandlers();

        //HungerStrike.network.registerMessage(SyncExtendedPlayerMessage.HandlerStub.class, SyncExtendedPlayerMessage.class, SyncExtendedPlayerMessage.MESSAGE_ID, Side.CLIENT);
        //HungerStrike.network.registerMessage(SyncConfigMessage.HandlerStub.class, SyncConfigMessage.class, SyncConfigMessage.MESSAGE_ID, Side.CLIENT);
    }
}
