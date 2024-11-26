package pl.ynfuien.yadmincore.commands;

import io.papermc.paper.entity.TeleportFlag;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.ynfuien.yadmincore.YAdminCore;
import pl.ynfuien.yadmincore.utils.Lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TpHereCommand extends YCommand {
    public TpHereCommand(YAdminCore instance) {
        super(instance);
    }

    @Override
    protected void run(@NotNull CommandSender sender, @NotNull String[] args, @NotNull HashMap<String, Object> placeholders) {
        if (!(sender instanceof Player executor)) {
            Lang.Message.COMMAND_TPHERE_FAIL_NEED_PLAYER.send(sender, placeholders);
            return;
        }

        if (args.length == 0) {
            Lang.Message.COMMAND_TPHERE_USAGE.send(sender, placeholders);
            return;
        }


        Player player = Bukkit.getPlayer(args[0]);
        placeholders.put("player", args[0]);
        placeholders.put("executor-username", executor.getName());
        placeholders.put("executor-display-name", MiniMessage.miniMessage().serialize(executor.displayName()));

        if (player == null) {
            Lang.Message.COMMAND_TPHERE_FAIL_PLAYER_DOESNT_EXIST.send(sender, placeholders);
            return;
        }

        placeholders.put("player-username", player.getName());
        placeholders.put("player-display-name", MiniMessage.miniMessage().serialize(player.displayName()));

        // Using scheduler to omit "moved too quickly" console logs
        // Idea from: https://www.spigotmc.org/threads/moved-too-quickly-warn-after-player-teleport.599036/#post-4569377
        Bukkit.getScheduler().runTask(instance, () -> {
            if (!player.teleport(executor.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND, TeleportFlag.EntityState.RETAIN_PASSENGERS)) {
                Lang.Message.COMMAND_TPHERE_FAIL.send(sender, placeholders);
                return;
            }

            Lang.Message.COMMAND_TPHERE_SUCCESS.send(sender, placeholders);
            if (args.length > 1 && args[1].equalsIgnoreCase("-s")) return;

            Lang.Message.COMMAND_TPHERE_SUCCESS_RECEIVE.send(player, placeholders);
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
