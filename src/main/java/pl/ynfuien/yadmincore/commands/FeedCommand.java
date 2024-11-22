package pl.ynfuien.yadmincore.commands;

import org.bukkit.Bukkit;
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

public class FeedCommand extends YCommand {
    private static final String PERMISSION_OTHERS = "yadmincore.command.feed.others";
    public FeedCommand(YAdminCore instance) {
        super(instance);
    }

    @Override
    protected void run(@NotNull CommandSender sender, @NotNull String[] args, @NotNull HashMap<String, Object> placeholders) {
        Player toFeed;

        if (args.length == 0 || !sender.hasPermission(PERMISSION_OTHERS)) {
            if (!(sender instanceof Player)) {
                Lang.Message.COMMAND_FEED_FAIL_NEED_PLAYER.send(sender, placeholders);
                return;
            }

            toFeed = (Player) sender;
        } else {
            String username = args[0];
            toFeed = Bukkit.getPlayer(username);
            if (toFeed == null) {
                placeholders.put("player", username);
                Lang.Message.COMMAND_FEED_FAIL_PLAYER_DOESNT_EXIST.send(sender, placeholders);
                return;
            }
        }

        feed(toFeed, placeholders);
        addPlayerPlaceholders(placeholders, toFeed);
        if (sender.equals(toFeed)) {
            Lang.Message.COMMAND_FEED_SUCCESS_YOURSELF.send(sender, placeholders);
            return;
        }

        Lang.Message.COMMAND_FEED_SUCCESS_ANOTHER_PLAYER.send(sender, placeholders);
        if (args.length > 1 && args[1].equalsIgnoreCase("-s")) return;
        Lang.Message.COMMAND_FEED_SUCCESS_RECEIVE.send(toFeed, placeholders);
    }

    private void feed(Player p, HashMap<String, Object> placeholders) {
        // Food
        if (config.getBoolean("heal.feed")) {
            int maxFoodLevel = 20;
            int foodLevel = p.getFoodLevel();
            int feed = maxFoodLevel - foodLevel;
            placeholders.put("max-food", maxFoodLevel);
            placeholders.put("food", foodLevel);
            placeholders.put("feed", feed);

            p.setFoodLevel(maxFoodLevel);
        }

        // Saturation
        if (config.getBoolean("heal.saturate")) {
            float maxSaturation = 20;
            float saturation = p.getSaturation();
            float saturated = maxSaturation - saturation;
            placeholders.put("max-saturation", maxSaturation);
            placeholders.put("saturation", saturation);
            placeholders.put("saturated", saturated);

            p.setSaturation(maxSaturation);
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length > 1) return completions;
        if (!sender.hasPermission(PERMISSION_OTHERS)) return completions;

        // Player list
        return getPlayerListCompletions(sender, args[0]);
    }
}
