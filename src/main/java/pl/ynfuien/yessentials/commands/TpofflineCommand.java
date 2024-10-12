package pl.ynfuien.yessentials.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.ynfuien.yessentials.YEssentials;
import pl.ynfuien.yessentials.data.Storage;
import pl.ynfuien.yessentials.utils.Lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TpofflineCommand extends YCommand {
    public TpofflineCommand(YEssentials instance) {
        super(instance);
    }

    @Override
    protected void run(@NotNull CommandSender sender, @NotNull String[] args, @NotNull HashMap<String, Object> placeholders) {
        if (!(sender instanceof Player)) {
            Lang.Message.COMMAND_TPOFFLINE_FAIL_ONLY_PLAYER.send(sender, placeholders);
            return;
        }

        if (args.length == 0) {
            Lang.Message.COMMAND_TPOFFLINE_USAGE.send(sender, placeholders);
            return;
        }

        OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
        placeholders.put("player", args[0]);

        if (!p.isOnline() && !p.hasPlayedBefore()){
            Lang.Message.COMMAND_TPOFFLINE_FAIL_PLAYER_DOESNT_EXIST.send(sender, placeholders);
            return;
        }

        addPlayerPlaceholders(placeholders, p);

        Location loc = Storage.getUser(p.getUniqueId()).getLogoutLocation();
        if (loc == null) {
            Lang.Message.COMMAND_TPOFFLINE_FAIL_NO_LOGOUT_LOCATION.send(sender, placeholders);
            return;
        }

        Bukkit.getScheduler().runTask(instance, () -> {
            if (!((Player) sender).teleport(loc, PlayerTeleportEvent.TeleportCause.COMMAND)) {
                Lang.Message.COMMAND_TPOFFLINE_FAIL.send(sender, placeholders);
                return;
            }

            Lang.Message.COMMAND_TPOFFLINE_SUCCESS.send(sender, placeholders);
        });
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        // Player list
        if (args.length == 1) return getPlayerListCompletions(sender, args[0]);

        return completions;
    }
}
