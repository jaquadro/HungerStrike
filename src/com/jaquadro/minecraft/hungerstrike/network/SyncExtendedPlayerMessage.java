package com.jaquadro.minecraft.hungerstrike.network;

import com.jaquadro.minecraft.hungerstrike.ExtendedPlayer;
import com.jaquadro.minecraft.hungerstrike.HungerStrike;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

public class SyncExtendedPlayerMessage implements IMessage
{
    public static final int MESSAGE_ID = 0;

    private NBTTagCompound data;

    public SyncExtendedPlayerMessage () { }

    public SyncExtendedPlayerMessage (EntityPlayer player) {
        data = new NBTTagCompound();
        ExtendedPlayer ep = ExtendedPlayer.get(player);
        if (ep != null)
            ep.saveNBTDataSync(data);
    }

    @Override
    public void fromBytes (ByteBuf buf) {
        data = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes (ByteBuf buf) {
        ByteBufUtils.writeTag(buf, data);
    }

    @SideOnly(Side.CLIENT)
    public static class Handler implements IMessageHandler<SyncExtendedPlayerMessage, IMessage>
    {
        @Override
        public IMessage onMessage (SyncExtendedPlayerMessage message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                ExtendedPlayer ep = ExtendedPlayer.get(FMLClientHandler.instance().getClientPlayerEntity());
                if (ep != null)
                    ep.loadNBTData(message.data);
            }

            return null;
        }
    }

    public static class HandlerStub implements IMessageHandler<SyncExtendedPlayerMessage, IMessage>
    {
        @Override
        public IMessage onMessage (SyncExtendedPlayerMessage message, MessageContext ctx) {
            FMLLog.log(HungerStrike.MOD_ID, Level.WARN, "SyncExtendedPlayerMessage stub handler called.");
            return null;
        }
    }
}
