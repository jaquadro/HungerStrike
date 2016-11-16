package com.jaquadro.minecraft.hungerstrike;

import com.jaquadro.minecraft.hungerstrike.command.CommandHungerStrike;
import com.jaquadro.minecraft.hungerstrike.proxy.CommonProxy;
import net.minecraft.command.CommandHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameData;

@Mod(modid = HungerStrike.MOD_ID, name = HungerStrike.MOD_NAME, version = HungerStrike.MOD_VERSION, guiFactory = HungerStrike.SOURCE_PATH + "ModGuiFactory", acceptedMinecraftVersions = "[1.9,1.12)")
public class HungerStrike
{
    public static final String MOD_ID = "hungerstrike";
    static final String MOD_NAME = "Hunger Strike";
    static final String MOD_VERSION = "1.0.7";
    static final String SOURCE_PATH = "com.jaquadro.minecraft.hungerstrike.";

    @Mod.Instance(MOD_ID)
    public static HungerStrike instance;

    @SidedProxy(clientSide = SOURCE_PATH + "proxy.ClientProxy", serverSide = SOURCE_PATH + "proxy.ServerProxy")
    public static CommonProxy proxy;

    public static SimpleNetworkWrapper network;
    public static ConfigManager config = new ConfigManager();

    @Mod.EventHandler
    public void preInit (FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(proxy);

        config.setup(event.getSuggestedConfigurationFile());

        ExtendedPlayerHandler.register();
    }

    @Mod.EventHandler
    public void load (FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(instance);

        network = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);
        proxy.registerNetworkHandlers();
    }

    @Mod.EventHandler
    public void postInit (FMLPostInitializationEvent event) {
        if (config.getFoodStackSize() > -1) {
            for (Item item : Item.REGISTRY) {
                if (item instanceof ItemFood)
                    item.setMaxStackSize(config.getFoodStackSize());
            }
        }
    }

    @Mod.EventHandler
    public void serverStarted (FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandHungerStrike());
    }

    @SubscribeEvent
    public void onConfigChanged (ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(MOD_ID))
            config.syncConfig();
    }
}
