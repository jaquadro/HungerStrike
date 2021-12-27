package com.jaquadro.minecraft.hungerstrike.command;

import com.jaquadro.minecraft.hungerstrike.ModConfig;
import com.jaquadro.minecraft.hungerstrike.ExtendedPlayer;
import com.jaquadro.minecraft.hungerstrike.PlayerHandler;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.players.PlayerList;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HungerStrikeCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("hungerstrike")
            .requires((req) -> req.hasPermission(3))
            .then(Commands.literal("list")
                .executes((command) -> listPlayers(command.getSource()))
            )
            .then(Commands.literal("add")
                .then(Commands.argument("targets", GameProfileArgument.gameProfile())
                    .suggests((command, t) -> {
                        PlayerList players = command.getSource().getServer().getPlayerList();
                        return SharedSuggestionProvider.suggest(players.getPlayers().stream().filter((entity) -> {
                            ExtendedPlayer player = ExtendedPlayer.get(entity);
                            return player != null && !player.isOnHungerStrike();
                        }).map((entity) -> entity.getGameProfile().getName()), t);
                    })
                    .executes((command) -> addPlayers(command.getSource(), GameProfileArgument.getGameProfiles(command, "targets")))
                )
            )
            .then(Commands.literal("remove")
                .then(Commands.argument("targets", GameProfileArgument.gameProfile())
                    .suggests((command, t) -> {
                        PlayerList players = command.getSource().getServer().getPlayerList();
                        return SharedSuggestionProvider.suggest(players.getPlayers().stream().filter((entity) -> {
                            ExtendedPlayer player = ExtendedPlayer.get(entity);
                            return player != null && player.isOnHungerStrike();
                        }).map((entity) -> entity.getGameProfile().getName()), t);
                    })
                    .executes((command) -> removePlayers(command.getSource(), GameProfileArgument.getGameProfiles(command, "targets")))
                )
            )
            .then(Commands.literal("mode")
                .executes((command) -> getMode(command.getSource()))
            )
            .then(Commands.literal("setmode")
                .then(Commands.literal("none")
                    .executes((command) -> setMode(command.getSource(), ModConfig.Mode.NONE))
                ).then(Commands.literal("list")
                    .executes((command) -> setMode(command.getSource(), ModConfig.Mode.LIST))
                ).then(Commands.literal("all")
                    .executes((command) -> setMode(command.getSource(), ModConfig.Mode.ALL))
                )
            )
        );
    }

    private static int listPlayers(CommandSourceStack source) {
        List<String> players = playersToNames(PlayerHandler.getStrikingPlayers(source.getServer()));

        if (players.size() == 0) {
            source.sendSuccess(new TranslatableComponent("commands.hungerstrike.list.none"), false);
        } else {
            source.sendSuccess(new TranslatableComponent("commands.hungerstrike.list", players.size(), String.join(", ", players)), false);
        }

        return players.size();
    }

    private static int addPlayers(CommandSourceStack source, Collection<GameProfile> playerProfiles) {
        int addedCount = 0;
        for (GameProfile profile : playerProfiles) {
            ExtendedPlayer player = ExtendedPlayer.get(source.getServer().getPlayerList().getPlayer(profile.getId()));
            if (player != null && !player.isOnHungerStrike()) {
                player.enableHungerStrike(true);
                source.sendSuccess(new TranslatableComponent("commands.hungerstrike.add.success", ComponentUtils.getDisplayName(profile)), true);
                addedCount++;
            }
        }

        return addedCount;
    }

    private static int removePlayers(CommandSourceStack source, Collection<GameProfile> playerProfiles) {
        int removedCount = 0;
        for (GameProfile profile : playerProfiles) {
            ExtendedPlayer player = ExtendedPlayer.get(source.getServer().getPlayerList().getPlayer(profile.getId()));
            if (player != null && player.isOnHungerStrike()) {
                player.enableHungerStrike(false);
                source.sendSuccess(new TranslatableComponent("commands.hungerstrike.remove.success", ComponentUtils.getDisplayName(profile)), true);
                removedCount++;
            }
        }

        return removedCount;
    }

    private static int getMode(CommandSourceStack source) {
        ModConfig.Mode mode = ModConfig.GENERAL.mode.get();

        if (mode == ModConfig.Mode.NONE)
            source.sendSuccess(new TranslatableComponent("commands.hungerstrike.mode.none"), false);
        else if (mode == ModConfig.Mode.LIST)
            source.sendSuccess(new TranslatableComponent("commands.hungerstrike.mode.list"), false);
        else if (mode == ModConfig.Mode.ALL)
            source.sendSuccess(new TranslatableComponent("commands.hungerstrike.mode.all"), false);

        return 1;
    }

    private static int setMode(CommandSourceStack source, ModConfig.Mode mode) {
        ModConfig.GENERAL.mode.set(mode);

        //if (!source.getWorld().isRemote)
        //    HungerStrike.network.sendToAll(new SyncConfigMessage());

        if (mode == ModConfig.Mode.NONE)
            source.sendSuccess(new TranslatableComponent("commands.hungerstrike.setmode.none"), true);
        else if (mode == ModConfig.Mode.LIST)
            source.sendSuccess(new TranslatableComponent("commands.hungerstrike.setmode.list"), true);
        else if (mode == ModConfig.Mode.ALL)
            source.sendSuccess(new TranslatableComponent("commands.hungerstrike.setmode.all"), true);

        return 1;
    }

    private static List<String> playersToNames (List<Player> players) {
        List<String> playerNames = new ArrayList<>(players.size());
        for (Player player : players)
            playerNames.add(player.getName().getString());

        return playerNames;
    }
}
