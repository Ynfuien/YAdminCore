package pl.ynfuien.yadmincore.commands.main;

import org.bukkit.command.CommandSender;
import pl.ynfuien.yadmincore.commands.Subcommand;
import pl.ynfuien.yadmincore.utils.Lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HelpSubcommand implements Subcommand {
    @Override
    public String permission() {
        return "yadmincore.main";
    }

    @Override
    public String name() {
        return "help";
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
        Lang.Message.COMMAND_MAIN_HELP.send(sender, placeholders);
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
