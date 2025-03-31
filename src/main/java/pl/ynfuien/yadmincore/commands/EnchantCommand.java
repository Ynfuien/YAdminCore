package pl.ynfuien.yadmincore.commands;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
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

public class EnchantCommand extends YCommand {
    public EnchantCommand(YAdminCore instance) {
        super(instance);
    }

    @Override
    protected void run(@NotNull CommandSender sender, @NotNull String[] args, @NotNull HashMap<String, Object> placeholders) {
        if (!(sender instanceof Player p)) {
            Lang.Message.COMMAND_ENCHANT_FAIL_NOT_A_PLAYER.send(sender, placeholders);
            return;
        }

        if (args.length < 2) {
            Lang.Message.COMMAND_ENCHANT_USAGE.send(sender, placeholders);
            return;
        }

        ItemStack item = p.getInventory().getItemInMainHand();
        if (item.isEmpty()) {
            Lang.Message.COMMAND_ENCHANT_FAIL_NO_ITEM.send(sender, placeholders);
            return;
        }



//        ItemGiver.give(p, new ItemStack(item, amount), config.getBoolean("item.extend-max-stack-size"));
//
//        placeholders.put("item", item.name().toLowerCase());
//        placeholders.put("amount", amount);
        Lang.Message.COMMAND_ENCHANT_SUCCESS.send(p, placeholders);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length > 2) return completions;

        // Enchantments
        if (args.length == 1) {
            Enchantment[] enchantments = Registry.ENCHANTMENT.stream().toArray(Enchantment[]::new);
            String arg1 = args[0].toLowerCase();

            for (Enchantment enchantment : enchantments) {
                NamespacedKey namespacedKey = enchantment.getKey();
//                if (item.isAir()) continue;
//                if (!item.isItem()) continue;

//                String name = item.name().toLowerCase();
//                if (name.startsWith(arg1)) {
//                    completions.add(name);
//                }
            }

            return completions;
        }

//        // Level
//        if (args.length == 2) {
//            String arg2 = args[1];
//
//            for (String amount : new String[] {"1", "16", "32", "64"}) {
//                if (amount.startsWith(arg2)) {
//                    completions.add(amount);
//                }
//            }
//
//            return completions;
//        }

        return completions;
    }
}
