package pl.ynfuien.yadmincore.commands;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
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
    private static final Registry<@NotNull Enchantment> enchantmentRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);

    public EnchantCommand(YAdminCore instance) {
        super(instance);
    }

    @Override
    protected void run(@NotNull CommandSender sender, @NotNull String[] args, @NotNull HashMap<String, Object> placeholders) {
        if (!(sender instanceof Player p)) {
            Lang.Message.COMMAND_ENCHANT_FAIL_NOT_A_PLAYER.send(sender, placeholders);
            return;
        }
        if (args.length == 0) {
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
        String arg1 = args[0].toLowerCase();
        if (args.length == 1) {
            Enchantment[] enchantments = enchantmentRegistry.stream().toArray(Enchantment[]::new);

            for (Enchantment enchantment : enchantments) {
                NamespacedKey namespacedKey = enchantment.getKey();

                String key = namespacedKey.asString();
                if (key.contains(arg1)) completions.add(key);
            }

            return completions;
        }

        NamespacedKey namespacedKey = NamespacedKey.fromString(arg1);
        if (namespacedKey == null) return completions;

        Enchantment enchantment = enchantmentRegistry.get(namespacedKey);
        if (enchantment == null) return completions;

        String arg2 = args[1];
        for (int i = 0; i < enchantment.getMaxLevel(); i++) {
            String stringValue = String.valueOf(i + 1);
            if (stringValue.startsWith(arg2)) completions.add(stringValue);
        }

        return completions;
    }
}
