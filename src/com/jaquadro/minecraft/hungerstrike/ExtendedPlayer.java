package com.jaquadro.minecraft.hungerstrike;

import com.jaquadro.minecraft.hungerstrike.network.SyncExtendedPlayerMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ExtendedPlayer
{
    public static final ResourceLocation EXTENDED_PLAYER_KEY = new ResourceLocation("HungerStrike:ExtendedPlayer");

    @CapabilityInject(ExtendedPlayer.class)
    public static Capability<ExtendedPlayer> EXTENDED_PLAYER_CAPABILITY;

    private final EntityPlayer player;

    private boolean hungerStrikeEnabled;
    private int startHunger;

    public ExtendedPlayer(EntityPlayer player) {
        this.player = player;
        this.hungerStrikeEnabled = false;
    }

    public static ExtendedPlayer get (EntityPlayer player) {
        if (EXTENDED_PLAYER_CAPABILITY == null)
            return null;

        return player.getCapability(EXTENDED_PLAYER_CAPABILITY, null);
    }

    public void saveNBTData(NBTTagCompound compound) {
        compound.setBoolean("Enabled", hungerStrikeEnabled);
    }

    public void loadNBTData(NBTTagCompound compound) {
        hungerStrikeEnabled = compound.getBoolean("Enabled");
    }

    public void saveNBTDataSync (NBTTagCompound compound) {
        compound.setBoolean("Enabled", hungerStrikeEnabled);
    }

    public void enableHungerStrike (boolean enable) {
        if (hungerStrikeEnabled != enable) {
            hungerStrikeEnabled = enable;
            if (player instanceof EntityPlayerMP)
                HungerStrike.network.sendTo(new SyncExtendedPlayerMessage(player), (EntityPlayerMP)player);
        }
    }

    public boolean isOnHungerStrike () {
        return hungerStrikeEnabled;
    }

    /*public boolean getEffectiveHungerStrike () {
        if (!player.worldObj.isRemote)
            return hungerStrikeEnabled;

        switch (HungerStrike.config.getMode()) {
            case NONE:
                return false;
            case ALL:
                return true;
            case LIST:
            default:
                return hungerStrikeEnabled;
        }
    }*/

    private boolean shouldTick () {
        ConfigManager.Mode mode = HungerStrike.config.getMode();
        if (mode == ConfigManager.Mode.LIST)
            return hungerStrikeEnabled;
        else
            return mode == ConfigManager.Mode.ALL;
    }

    public void tick (TickEvent.Phase phase, Side side) {
        if (!shouldTick())
            return;

        if (phase == TickEvent.Phase.START)
            tickStart();
        else if (phase == TickEvent.Phase.END)
            tickEnd(side);
    }

    private void tickStart () {
        setFoodData(player.getFoodStats(), calcBaselineHunger(), 1);
        startHunger = player.getFoodStats().getFoodLevel();
    }

    private void tickEnd (Side side) {
        if (side == Side.SERVER) {
            int foodDiff = player.getFoodStats().getFoodLevel() - startHunger;
            if (foodDiff > 0)
                player.heal(foodDiff * (float) HungerStrike.config.getFoodHealFactor());
        }

        setFoodData(player.getFoodStats(), calcBaselineHunger(), 1);
    }

    private void setFoodData (FoodStats foodStats, int foodLevel, float saturationLevel) {
        foodStats.addStats(1, (saturationLevel - foodStats.getSaturationLevel()) / 2);
        foodStats.addStats(foodLevel - foodStats.getFoodLevel(), 0);
    }

    private int calcBaselineHunger () {
        if (player.isPotionActive(Potion.getPotionFromResourceLocation("hunger")))
            return 5;
        else if (player.isPotionActive(Potion.getPotionFromResourceLocation("regeneration")))
            return 20;
        else
            return HungerStrike.config.getHungerBaseline();
    }
}
