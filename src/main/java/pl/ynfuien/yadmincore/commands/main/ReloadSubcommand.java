package pl.ynfuien.yadmincore.commands.main;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.ynfuien.yadmincore.YAdminCore;
import pl.ynfuien.yadmincore.commands.Subcommand;
import pl.ynfuien.yadmincore.utils.Lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReloadSubcommand implements Subcommand {
    @Override
    public String permission() {
        return null;
    }

    @Override
    public String name() {
        return "reload";
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public void run(CommandSender sender, String[] args, HashMap<String, Object> placeholders) {
        // Reload plugin
        boolean success = YAdminCore.getInstance().reloadPlugin();

        // Check if reload was success
        if (success) {
            // Send success message to console if sender is player
            if (sender instanceof Player) {
                Lang.Message.COMMAND_MAIN_RELOAD_SUCCESS.send(Bukkit.getConsoleSender());
            }
            // Send success message to sender
            Lang.Message.COMMAND_MAIN_RELOAD_SUCCESS.send(sender);
            return;
        }

        // Send fail message to console if sender is player
        if (sender instanceof Player) {
            Lang.Message.COMMAND_MAIN_RELOAD_FAIL.send(Bukkit.getConsoleSender());
        }
        // Send fail message to sender
        Lang.Message.COMMAND_MAIN_RELOAD_FAIL.send(sender);
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
