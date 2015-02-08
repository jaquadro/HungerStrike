package com.jaquadro.minecraft.hungerstrike;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

import java.util.ArrayList;
import java.util.List;

public class ModConfigGui extends GuiConfig
{
    public ModConfigGui (GuiScreen parent) {
        super(parent, getConfigElements(), HungerStrike.MOD_ID, false, false, "Hunger Strike Configuration");
    }

    private static List<IConfigElement> getConfigElements () {
        List<IConfigElement> list = new ArrayList<IConfigElement>();

        list.add(new ConfigElement(HungerStrike.instance.config.getConfig().getCategory(Configuration.CATEGORY_GENERAL)));

        //for (ConfigManager.ConfigSection section : StorageDrawers.config.sections)
        //    list.add(new ConfigElement(section.getCategory()));

        return list;
    }

}
