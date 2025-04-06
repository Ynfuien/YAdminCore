package pl.ynfuien.yadmincore.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.ynfuien.yadmincore.YAdminCore;
import pl.ynfuien.yadmincore.data.Storage;
import pl.ynfuien.yadmincore.utils.Lang;
import pl.ynfuien.ydevlib.messages.Messenger;
import pl.ynfuien.ydevlib.utils.DoubleFormatter;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EntityCommand extends YCommand {
    private final static String PERMISSION_BASE = "yadmincore.entity";
    private final static String USE_SELECTORS_PERMISSION = PERMISSION_BASE + ".selectors";

    private final static Lang.Message YES_MESSAGE = Lang.Message.COMMAND_ENTITY_INFO_PLACEHOLDER_TRUE;
    private final static Lang.Message NO_MESSAGE = Lang.Message.COMMAND_ENTITY_INFO_PLACEHOLDER_FALSE;

    private static final DoubleFormatter df = new DoubleFormatter().setMaxDecimalPlaces(2);
    private static final double DAYS_IN_A_YEAR = 365.242;

    public EntityCommand(YAdminCore instance) {
        super(instance);
    }

    @Override
    protected void run(@NotNull CommandSender sender, @NotNull String[] args, @NotNull HashMap<String, Object> placeholders) {
        if (args.length == 0) {
            Lang.Message.COMMAND_ENTITY_USAGE.send(sender, placeholders);
            return;
        }


        String arg = args[0];
        List<Entity> targetEntities = getSelectedEntities(sender, arg, USE_SELECTORS_PERMISSION);

        if (targetEntities == null) {
            Lang.Message.COMMAND_ENTITY_FAIL_PLAYER_DOESNT_EXIST.send(sender, placeholders);
            return;
        }

        if (targetEntities.isEmpty()) {
            if (arg.startsWith("@")) {
                Lang.Message.COMMAND_ENTITY_FAIL_SELECTOR_NO_TARGETS.send(sender, placeholders);
                return;
            }

            Lang.Message.COMMAND_ENTITY_FAIL_SELECTOR_TARGET_DOESNT_EXIST.send(sender, placeholders);
            return;
        }

        if (targetEntities.size() > 1) {
            Lang.Message.COMMAND_ENTITY_FAIL_SELECTOR_MANY_TARGETS.send(sender, placeholders);
            return;
        }


        Entity target = targetEntities.get(0);
        addEntityPlaceholders(placeholders, target, null);

        // Health
        if (target instanceof LivingEntity livingEntity) {
            AttributeInstance att = livingEntity.getAttribute(Attribute.MAX_HEALTH);
            double maxHealth = att == null ? 20 : att.getValue();
            double health = livingEntity.getHealth();
            placeholders.put("health-max-points", df.format(maxHealth));
            placeholders.put("health-points", df.format(health));
            placeholders.put("health-max-hearts", df.format(maxHealth / 2));
            placeholders.put("health-hearts", df.format(health / 2));
        }

        // Location
        Location loc = target.getLocation();
        placeholders.put("loc-world", loc.getWorld().getName());
        placeholders.put("loc-x", df.format(loc.getX()));
        placeholders.put("loc-y", df.format(loc.getY()));
        placeholders.put("loc-z", df.format(loc.getZ()));
        placeholders.put("loc-yaw", df.format(loc.getYaw()));
        placeholders.put("loc-pitch", df.format(loc.getPitch()));


        if (target instanceof Player p) {
            // IP
            InetSocketAddress socketAddress = p.getAddress();
            String ip = socketAddress != null ? socketAddress.getAddress().getHostAddress() : "N/A";
            placeholders.put("ip", ip);

            // Time
            int secondsPlayed = p.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;
            double minutesPlayed = (double) secondsPlayed / 60;
            double hoursPlayed = minutesPlayed / 60;
            double daysPlayed = hoursPlayed / 24;
            double weeksPlayed = daysPlayed / 7;
            double monthsPlayed = daysPlayed / (DAYS_IN_A_YEAR / 12);
            double yearsPlayed = daysPlayed / DAYS_IN_A_YEAR;

            placeholders.put("time-total-seconds", secondsPlayed);
            placeholders.put("time-total-minutes", df.format(minutesPlayed));
            placeholders.put("time-total-hours", df.format(hoursPlayed));
            placeholders.put("time-total-days", df.format(daysPlayed));
            placeholders.put("time-total-weeks", df.format(weeksPlayed));
            placeholders.put("time-total-months", df.format(monthsPlayed));
            placeholders.put("time-total-years", df.format(yearsPlayed));

            placeholders.put("time-days", (int) daysPlayed);
            placeholders.put("time-hours", (int) hoursPlayed % 24);
            placeholders.put("time-minutes", (int) minutesPlayed % (24 * 60));
            placeholders.put("time-seconds", secondsPlayed % (24 * 60 * 60));


            // Food
            int maxFoodLevel = 20;
            int foodLevel = p.getFoodLevel();
            placeholders.put("hunger-max-points", df.format(maxFoodLevel));
            placeholders.put("hunger-points", df.format(foodLevel));
            placeholders.put("hunger-max-things", df.format((double) maxFoodLevel / 2));
            placeholders.put("hunger-things", df.format((double) foodLevel / 2));

            // Saturation
            float maxSaturation = 20;
            float saturation = p.getSaturation();
            placeholders.put("saturation-max-points", df.format(maxSaturation));
            placeholders.put("saturation-points", df.format(saturation));
            placeholders.put("saturation-max-things", df.format((double) maxSaturation / 2));
            placeholders.put("saturation-things", df.format((double) saturation / 2));


            // Gamemode
            String gm = "none";
            switch (p.getGameMode()) {
                case CREATIVE -> gm = Lang.Message.COMMAND_GM_CREATIVE.get();
                case SURVIVAL -> gm = Lang.Message.COMMAND_GM_SURVIVAL.get();
                case ADVENTURE -> gm = Lang.Message.COMMAND_GM_ADVENTURE.get();
                case SPECTATOR -> gm = Lang.Message.COMMAND_GM_SPECTATOR.get();
            }
            placeholders.put("gamemode", gm);

            // Operator
            placeholders.put("op", p.isOp() ? YES_MESSAGE.get() : NO_MESSAGE.get());

            // Godmode
            String godmode = Lang.Message.COMMAND_GOD_ENABLED.get();
            if (!Storage.getUser(p.getUniqueId()).isGodModeEnabled()) godmode = Lang.Message.COMMAND_GOD_DISABLED.get();
            placeholders.put("godmode", godmode);

            // Fly
            placeholders.put("flying", p.isFlying() ? YES_MESSAGE.get() : NO_MESSAGE.get());
            placeholders.put("can-fly", p.getAllowFlight() ? YES_MESSAGE.get() : NO_MESSAGE.get());


            HashMap<String, String > infoFields = getFields();
            for (String fieldName : infoFields.keySet()) {
                String result = "";
                if (sender.hasPermission(String.format("%s.field.%s", PERMISSION_BASE, fieldName))) {
                    String field = infoFields.get(fieldName);
                    result = MiniMessage.miniMessage().serialize(Messenger.parseMessage(p, field, placeholders));
                }

                placeholders.put("field-" + fieldName, result);
            }

            Lang.Message.COMMAND_ENTITY_INFO_PLAYER.send(sender, placeholders);
            return;
        }

        // Any entities
        HashMap<String, String > infoFields = getFields();
        for (String fieldName : infoFields.keySet()) {
            String field = infoFields.get(fieldName);
            String result = MiniMessage.miniMessage().serialize(Messenger.parseMessage(target, field, placeholders));

            if (fieldName.equalsIgnoreCase("health") && !(target instanceof LivingEntity)) result = "";

            placeholders.put("field-" + fieldName, result);
        }

        Lang.Message.COMMAND_ENTITY_INFO_OTHER.send(sender, placeholders);
    }

    private HashMap<String, String> getFields() {
        ConfigurationSection fieldsSection = config.getConfigurationSection("entity.fields");

        HashMap<String, String> fields = new HashMap<>();
        if (fieldsSection == null) return fields;

        for (String fieldName : fieldsSection.getKeys(false)) {
            fields.put(fieldName, fieldsSection.getString(fieldName));
        }

        return fields;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length > 1) return completions;

        String arg = args[0].toLowerCase();

        // Target selectors
        if (sender.hasPermission(USE_SELECTORS_PERMISSION)) completions.addAll(getSelectorCompletions(arg));

        // Target entity uuid
        if (sender instanceof Player p) {
            String uuid = getTargetEntityUUID(p, USE_SELECTORS_PERMISSION);
            if (uuid != null && uuid.startsWith(arg)) completions.add(uuid);
        }

        // Player list
        completions.addAll(getPlayerListCompletions(sender, arg));
        return completions;
    }
}
