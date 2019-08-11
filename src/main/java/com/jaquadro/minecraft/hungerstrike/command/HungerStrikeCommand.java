package com.jaquadro.minecraft.hungerstrike.command;

import com.jaquadro.minecraft.hungerstrike.ModConfig;
import com.jaquadro.minecraft.hungerstrike.ExtendedPlayer;
import com.jaquadro.minecraft.hungerstrike.PlayerHandler;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.GameProfileArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HungerStrikeCommand
{
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("hungerstrike")
            .requires((req) -> req.hasPermissionLevel(3))
            .then(Commands.literal("list")
                .executes((command) -> listPlayers(command.getSource()))
            )
            .then(Commands.literal("add")
                .then(Commands.argument("targets", GameProfileArgument.gameProfile())
                    .suggests((command, t) -> {
                        PlayerList players = command.getSource().getServer().getPlayerList();
                        return ISuggestionProvider.suggest(players.getPlayers().stream().filter((entity) -> {
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
                        return ISuggestionProvider.suggest(players.getPlayers().stream().filter((entity) -> {
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

    private static int listPlayers(CommandSource source) {
        List<String> players = playersToNames(PlayerHandler.getStrikingPlayers(source.getServer()));

        if (players.size() == 0) {
            source.sendFeedback(new TranslationTextComponent("commands.hungerstrike.list.none"), false);
        } else {
            source.sendFeedback(new TranslationTextComponent("commands.hungerstrike.list", players.size(), String.join(", ", players)), false);
        }

        return players.size();
    }

    private static int addPlayers(CommandSource source, Collection<GameProfile> playerProfiles) {
        int addedCount = 0;
        for (GameProfile profile : playerProfiles) {
            ExtendedPlayer player = ExtendedPlayer.get(source.getServer().getPlayerList().getPlayerByUUID(profile.getId()));
            if (player != null && !player.isOnHungerStrike()) {
                player.enableHungerStrike(true);
                source.sendFeedback(new TranslationTextComponent("commands.hungerstrike.add.success", TextComponentUtils.getDisplayName(profile)), true);
                addedCount++;
            }
        }

        return addedCount;
    }

    private static int removePlayers(CommandSource source, Collection<GameProfile> playerProfiles) {
        int removedCount = 0;
        for (GameProfile profile : playerProfiles) {
            ExtendedPlayer player = ExtendedPlayer.get(source.getServer().getPlayerList().getPlayerByUUID(profile.getId()));
            if (player != null && player.isOnHungerStrike()) {
                player.enableHungerStrike(false);
                source.sendFeedback(new TranslationTextComponent("commands.hungerstrike.remove.success", TextComponentUtils.getDisplayName(profile)), true);
                removedCount++;
            }
        }

        return removedCount;
    }

    private static int getMode(CommandSource source) {
        ModConfig.Mode mode = ModConfig.GENERAL.mode.get();

        if (mode == ModConfig.Mode.NONE)
            source.sendFeedback(new TranslationTextComponent("commands.hungerstrike.mode.none"), false);
        else if (mode == ModConfig.Mode.LIST)
            source.sendFeedback(new TranslationTextComponent("commands.hungerstrike.mode.list"), false);
        else if (mode == ModConfig.Mode.ALL)
            source.sendFeedback(new TranslationTextComponent("commands.hungerstrike.mode.all"), false);

        return 1;
    }

    private static int setMode(CommandSource source, ModConfig.Mode mode) {
        ModConfig.GENERAL.mode.set(mode);

        //if (!source.getWorld().isRemote)
        //    HungerStrike.network.sendToAll(new SyncConfigMessage());

        if (mode == ModConfig.Mode.NONE)
            source.sendFeedback(new TranslationTextComponent("commands.hungerstrike.setmode.none"), true);
        else if (mode == ModConfig.Mode.LIST)
            source.sendFeedback(new TranslationTextComponent("commands.hungerstrike.setmode.list"), true);
        else if (mode == ModConfig.Mode.ALL)
            source.sendFeedback(new TranslationTextComponent("commands.hungerstrike.setmode.all"), true);

        return 1;
    }

    private static List<String> playersToNames (List<PlayerEntity> players) {
        List<String> playerNames = new ArrayList<>(players.size());
        for (PlayerEntity player : players)
            playerNames.add(player.getName().getString());

        return playerNames;
    }
}
