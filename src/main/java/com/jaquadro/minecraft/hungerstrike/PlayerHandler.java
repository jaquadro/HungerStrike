package com.jaquadro.minecraft.hungerstrike;

import com.mojang.authlib.GameProfile;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerHandler
{
    private static final Map<GameProfile, Map<String, CompoundTag>> dataStore = new HashMap<>();

    public static List<Player> getStrikingPlayers (MinecraftServer server) {
        return getPlayers(server, true);
    }

    public static List<Player> getNonStrikingPlayers (MinecraftServer server) {
        return getPlayers(server, false);
    }

    private static List<Player> getPlayers (MinecraftServer server, boolean isStriking) {
        List<Player> players = new ArrayList<>();
        for (ServerPlayer playerEnt : server.getPlayerList().getPlayers()) {
            ExtendedPlayer playerExt = ExtendedPlayer.get(playerEnt);
            if (playerExt != null && playerExt.isOnHungerStrike() == isStriking)
                players.add(playerEnt);
        }

        return players;
    }

    public void storeData (Player player) {
        ExtendedPlayer playerExt = ExtendedPlayer.get(player);

        if (playerExt != null) {
            CompoundTag data = new CompoundTag();
            playerExt.saveNBTData(data);
            
            storeData(player, "HungerStrike", data);
        }
    }

    public void storeData (Player player, String name, CompoundTag data) {
        storeData(player.getGameProfile(), name, data);
    }

    public void storeData (GameProfile profile, String name, CompoundTag data) {
        Map<String, CompoundTag> store = dataStore.get(profile);
        if (store == null) {
            store = new HashMap<>();
            dataStore.put(profile, store);
        }

        store.put(name, data);
    }

    public void restoreData (Player player) {
        ExtendedPlayer playerExt = ExtendedPlayer.get(player);

        if (playerExt != null) {
            CompoundTag data = getData(player, "HungerStrike");
            if (data != null)
                playerExt.loadNBTData(data);
        }
    }

    public CompoundTag getData (Player player, String name) {
        return getData(player.getGameProfile(), name);
    }

    public CompoundTag getData (GameProfile profile, String name) {
        Map<String, CompoundTag> store = dataStore.get(profile);
        if (store == null)
            return null;

        return store.remove(name);
    }

    public void tick (Player player, TickEvent.Phase phase, LogicalSide side) {
        ExtendedPlayer playerExt = ExtendedPlayer.get(player);
        if (playerExt != null)
            playerExt.tick(phase, side);
    }

    public boolean isOnHungerStrike (Player player) {
        ExtendedPlayer playerExt = ExtendedPlayer.get(player);
        if (playerExt != null)
            return playerExt.isOnHungerStrike();

        return false;
    }
}
