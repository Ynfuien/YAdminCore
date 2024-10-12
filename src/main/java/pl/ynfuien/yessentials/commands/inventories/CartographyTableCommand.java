package pl.ynfuien.yessentials.commands.inventories;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.ynfuien.yessentials.YEssentials;
import pl.ynfuien.yessentials.commands.YCommand;
import pl.ynfuien.yessentials.utils.Lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CartographyTableCommand extends YCommand {
    public CartographyTableCommand(YEssentials instance) {
        super(instance);
    }

    @Override
    protected void run(@NotNull CommandSender sender, @NotNull String[] args, @NotNull HashMap<String, Object> placeholders) {
        if (!(sender instanceof Player)) {
            Lang.Message.COMMAND_INVENTORIES_FAIL_ONLY_PLAYER.send(sender, placeholders);
            return;
        }

        ((Player) sender).openCartographyTable(null, true);
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
