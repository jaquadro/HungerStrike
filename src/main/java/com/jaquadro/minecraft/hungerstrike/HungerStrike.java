package com.jaquadro.minecraft.hungerstrike;

import com.jaquadro.minecraft.hungerstrike.network.PacketHandler;
import com.jaquadro.minecraft.hungerstrike.proxy.ClientProxy;
import com.jaquadro.minecraft.hungerstrike.proxy.CommonProxy;
import com.jaquadro.minecraft.hungerstrike.proxy.ServerProxy;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(HungerStrike.MOD_ID)
public class HungerStrike
{
    public static final String MOD_ID = "hungerstrike";

    static final String MOD_NAME = "Hunger Strike";
    static final String MOD_VERSION = "@VERSION@";
    static final String SOURCE_PATH = "com.jaquadro.minecraft.hungerstrike.";

    public static final Logger log = LogManager.getLogger();

    public static CommonProxy proxy;

    public HungerStrike() {
        proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, ModConfig.spec);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        MinecraftForge.EVENT_BUS.register(this);
        //FMLJavaModLoadingContext.get().getModEventBus().register(this);
    }

    private void setup (final FMLCommonSetupEvent event) {
        //config.setup(event.);
        PacketHandler.init();

        IForgeRegistry<Item> itemRegistry = ForgeRegistries.ITEMS;
        if (ModConfig.GENERAL.foodStackSize.get() > -1) {
            for (Item item : itemRegistry) {
                if (item != null && item.isEdible())
                    item.maxStackSize = ModConfig.GENERAL.foodStackSize.get();
            }
        }
    }

    @SubscribeEvent
    public void registerCapabilities (RegisterCapabilitiesEvent event) {
        ExtendedPlayerHandler.register(event);
    }

    //@SubscribeEvent
    //public void onConfigChanged (ConfigChangedEvent.OnConfigChangedEvent event) {
        //if (event.getModID().equals(MOD_ID))
        //    config.syncConfig();
    //}

    //@SidedProxy(clientSide = SOURCE_PATH + "proxy.ClientProxy", serverSide = SOURCE_PATH + "proxy.ServerProxy")
    //public static CommonProxy proxy;

    /*public static final SimpleChannel network = NetworkRegistry.ChannelBuilder
        .named(new ResourceLocation(MOD_ID, "channel"))
        .clientAcceptedVersions(s -> true)
        .serverAcceptedVersions(s -> true)
        .networkProtocolVersion(() -> "1.0.0")
        .simpleChannel();*/

    //public static SimpleNetworkWrapper network;
}
