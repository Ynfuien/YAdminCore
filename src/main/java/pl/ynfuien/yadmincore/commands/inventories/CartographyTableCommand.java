package pl.ynfuien.yadmincore.commands.inventories;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.ynfuien.yadmincore.YAdminCore;
import pl.ynfuien.yadmincore.commands.YCommand;
import pl.ynfuien.yadmincore.utils.Lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CartographyTableCommand extends YCommand {
    public CartographyTableCommand(YAdminCore instance) {
        super(instance);
    }

    @Override
    protected void run(@NotNull CommandSender sender, @NotNull String[] args, @NotNull HashMap<String, Object> placeholders) {
        if (!(sender instanceof Player p)) {
            Lang.Message.COMMAND_INVENTORIES_FAIL_ONLY_PLAYER.send(sender, placeholders);
            return;
        }

        p.openCartographyTable(null, true);
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
