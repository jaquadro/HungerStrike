package com.jaquadro.minecraft.hungerstrike;

import com.jaquadro.minecraft.hungerstrike.network.PacketHandler;
import com.jaquadro.minecraft.hungerstrike.network.PacketSyncExtendedPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodData;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkDirection;

public class ExtendedPlayer
{
    public static final ResourceLocation EXTENDED_PLAYER_KEY = new ResourceLocation("hungerstrike:extended_player");

    public static Capability<ExtendedPlayer> EXTENDED_PLAYER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private final Player player;

    private boolean hungerStrikeEnabled;
    private int startHunger;

    public ExtendedPlayer(Player player) {
        this.player = player;
        this.hungerStrikeEnabled = false;
    }

    public static ExtendedPlayer get (Player player) {
        if (EXTENDED_PLAYER_CAPABILITY == null)
            return null;

        return player.getCapability(EXTENDED_PLAYER_CAPABILITY, null).orElse(null);
    }

    public void saveNBTData(CompoundTag compound) {
        compound.putBoolean("Enabled", hungerStrikeEnabled);
    }

    public void loadNBTData(CompoundTag compound) {
        hungerStrikeEnabled = compound.getBoolean("Enabled");
    }

    //public void saveNBTDataSync (NBTTagCompound compound) {
    //    compound.setBoolean("Enabled", hungerStrikeEnabled);
    //}

    public void enableHungerStrike (boolean enable) {
        if (hungerStrikeEnabled != enable) {
            hungerStrikeEnabled = enable;
            if (player instanceof ServerPlayer) {
                ServerPlayer playerMP = (ServerPlayer)player;
                PacketHandler.INSTANCE.sendTo(new PacketSyncExtendedPlayer(player), playerMP.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
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
        setFoodData(player.getFoodData(), calcBaselineHunger(), 1);
        startHunger = player.getFoodData().getFoodLevel();
    }

    private void tickEnd (LogicalSide side) {
        if (side == LogicalSide.SERVER) {
            int foodDiff = player.getFoodData().getFoodLevel() - startHunger;
            if (foodDiff > 0)
                player.heal((float)(foodDiff * ModConfig.GENERAL.foodHealFactor.get()));
        }

        setFoodData(player.getFoodData(), calcBaselineHunger(), 1);
    }

    private void setFoodData (FoodData foodStats, int foodLevel, float saturationLevel) {
        foodStats.eat(1, (saturationLevel - foodStats.getSaturationLevel()) / 2);
        foodStats.eat(foodLevel - foodStats.getFoodLevel(), 0);
    }

    private int calcBaselineHunger () {
        if (player.hasEffect(MobEffects.HUNGER))
            return 5;
        else if (player.hasEffect(MobEffects.REGENERATION))
            return 20;
        else
            return ModConfig.GENERAL.hungerBaseline.get();
    }
}
