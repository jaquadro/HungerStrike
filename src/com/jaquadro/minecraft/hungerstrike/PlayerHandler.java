package com.jaquadro.minecraft.hungerstrike;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerHandler
{
    private static final Map<GameProfile, Map<String, NBTTagCompound>> dataStore = new HashMap<>();

    public static List<PlayerEntity> getStrikingPlayers (MinecraftServer server) {
        return getPlayers(server, true);
    }

    public static List<PlayerEntity> getNonStrikingPlayers (MinecraftServer server) {
        return getPlayers(server, false);
    }

    private static List<PlayerEntity> getPlayers (MinecraftServer server, boolean isStriking) {
        List<PlayerEntity> players = new ArrayList<>();
        for (ServerPlayerEntity playerEnt : server.getPlayerList().getPlayers()) {
            ExtendedPlayer playerExt = ExtendedPlayer.get(playerEnt);
            if (playerExt != null && playerExt.isOnHungerStrike() == isStriking)
                players.add(playerEnt);
        }

        return players;
    }

    public void storeData (EntityPlayer player) {
        ExtendedPlayer playerExt = ExtendedPlayer.get(player);

        if (playerExt != null) {
            NBTTagCompound data = new NBTTagCompound();
            playerExt.saveNBTData(data);

            storeData(player, "HungerStrike", data);
        }
    }

    public void storeData (EntityPlayer player, String name, NBTTagCompound data) {
        storeData(player.getGameProfile(), name, data);
    }

    public void storeData (GameProfile profile, String name, NBTTagCompound data) {
        Map<String, NBTTagCompound> store = dataStore.get(profile);
        if (store == null) {
            store = new HashMap<>();
            dataStore.put(profile, store);
        }

        store.put(name, data);
    }

    public void restoreData (EntityPlayer player) {
        ExtendedPlayer playerExt = ExtendedPlayer.get(player);

        if (playerExt != null) {
            NBTTagCompound data = getData(player, "HungerStrike");
            if (data != null)
                playerExt.loadNBTData(data);
        }
    }

    public NBTTagCompound getData (EntityPlayer player, String name) {
        return getData(player.getGameProfile(), name);
    }

    public NBTTagCompound getData (GameProfile profile, String name) {
        Map<String, NBTTagCompound> store = dataStore.get(profile);
        if (store == null)
            return null;

        return store.remove(name);
    }

    public void tick (EntityPlayer player, TickEvent.Phase phase, Side side) {
        ExtendedPlayer playerExt = ExtendedPlayer.get(player);
        if (playerExt != null)
            playerExt.tick(phase, side);
    }

    public boolean isOnHungerStrike (EntityPlayer player) {
        ExtendedPlayer playerExt = ExtendedPlayer.get(player);
        if (playerExt != null)
            return playerExt.isOnHungerStrike();

        return false;
    }
}
