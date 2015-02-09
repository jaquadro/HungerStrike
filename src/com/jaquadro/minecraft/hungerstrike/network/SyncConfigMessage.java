package com.jaquadro.minecraft.hungerstrike.network;

import com.jaquadro.minecraft.hungerstrike.ConfigManager;
import com.jaquadro.minecraft.hungerstrike.HungerStrike;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

public class SyncConfigMessage implements IMessage
{
    public static final int MESSAGE_ID = 1;

    private NBTTagCompound data;

    public SyncConfigMessage () {
        data = new NBTTagCompound();
        data.setTag("mode", new NBTTagString(HungerStrike.config.getMode().toString()));
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
    public static class Handler implements IMessageHandler<SyncConfigMessage, IMessage>
    {
        @Override
        public IMessage onMessage (SyncConfigMessage message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                if (message.data.hasKey("mode")) {
                    String mode = message.data.getString("mode");
                    HungerStrike.config.setModeSoft(ConfigManager.Mode.valueOf(mode));
                }
            }

            return null;
        }
    }

    public static class HandlerStub implements IMessageHandler<SyncConfigMessage, IMessage>
    {
        @Override
        public IMessage onMessage (SyncConfigMessage message, MessageContext ctx) {
            FMLLog.log(HungerStrike.MOD_ID, Level.WARN, "SyncConfigMessage stub handler called.");
            return null;
        }
    }
}
