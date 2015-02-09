package com.jaquadro.minecraft.hungerstrike.proxy;

import com.jaquadro.minecraft.hungerstrike.HungerStrike;
import com.jaquadro.minecraft.hungerstrike.network.SyncConfigMessage;
import com.jaquadro.minecraft.hungerstrike.network.SyncExtendedPlayerMessage;
import net.minecraftforge.fml.relauncher.Side;

public class ServerProxy extends CommonProxy
{
    @Override
    public void registerNetworkHandlers () {
        super.registerNetworkHandlers();

        HungerStrike.network.registerMessage(SyncExtendedPlayerMessage.HandlerStub.class, SyncExtendedPlayerMessage.class, SyncExtendedPlayerMessage.MESSAGE_ID, Side.CLIENT);
        HungerStrike.network.registerMessage(SyncConfigMessage.HandlerStub.class, SyncConfigMessage.class, SyncConfigMessage.MESSAGE_ID, Side.CLIENT);
    }
}
