package pl.ynfuien.yadmincore.commands;

import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;

public interface Subcommand {
    String permission();

    String name();

    String description();

    String usage();

    void run(CommandSender sender, String[] args, HashMap<String, Object> placeholders);

    List<String> getTabCompletions(CommandSender sender, String[] args);
}
