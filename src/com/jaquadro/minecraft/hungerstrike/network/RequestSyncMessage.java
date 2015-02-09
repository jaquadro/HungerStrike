package com.jaquadro.minecraft.hungerstrike.network;

import com.jaquadro.minecraft.hungerstrike.HungerStrike;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class RequestSyncMessage implements IMessage
{
    public static final int MESSAGE_ID = 2;

    @Override
    public void fromBytes (ByteBuf buf) { }

    @Override
    public void toBytes (ByteBuf buf) { }

    public static class Handler implements IMessageHandler<RequestSyncMessage, IMessage>
    {
        @Override
        public IMessage onMessage (RequestSyncMessage message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                EntityPlayerMP player = ctx.getServerHandler().playerEntity;
                HungerStrike.network.sendTo(new SyncExtendedPlayerMessage(player), player);
                HungerStrike.network.sendTo(new SyncConfigMessage(), player);
            }

            return null;
        }
    }
}
