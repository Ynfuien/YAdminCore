package pl.ynfuien.yessentials.commands.main;

import org.bukkit.command.CommandSender;
import pl.ynfuien.yessentials.commands.Subcommand;
import pl.ynfuien.yessentials.utils.Lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HelpSubcommand implements Subcommand {
    @Override
    public String permission() {
        return "yessentials.main";
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
