package pl.ynfuien.yadmincore.commands.main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.ynfuien.yadmincore.commands.Subcommand;
import pl.ynfuien.yadmincore.utils.Lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainCommand implements CommandExecutor, TabCompleter {
    public static Subcommand[] subcommands = {
            new ReloadSubcommand(),
            new VersionSubcommand()
    };

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        HashMap<String, Object> placeholders = new HashMap<>() {{put("command", label);}};

        // Run help subcommand if none is provided
        if (args.length == 0) {
            Lang.Message.COMMAND_MAIN_USAGE.send(sender, placeholders);
            return true;
        }

        // Loop through and check every subcommand
        String arg1 = args[0].toLowerCase();
        for (Subcommand cmd : subcommands) {
            if (!cmd.name().equals(arg1)) continue;

            String[] argsLeft = Arrays.copyOfRange(args, 1, args.length);
            cmd.run(sender, argsLeft, placeholders);
            return true;
        }

        Lang.Message.COMMAND_MAIN_USAGE.send(sender, placeholders);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        // Get commands the sender has permissions for
        List<Subcommand> canUse = Arrays.stream(subcommands).filter(cmd -> sender.hasPermission(cmd.permission())).toList();
        if (canUse.isEmpty()) return completions;

        //// Tab completion for subcommands
        String arg1 = args[0].toLowerCase();
        if (args.length == 1) {
            for (Subcommand cmd : canUse) {
                String name = cmd.name();

                if (name.startsWith(args[0])) {
                    completions.add(name);
                }
            }

            return completions;
        }

        //// Tab completion for subcommand arguments

        // Get provided command in first arg
        Subcommand subcommand = canUse.stream().filter(cmd -> cmd.name().equals(arg1)).findAny().orElse(null);
        if (subcommand == null) return completions;

        // Get completions from provided command and return them if there are any
        List<String> subcommandCompletions = subcommand.getTabCompletions(sender, Arrays.copyOfRange(args, 1, args.length));
        if (subcommandCompletions != null) return subcommandCompletions;

        return completions;
    }
}
