package pl.ynfuien.yadmincore.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.ynfuien.yadmincore.YAdminCore;
import pl.ynfuien.yadmincore.data.Storage;
import pl.ynfuien.yadmincore.utils.Lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BackCommand extends YCommand {
    private static final String PERMISSION_OTHERS = "yadmincore.command.back.others";
    public BackCommand(YAdminCore instance) {
        super(instance);
    }

    @Override
    protected void run(@NotNull CommandSender sender, @NotNull String[] args, @NotNull HashMap<String, Object> placeholders) {
        Player p;

        if (args.length == 0 || !sender.hasPermission(PERMISSION_OTHERS)) {
            if (!(sender instanceof Player)) {
                Lang.Message.COMMAND_BACK_FAIL_ONLY_PLAYER.send(sender, placeholders);
                return;
            }

            p = (Player) sender;
        } else {
            p = Bukkit.getPlayer(args[0]);
            placeholders.put("player", args[0]);

            if (p == null){
                Lang.Message.COMMAND_BACK_FAIL_PLAYER_DOESNT_EXIST.send(sender, placeholders);
                return;
            }
        }

        addPlayerPlaceholders(placeholders, p);

        Location loc = Storage.getUser(p.getUniqueId()).getLastLocation();
        if (loc == null) {
            Lang.Message.COMMAND_BACK_FAIL_NO_LAST_LOCATION.send(sender, placeholders);
            return;
        }

        Bukkit.getScheduler().runTask(instance, () -> {
            if (!p.teleport(loc, PlayerTeleportEvent.TeleportCause.COMMAND)) {
                Lang.Message.COMMAND_BACK_FAIL.send(sender, placeholders);
                return;
            }

            if (sender.equals(p)) {
                Lang.Message.COMMAND_BACK_SUCCESS.send(sender, placeholders);
                return;
            }

            Lang.Message.COMMAND_BACK_SUCCESS_OTHER.send(sender, placeholders);
            if (args.length > 1 && args[1].equalsIgnoreCase("-s")) return;
            Lang.Message.COMMAND_BACK_SUCCESS_RECEIVE.send(p, placeholders);
        });
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
