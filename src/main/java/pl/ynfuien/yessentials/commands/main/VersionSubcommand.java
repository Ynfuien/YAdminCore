package pl.ynfuien.yessentials.commands.main;

import io.papermc.paper.plugin.configuration.PluginMeta;
import org.bukkit.command.CommandSender;
import pl.ynfuien.yessentials.YEssentials;
import pl.ynfuien.yessentials.commands.Subcommand;
import pl.ynfuien.yessentials.utils.Lang;

import java.util.HashMap;
import java.util.List;

public class VersionSubcommand implements Subcommand {
    @Override
    public String permission() {
        return "yessentials.command.main."+name();
    }

    @Override
    public String name() {
        return "version";
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
        PluginMeta info = YEssentials.getInstance().getPluginMeta();

        placeholders.put("name", info.getName());
        placeholders.put("version", info.getVersion());
        placeholders.put("author", info.getAuthors().get(0));
        placeholders.put("description", info.getDescription());
        placeholders.put("website", info.getWebsite());

        Lang.Message.COMMAND_MAIN_VERSION.send(sender, placeholders);
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return null;
    }
}
