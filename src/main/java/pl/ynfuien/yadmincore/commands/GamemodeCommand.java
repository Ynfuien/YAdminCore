package pl.ynfuien.yadmincore.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.ynfuien.yadmincore.YAdminCore;
import pl.ynfuien.yadmincore.utils.Lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GamemodeCommand extends YCommand {
    private static final String PERMISSION_BASE = "yadmincore.gamemode";
    private static final String PERMISSION_OTHERS = PERMISSION_BASE + ".others";

    public GamemodeCommand(YAdminCore instance) {
        super(instance);
    }

    @Override
    protected void run(@NotNull CommandSender sender, @NotNull String[] args, @NotNull HashMap<String, Object> placeholders) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                Lang.Message.COMMAND_GM_USAGE_OTHER.send(sender, placeholders);
                return;
            }

            Lang.Message.COMMAND_GM_USAGE.send(sender, placeholders);
            if (sender.hasPermission(PERMISSION_OTHERS)) {
                Lang.Message.COMMAND_GM_USAGE_OTHER.send(sender, placeholders);
            }
            return;
        }

        // Check for console
        if (!(sender instanceof Player)) {
            Lang.Message.COMMAND_GM_FAIL_NEED_PLAYER.send(sender, placeholders);
            return;
        }

        // Get gamemode from an argument
        GameMode gamemode = getGamemode(args[0]);
        placeholders.put("gamemode", args[0]);
        if (gamemode == null) {
            Lang.Message.COMMAND_GM_FAIL_INCORRECT_GAMEMODE.send(sender, placeholders);
            return;
        }

        switch (gamemode) {
            case ADVENTURE:
                placeholders.put("gamemode", Lang.Message.COMMAND_GM_ADVENTURE.get(placeholders));
                break;
            case CREATIVE:
                placeholders.put("gamemode", Lang.Message.COMMAND_GM_CREATIVE.get(placeholders));
                break;
            case SURVIVAL:
                placeholders.put("gamemode", Lang.Message.COMMAND_GM_SURVIVAL.get(placeholders));
                break;
            case SPECTATOR:
                placeholders.put("gamemode", Lang.Message.COMMAND_GM_SPECTATOR.get(placeholders));
                break;
        }

        if (!sender.hasPermission(String.format("%s.%s", PERMISSION_BASE, gamemode.name().toLowerCase()))) {
            Lang.Message.COMMAND_GM_FAIL_NO_PERMISSION.send(sender, placeholders);
            return;
        }

        // Self gamemode change
        if (args.length == 1) {
            ((Player) sender).setGameMode(gamemode);
            Lang.Message.COMMAND_GM_SUCCESS.send(sender, placeholders);
            return;
        }

        // Other player gm change
        Player player = Bukkit.getPlayer(args[1]);
        placeholders.put("player", args[1]);

        if (player == null) {
            Lang.Message.COMMAND_GM_FAIL_PLAYER_DOESNT_EXIST.send(sender, placeholders);
            return;
        }


        addPlayerPlaceholders(placeholders, player);

        player.setGameMode(gamemode);
        Lang.Message.COMMAND_GM_SUCCESS_OTHER.send(sender, placeholders);
        if (args.length > 2 && args[2].equalsIgnoreCase("-s")) return;

        Lang.Message.COMMAND_GM_SUCCESS_RECEIVE.send(player, placeholders);
    }

    private GameMode getGamemode(String gm) {
        int gmNumber = -1;
        try {
            gmNumber = Integer.parseInt(gm);
        } catch (NumberFormatException ignored) {}

        GameMode gamemode = GameMode.getByValue(gmNumber);
        if (gamemode == null) {
            try {
                gamemode = GameMode.valueOf(gm.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        return gamemode;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length > 2) return completions;

        // Gamemode list
        if (args.length == 1) {
            String arg1 = args[0].toLowerCase();
            for (GameMode gameMode : GameMode.values()) {
                String name = gameMode.name().toLowerCase();
                if (!sender.hasPermission(String.format("%s.%s", PERMISSION_BASE, name))) continue;

                if (name.startsWith(arg1)) completions.add(name);
            }

            return completions;
        }

        if (!sender.hasPermission(PERMISSION_OTHERS)) return completions;

        // Player list
        return getPlayerListCompletions(sender, args[1]);
    }
}
