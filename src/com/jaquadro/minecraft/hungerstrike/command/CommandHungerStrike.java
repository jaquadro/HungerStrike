package com.jaquadro.minecraft.hungerstrike.command;

import com.jaquadro.minecraft.hungerstrike.ConfigManager;
import com.jaquadro.minecraft.hungerstrike.ExtendedPlayer;
import com.jaquadro.minecraft.hungerstrike.HungerStrike;
import com.jaquadro.minecraft.hungerstrike.PlayerHandler;
import com.jaquadro.minecraft.hungerstrike.network.SyncConfigMessage;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.ArrayList;
import java.util.List;

public class CommandHungerStrike extends CommandBase {
    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override
    public String getCommandName () {
        return "hungerstrike";
    }

    @Override
    public String getCommandUsage (ICommandSender sender) {
        return "commands.hungerstrike.usage";
    }

    @Override
    public void execute (MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length >= 1) {
            if (args[0].equals("list")) {
                List<String> players = playersToNames(PlayerHandler.getStrikingPlayers(server));

                sender.addChatMessage(new TextComponentTranslation("commands.hungerstrike.list",
                    Integer.valueOf(players.size()),
                    Integer.valueOf(server.getPlayerList().getPlayerList().size())));
                sender.addChatMessage(new TextComponentString(joinNiceString(players.toArray(new String[players.size()]))));
                return;
            }

            if (args[0].equals("add")) {
                if (args.length < 2)
                    throw new WrongUsageException("commands.hungerstrike.add.usage");

                ExtendedPlayer player = ExtendedPlayer.get(getPlayer(server, sender, args[1]));
                player.enableHungerStrike(true);

                notifyCommandListener(sender, this, "commands.hungerstrike.add.success", args[1]);
                return;
            }

            if (args[0].equals("remove")) {
                if (args.length < 2)
                    throw new WrongUsageException("commands.hungerstrike.remove.usage");

                ExtendedPlayer player = ExtendedPlayer.get(getPlayer(server, sender, args[1]));
                player.enableHungerStrike(false);

                notifyCommandListener(sender, this, "commands.hungerstrike.remove.success", args[1]);
                return;
            }

            if (args[0].equals("mode")) {
                ConfigManager.Mode mode = HungerStrike.instance.config.getMode();

                if (mode == ConfigManager.Mode.NONE)
                    notifyCommandListener(sender, this, "commands.hungerstrike.mode.none");
                else if (mode == ConfigManager.Mode.LIST)
                    notifyCommandListener(sender, this, "commands.hungerstrike.mode.list");
                else if (mode == ConfigManager.Mode.ALL)
                    notifyCommandListener(sender, this, "commands.hungerstrike.mode.all");
                return;
            }

            if (args[0].equals("setmode")) {
                if (args.length < 2)
                    throw new WrongUsageException("commands.hungerstrike.setmode.usage");

                ConfigManager.Mode mode = ConfigManager.Mode.fromValueIgnoreCase(args[1]);
                HungerStrike.instance.config.setMode(mode);

                if (!sender.getEntityWorld().isRemote)
                    HungerStrike.network.sendToAll(new SyncConfigMessage());

                if (mode == ConfigManager.Mode.NONE)
                    notifyCommandListener(sender, this, "commands.hungerstrike.setmode.none");
                else if (mode == ConfigManager.Mode.LIST)
                    notifyCommandListener(sender, this, "commands.hungerstrike.setmode.list");
                else if (mode == ConfigManager.Mode.ALL)
                    notifyCommandListener(sender, this, "commands.hungerstrike.setmode.all");
                return;
            }
        }

        throw new WrongUsageException("commands.hungerstrike.usage");
    }

    @Override
    public List getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "list", "add", "remove", "mode", "setmode");
        }
        else {
            if (args.length == 2) {
                if (args[0].equals("add")) {
                    List<String> players = playersToNames(PlayerHandler.getNonStrikingPlayers(server));
                    return getPartialMatches(args[args.length - 1], players);
                }

                if (args[0].equals("remove")) {
                    List<String> players = playersToNames(PlayerHandler.getStrikingPlayers(server));
                    return getPartialMatches(args[args.length - 1], players);
                }

                if (args[0].equals("setmode")) {
                    return getListOfStringsMatchingLastWord(args, "none", "list", "all");
                }
            }

            return null;
        }
    }

    private List<String> playersToNames (List<EntityPlayer> players) {
        List<String> playerNames = new ArrayList<String>(players.size());
        for (EntityPlayer player : players)
            playerNames.add(player.getName());

        return playerNames;
    }

    private List<String> getPartialMatches (String partialName, List<String> candidates) {
        List<String> suggestions = new ArrayList<String>();
        for (int i = 0; i < candidates.size(); i++) {
            String playerName = candidates.get(i);
            if (doesStringStartWith(partialName, playerName))
                suggestions.add(playerName);
        }

        return suggestions;
    }
}
