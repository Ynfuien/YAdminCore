package pl.ynfuien.yadmincore.commands;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.ynfuien.yadmincore.YAdminCore;
import pl.ynfuien.yadmincore.utils.Lang;
import pl.ynfuien.ydevlib.utils.DoubleFormatter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class HealCommand extends YCommand {
    private static final String PERMISSION_OTHERS = "yadmincore.command.heal.others";
    private final DoubleFormatter df = new DoubleFormatter();

    public HealCommand(YAdminCore instance) {
        super(instance);
    }

    @Override
    protected void run(@NotNull CommandSender sender, @NotNull String[] args, @NotNull HashMap<String, Object> placeholders) {
        Player toHeal;

        if (args.length == 0 || !sender.hasPermission(PERMISSION_OTHERS)) {
            if (!(sender instanceof Player)) {
                Lang.Message.COMMAND_HEAL_FAIL_NEED_PLAYER.send(sender, placeholders);
                return;
            }

            toHeal = (Player) sender;
        } else {
            String username = args[0];
            toHeal = Bukkit.getPlayer(username);
            if (toHeal == null) {
                placeholders.put("player", username);
                Lang.Message.COMMAND_HEAL_FAIL_PLAYER_DOESNT_EXIST.send(sender, placeholders);
                return;
            }
        }

        heal(toHeal, placeholders);
        addPlayerPlaceholders(placeholders, toHeal);
        if (sender.equals(toHeal)) {
            Lang.Message.COMMAND_HEAL_SUCCESS_YOURSELF.send(sender, placeholders);
            return;
        }

        Lang.Message.COMMAND_HEAL_SUCCESS_ANOTHER_PLAYER.send(sender, placeholders);
        if (args.length > 1 && args[1].equalsIgnoreCase("-s")) return;
        Lang.Message.COMMAND_HEAL_SUCCESS_RECEIVE.send(toHeal, placeholders);
    }

    private void heal(Player p, HashMap<String, Object> placeholders) {
        // Health
        if (config.getBoolean("heal.heal")) {
            AttributeInstance att = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            double maxHealth = att == null ? 20 : att.getValue();
            double health = p.getHealth();
            double healed = maxHealth - health;
            placeholders.put("max-health", df.format(maxHealth));
            placeholders.put("health", df.format(health));
            placeholders.put("healed", df.format(healed));

            p.setHealth(maxHealth);
        }

        // Food
        if (config.getBoolean("heal.feed")) {
            int maxFoodLevel = 20;
            int foodLevel = p.getFoodLevel();
            int feed = maxFoodLevel - foodLevel;
            placeholders.put("max-food", maxFoodLevel);
            placeholders.put("food", foodLevel);
            placeholders.put("feed", feed);

            p.setFoodLevel(maxFoodLevel);
        }

        // Saturation
        if (config.getBoolean("heal.saturate")) {
            float maxSaturation = 20;
            float saturation = p.getSaturation();
            float saturated = maxSaturation - saturation;
            placeholders.put("max-saturation", maxSaturation);
            placeholders.put("saturation", saturation);
            placeholders.put("saturated", saturated);

            p.setSaturation(maxSaturation);
        }

        // Clear effects
        if (config.getBoolean("heal.clear-effects")) {
            Collection<PotionEffect> potionEffects = p.getActivePotionEffects();
            placeholders.put("cleared-effects", potionEffects.size());

            for (PotionEffect effect : potionEffects) {
                p.removePotionEffect(effect.getType());
            }
        }

        // Stop burning
        if (config.getBoolean("heal.stop-burning")) {
            p.setFireTicks(0);
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length > 1) return completions;
        if (!sender.hasPermission(PERMISSION_OTHERS)) return completions;

        // Player list
        return getPlayerListCompletions(sender, args[0]);
    }
}
