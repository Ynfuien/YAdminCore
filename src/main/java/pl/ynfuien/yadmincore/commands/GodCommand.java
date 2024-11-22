package pl.ynfuien.yadmincore.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.ynfuien.yadmincore.YAdminCore;
import pl.ynfuien.yadmincore.data.Storage;
import pl.ynfuien.yadmincore.data.User;
import pl.ynfuien.yadmincore.utils.Lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GodCommand extends YCommand {
    private static final String PERMISSION_OTHERS = "yadmincore.command.god.others";
    private final static Lang.Message enabledMsg = Lang.Message.COMMAND_GOD_ENABLED;
    private final static Lang.Message disabledMsg = Lang.Message.COMMAND_GOD_DISABLED;

    public GodCommand(YAdminCore instance) {
        super(instance);
    }

    @Override
    protected void run(@NotNull CommandSender sender, @NotNull String[] args, @NotNull HashMap<String, Object> placeholders) {
        if (args.length == 0 || !sender.hasPermission(PERMISSION_OTHERS)) {
            if (!(sender instanceof Player)) {
                Lang.Message.COMMAND_GOD_FAIL_NEED_PLAYER.send(sender, placeholders);
                return;
            }

            toggleGodmode((Player) sender, placeholders, "");
            Lang.Message.COMMAND_GOD_SUCCESS.send(sender, placeholders);
            return;
        }



        Player player = Bukkit.getPlayer(args[0]);
        placeholders.put("player", args[0]);

        if (player == null) {
            Lang.Message.COMMAND_GOD_FAIL_PLAYER_DOESNT_EXIST.send(sender, placeholders);
            return;
        }

        addPlayerPlaceholders(placeholders, player);

        toggleGodmode(player, placeholders, args.length > 1 ? args[1].toLowerCase() : "");
        Lang.Message.COMMAND_GOD_SUCCESS_OTHER.send(sender, placeholders);
        if (args.length > 1 && args[1].equalsIgnoreCase("-s")) return;
        if (sender.equals(player)) return;

        Lang.Message.COMMAND_GOD_SUCCESS_RECEIVE.send(player, placeholders);
    }

    private boolean toggleGodmode(Player p, HashMap<String, Object> ph, String arg) {
        boolean enable = false;
        boolean disable = false;

        if (arg.equals("enable")) enable = true;
        else if (arg.equals("disable")) disable = true;

        User user = Storage.getUser(p.getUniqueId());
        if (!enable && !disable) {
            if (user.isGodModeEnabled()) {
                user.setGodMode(false);
                ph.put("action", disabledMsg.get(ph));
                return false;
            }

            user.setGodMode(true);
            ph.put("action", enabledMsg.get(ph));
            return true;
        }

        if (enable) {
            user.setGodMode(true);
            ph.put("action", enabledMsg.get(ph));
            return true;
        }

        user.setGodMode(false);
        ph.put("action", disabledMsg.get(ph));
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
            if (s.startsWith(arg2)) completions.add(s);
        }

        return completions;
    }
}
