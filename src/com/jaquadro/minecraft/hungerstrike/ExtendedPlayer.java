package com.jaquadro.minecraft.hungerstrike;

import com.jaquadro.minecraft.hungerstrike.network.PacketHandler;
import com.jaquadro.minecraft.hungerstrike.network.PacketSyncExtendedPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effects;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkDirection;

public class ExtendedPlayer
{
    public static final ResourceLocation EXTENDED_PLAYER_KEY = new ResourceLocation("hungerstrike:extended_player");

    @CapabilityInject(ExtendedPlayer.class)
    public static Capability<ExtendedPlayer> EXTENDED_PLAYER_CAPABILITY;

    private final PlayerEntity player;

    private boolean hungerStrikeEnabled;
    private int startHunger;

    public ExtendedPlayer(PlayerEntity player) {
        this.player = player;
        this.hungerStrikeEnabled = false;
    }

    public static ExtendedPlayer get (PlayerEntity player) {
        if (EXTENDED_PLAYER_CAPABILITY == null)
            return null;

        return player.getCapability(EXTENDED_PLAYER_CAPABILITY, null).orElse(null);
    }

    public void saveNBTData(CompoundNBT compound) {
        compound.putBoolean("Enabled", hungerStrikeEnabled);
    }

    public void loadNBTData(CompoundNBT compound) {
        hungerStrikeEnabled = compound.getBoolean("Enabled");
    }

    //public void saveNBTDataSync (NBTTagCompound compound) {
    //    compound.setBoolean("Enabled", hungerStrikeEnabled);
    //}

    public void enableHungerStrike (boolean enable) {
        if (hungerStrikeEnabled != enable) {
            hungerStrikeEnabled = enable;
            if (player instanceof ServerPlayerEntity) {
                ServerPlayerEntity playerMP = (ServerPlayerEntity)player;
                PacketHandler.INSTANCE.sendTo(new PacketSyncExtendedPlayer(player), playerMP.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
            }
        }
    }

    public void loadState (boolean hungerStrikeEnabled) {
        this.hungerStrikeEnabled = hungerStrikeEnabled;
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
        ModConfig.Mode mode = ModConfig.GENERAL.mode.get();
        if (mode == ModConfig.Mode.LIST)
            return hungerStrikeEnabled;
        else
            return mode == ModConfig.Mode.ALL;
    }

    public void tick (TickEvent.Phase phase, LogicalSide side) {
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

    private void tickEnd (LogicalSide side) {
        if (side == LogicalSide.SERVER) {
            int foodDiff = player.getFoodStats().getFoodLevel() - startHunger;
            if (foodDiff > 0)
                player.heal(foodDiff * (float) ModConfig.GENERAL.foodHealFactor.get());
        }

        setFoodData(player.getFoodStats(), calcBaselineHunger(), 1);
    }

    private void setFoodData (FoodStats foodStats, int foodLevel, float saturationLevel) {
        foodStats.addStats(1, (saturationLevel - foodStats.getSaturationLevel()) / 2);
        foodStats.addStats(foodLevel - foodStats.getFoodLevel(), 0);
    }

    private int calcBaselineHunger () {
        if (player.isPotionActive(Effects.HUNGER))
            return 5;
        else if (player.isPotionActive(Effects.REGENERATION))
            return 20;
        else
            return ModConfig.GENERAL.hungerBaseline.get();
    }
}
