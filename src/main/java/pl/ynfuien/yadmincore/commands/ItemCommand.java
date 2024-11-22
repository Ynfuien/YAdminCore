package pl.ynfuien.yadmincore.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.ynfuien.yadmincore.YAdminCore;
import pl.ynfuien.yadmincore.utils.ItemGiver;
import pl.ynfuien.yadmincore.utils.Lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemCommand extends YCommand {
    public ItemCommand(YAdminCore instance) {
        super(instance);
    }

    @Override
    protected void run(@NotNull CommandSender sender, @NotNull String[] args, @NotNull HashMap<String, Object> placeholders) {
        if (!(sender instanceof Player)) {
            Lang.Message.COMMAND_ITEM_FAIL_NOT_PLAYER.send(sender, placeholders);
            return;
        }

        if (args.length == 0) {
            Lang.Message.COMMAND_ITEM_USAGE.send(sender, placeholders);
            return;
        }

        Material item = Material.matchMaterial(args[0]);
        if (item == null) {
            Lang.Message.COMMAND_ITEM_FAIL_ITEM_DOESNT_EXIST.send(sender, placeholders);
            return;
        }

        if (item.isAir() || !item.isItem()) {
            Lang.Message.COMMAND_ITEM_FAIL_ITEM_UNOBTAINABLE.send(sender, placeholders);
            return;
        }

        int amount = 1;
        if (args.length > 1) {
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                Lang.Message.COMMAND_ITEM_FAIL_INCORRECT_AMOUNT.send(sender, placeholders);
                return;
            }

            if (amount < 1) {
                Lang.Message.COMMAND_ITEM_FAIL_INCORRECT_AMOUNT.send(sender, placeholders);
                return;
            }
        }

        Player p = (Player) sender;
        ItemGiver.give(p, new ItemStack(item, amount), config.getBoolean("item.extend-max-stack-size"));

        placeholders.put("item", item.name().toLowerCase());
        placeholders.put("amount", amount);
        Lang.Message.COMMAND_ITEM_SUCCESS.send(p, placeholders);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        // Item list
        if (args.length == 1) {
            String arg1 = args[0].toLowerCase();

            for (Material item : Material.values()) {
                if (item.isAir()) continue;
                if (!item.isItem()) continue;

                String name = item.name().toLowerCase();
                if (name.startsWith(arg1)) {
                    completions.add(name);
                }
            }

            return completions;
        }

        // Amount
        if (args.length == 2) {
            String arg2 = args[1];

            for (String amount : new String[] {"1", "16", "32", "64"}) {
                if (amount.startsWith(arg2)) {
                    completions.add(amount);
                }
            }

            return completions;
        }

        return completions;
    }
}
