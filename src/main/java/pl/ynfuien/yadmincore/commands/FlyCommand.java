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

public class FlyCommand extends YCommand {
    private static final String PERMISSION_OTHERS = "yadmincore.fly.others";
    private static final Lang.Message ENABLED_MSG = Lang.Message.COMMAND_FLY_ENABLED;
    private static final Lang.Message DISABLED_MSG = Lang.Message.COMMAND_FLY_DISABLED;

    public FlyCommand(YAdminCore instance) {
        super(instance);
    }

    @Override
    protected void run(@NotNull CommandSender sender, @NotNull String[] args, @NotNull HashMap<String, Object> placeholders) {
        if (args.length == 0 || !sender.hasPermission(PERMISSION_OTHERS)) {
            if (!(sender instanceof Player)) {
                Lang.Message.COMMAND_FLY_FAIL_NEED_PLAYER.send(sender, placeholders);
                return;
            }

            toggleFly((Player) sender, placeholders, "");
            Lang.Message.COMMAND_FLY_SUCCESS.send(sender, placeholders);
            return;
        }



        Player player = Bukkit.getPlayer(args[0]);
        placeholders.put("player", args[0]);

        if (player == null) {
            Lang.Message.COMMAND_FLY_FAIL_PLAYER_DOESNT_EXIST.send(sender, placeholders);
            return;
        }

        addPlayerPlaceholders(placeholders, player);

        toggleFly(player, placeholders, args.length > 1 ? args[1].toLowerCase() : "");
        Lang.Message.COMMAND_FLY_SUCCESS_OTHER.send(sender, placeholders);
        if (args.length > 1 && args[1].equalsIgnoreCase("-s")) return;
        if (sender.equals(player)) return;

        Lang.Message.COMMAND_FLY_SUCCESS_RECEIVE.send(player, placeholders);
    }

    private boolean toggleFly(Player p, HashMap<String, Object> ph, String arg) {
        boolean enable = false;
        boolean disable = false;

        if (arg.equals("enable")) enable = true;
        else if (arg.equals("disable")) disable = true;

        if (!enable && !disable) {
            if (p.getAllowFlight()) {
                p.setAllowFlight(false);
                ph.put("action", DISABLED_MSG.get(ph));
                return false;
            }

            p.setAllowFlight(true);
            ph.put("action", ENABLED_MSG.get(ph));
            return true;
        }

        if (enable) {
            p.setAllowFlight(true);
            ph.put("action", ENABLED_MSG.get(ph));
            return true;
        }

        p.setAllowFlight(false);
        ph.put("action", DISABLED_MSG.get(ph));
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length > 2) return completions;
        if (!sender.hasPermission(PERMISSION_OTHERS)) return completions;

        // Player list
        if (args.length == 1) return getPlayerListCompletions(sender, args[0]);


        String arg2 = args[1].toLowerCase();
        for (String s : new String[] {"enable", "disable"}) {
            if (s.startsWith(arg2)) {
                completions.add(s);
            }
        }

        return completions;
    }
}
