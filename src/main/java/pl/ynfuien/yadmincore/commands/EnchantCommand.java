package pl.ynfuien.yadmincore.commands;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
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
import pl.ynfuien.yadmincore.utils.Lang;
import pl.ynfuien.ydevlib.messages.colors.ColorFormatter;

import java.util.*;

public class EnchantCommand extends YCommand {
    private final static String PERMISSION_BASE = "yadmincore.enchant";
    private final static String PERMISSION_INCOMPATIBLE = PERMISSION_BASE + ".incompatible";
    private final static String PERMISSION_UNSUPPORTED_ITEM = PERMISSION_BASE + ".unsupported-item";
    private final static String PERMISSION_UNSAFE = PERMISSION_BASE + ".unsafe";

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

        String arg1 = args[0].toLowerCase();
        NamespacedKey namespacedKey = NamespacedKey.fromString(arg1);
        if (namespacedKey == null) {
            Lang.Message.COMMAND_ENCHANT_FAIL_INCORRECT_ENCHANT.send(sender, placeholders);
            return;
        }

        Enchantment enchantment = enchantmentRegistry.get(namespacedKey);
        if (enchantment == null) {
            Lang.Message.COMMAND_ENCHANT_FAIL_INCORRECT_ENCHANT.send(sender, placeholders);
            return;
        }

        int level = 1;
        if (args.length > 1) {
            try {
                level = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                Lang.Message.COMMAND_ENCHANT_FAIL_INCORRECT_LEVEL.send(sender, placeholders);
                return;
            }

            if (level < 0) level = 0;
            if (level > 255) level = 255;
        }


        placeholders.put("level", level);
        placeholders.put("item-name", item.getType().getKey().getKey());
        placeholders.put("item-name-translation-key", item.getType().translationKey());
        placeholders.put("item-display-name", ColorFormatter.SERIALIZER.serialize(item.effectiveName()));
        placeholders.put("enchantment-display-name", ColorFormatter.SERIALIZER.serialize(enchantment.displayName(level)));
        placeholders.put("enchantment-display-name-no-color", ColorFormatter.SERIALIZER.serialize(enchantment.displayName(level).color(null)));
        placeholders.put("enchantment-name", namespacedKey.getKey());
        placeholders.put("enchantment-key", namespacedKey.asString());

        //// Remove enchantment
        if (level == 0) {
            level = item.removeEnchantment(enchantment);

            Component displayName = level == 0 ? enchantment.description() : enchantment.displayName(level);
            placeholders.put("level", level);
            placeholders.put("enchantment-display-name", ColorFormatter.SERIALIZER.serialize(displayName));
            placeholders.put("enchantment-display-name-no-color", ColorFormatter.SERIALIZER.serialize(displayName.color(null)));

            Lang.Message.COMMAND_ENCHANT_SUCCESS_REMOVE.send(p, placeholders);
            return;
        }

        //// Add enchantment
        if (!p.hasPermission(PERMISSION_UNSAFE) && level > enchantment.getMaxLevel()) {
            Lang.Message.COMMAND_ENCHANT_FAIL_NO_PERMISSION_UNSAFE_LEVEL.send(sender, placeholders);
            return;
        }

        if (!p.hasPermission(PERMISSION_UNSUPPORTED_ITEM) && !enchantment.canEnchantItem(item)) {
            Lang.Message.COMMAND_ENCHANT_FAIL_NO_PERMISSION_UNSUPPORTED_ITEM.send(sender, placeholders);
            return;
        }

        if (!p.hasPermission(PERMISSION_INCOMPATIBLE)) {
            for (Enchantment enchant : item.getEnchantments().keySet()) {
                if (!enchantment.conflictsWith(enchant)) continue;
                if (enchantment.equals(enchant)) continue;

                Lang.Message.COMMAND_ENCHANT_FAIL_NO_PERMISSION_INCOMPATIBLE_ENCHANT.send(sender, placeholders);
                return;
            }
        }

        item.addUnsafeEnchantment(enchantment, level);
        Lang.Message.COMMAND_ENCHANT_SUCCESS.send(p, placeholders);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length > 2) return completions;

        if (!(sender instanceof Player p)) return completions;

        boolean smart = config.getBoolean("enchant.smart-tab-completions");
        ItemStack item = p.getInventory().getItemInMainHand();

        if (smart && item.isEmpty()) {
            completions.add("get-something-in-your-hand");
            return completions;
        }

        Set<Enchantment> itemEnchantments = item.getEnchantments().keySet();
        Set<Enchantment> applicableEnchantments = new HashSet<>();

        Enchantment[] enchantments = enchantmentRegistry.stream().toArray(Enchantment[]::new);

        if (smart) {
            allEnchantments: for (Enchantment enchantment : enchantments) {
                if (!p.hasPermission(PERMISSION_UNSUPPORTED_ITEM) && !enchantment.canEnchantItem(item)) continue;
                if (!p.hasPermission(PERMISSION_INCOMPATIBLE)) {
                    for (Enchantment itemEnchant : itemEnchantments) {
                        if (!enchantment.conflictsWith(itemEnchant)) continue;
                        if (enchantment.equals(itemEnchant)) continue;

                        continue allEnchantments;
                    }
                }

                applicableEnchantments.add(enchantment);
            }
        } else {
            applicableEnchantments.addAll(List.of(enchantments));
        }


        // Enchantments
        String arg1 = args[0].toLowerCase();
        if (args.length == 1) {
            for (Enchantment enchantment : applicableEnchantments) {
                NamespacedKey namespacedKey = enchantment.getKey();

                String key = namespacedKey.asString();
                if (key.contains(arg1)) completions.add(key);
            }

            if (smart && completions.isEmpty() && arg1.isEmpty()) completions.add("no-enchants-for-this-item");
            return completions;
        }

        NamespacedKey namespacedKey = NamespacedKey.fromString(arg1);
        if (namespacedKey == null) return completions;

        Enchantment enchantment = enchantmentRegistry.get(namespacedKey);
        if (enchantment == null) return completions;

        if (!applicableEnchantments.contains(enchantment)) return completions;

        String arg2 = args[1];
        for (int i = 0; i < enchantment.getMaxLevel(); i++) {
            String stringValue = String.valueOf(i + 1);
            if (stringValue.startsWith(arg2)) completions.add(stringValue);
        }

        if (itemEnchantments.contains(enchantment) && "0".startsWith(arg2)) completions.add("0");
        return completions;
    }
}
