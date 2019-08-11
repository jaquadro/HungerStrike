package com.jaquadro.minecraft.hungerstrike;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerHandler
{
    private static final Map<GameProfile, Map<String, CompoundNBT>> dataStore = new HashMap<>();

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

    public void storeData (PlayerEntity player) {
        ExtendedPlayer playerExt = ExtendedPlayer.get(player);

        if (playerExt != null) {
            CompoundNBT data = new CompoundNBT();
            playerExt.saveNBTData(data);

            storeData(player, "HungerStrike", data);
        }
    }

    public void storeData (PlayerEntity player, String name, CompoundNBT data) {
        storeData(player.getGameProfile(), name, data);
    }

    public void storeData (GameProfile profile, String name, CompoundNBT data) {
        Map<String, CompoundNBT> store = dataStore.get(profile);
        if (store == null) {
            store = new HashMap<>();
            dataStore.put(profile, store);
        }

        store.put(name, data);
    }

    public void restoreData (PlayerEntity player) {
        ExtendedPlayer playerExt = ExtendedPlayer.get(player);

        if (playerExt != null) {
            CompoundNBT data = getData(player, "HungerStrike");
            if (data != null)
                playerExt.loadNBTData(data);
        }
    }

    public CompoundNBT getData (PlayerEntity player, String name) {
        return getData(player.getGameProfile(), name);
    }

    public CompoundNBT getData (GameProfile profile, String name) {
        Map<String, CompoundNBT> store = dataStore.get(profile);
        if (store == null)
            return null;

        return store.remove(name);
    }

    public void tick (PlayerEntity player, TickEvent.Phase phase, LogicalSide side) {
        ExtendedPlayer playerExt = ExtendedPlayer.get(player);
        if (playerExt != null)
            playerExt.tick(phase, side);
    }

    public boolean isOnHungerStrike (PlayerEntity player) {
        ExtendedPlayer playerExt = ExtendedPlayer.get(player);
        if (playerExt != null)
            return playerExt.isOnHungerStrike();

        return false;
    }
}
