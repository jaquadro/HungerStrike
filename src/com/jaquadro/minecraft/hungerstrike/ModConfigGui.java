package com.jaquadro.minecraft.hungerstrike;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

public class ModConfigGui extends GuiConfig
{
    public ModConfigGui (GuiScreen parent) {
        super(parent, getConfigElements(), HungerStrike.MOD_ID, false, false, "Hunger Strike Configuration");
    }

    private static List<IConfigElement> getConfigElements () {
        List<IConfigElement> list = new ArrayList<>();

        for (Property prop : HungerStrike.config.getConfig().getCategory(Configuration.CATEGORY_GENERAL).values())
            list.add(new ConfigElement(prop));

        return list;
    }

}
