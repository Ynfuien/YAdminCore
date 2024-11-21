package pl.ynfuien.yessentials.commands;

import io.papermc.paper.entity.TeleportFlag;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.ynfuien.ydevlib.messages.Messenger;
import pl.ynfuien.ydevlib.utils.DoubleFormatter;
import pl.ynfuien.yessentials.YEssentials;
import pl.ynfuien.yessentials.utils.Lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class TpCommand extends YCommand {
    private final static String PERMISSION_BASE = "yessentials.tp";
    private final static String PERMISSION_TP_OTHERS = PERMISSION_BASE + ".others";
    private final static String PERMISSION_TP_LOCATION = PERMISSION_BASE + ".location";
    private final static String PERMISSION_USE_SELECTORS = PERMISSION_BASE + ".selectors";

    private static final Pattern locationArgPattern = Pattern.compile("^[\\d\\-\\.,~]+$");

    private static final DoubleFormatter df = new DoubleFormatter().setMaxDecimalPlaces(2);


    public TpCommand(YEssentials instance) {
        super(instance);
    }

    @Override
    protected void run(@NotNull CommandSender sender, @NotNull String[] args, @NotNull HashMap<String, Object> placeholders) {
        if (args.length == 0) {
            Lang.Message.COMMAND_TP_USAGE.send(sender, placeholders);

            if (sender.hasPermission(PERMISSION_TP_OTHERS)) {
                Lang.Message.COMMAND_TP_USAGE_PLAYER_TO_PLAYER.send(sender, placeholders);
            }
            return;
        }

        boolean canTpLocation = sender.hasPermission(PERMISSION_TP_LOCATION);

        // Self tp to another player/entity
        if (args.length == 1 || (!sender.hasPermission(PERMISSION_TP_OTHERS) && !canTpLocation)) {
            if (!(sender instanceof Player)) {
                Lang.Message.COMMAND_TP_FAIL_ONLY_PLAYER.send(sender, placeholders);
                return;
            }

            Player p = (Player) sender;
            String destinationArg = args[0];
            List<Entity> destinationEntities = getSelectedEntities(sender, destinationArg, PERMISSION_USE_SELECTORS);
            placeholders.put("player", destinationArg);
            if (destinationEntities == null) {
                Lang.Message.COMMAND_TP_FAIL_PLAYER_DOESNT_EXIST.send(sender, placeholders);
                return;
            }

            if (destinationEntities.isEmpty()) {
                if (destinationArg.startsWith("@")) {
                    Lang.Message.COMMAND_TP_FAIL_SELECTOR_NO_DESTINATION.send(sender, placeholders);
                    return;
                }

                Lang.Message.COMMAND_TP_FAIL_SELECTOR_DESTINATION_DOESNT_EXIST.send(sender, placeholders);
                return;
            }

            if (destinationEntities.size() > 1) {
                Lang.Message.COMMAND_TP_FAIL_SELECTOR_MANY_DESTINATIONS.send(sender, placeholders);
                return;
            }

            Entity destination = destinationEntities.get(0);
            addPlayerPlaceholders(placeholders, p, "target");
            addEntityPlaceholders(placeholders, destination, "destination");

            // Using scheduler to omit "moved too quickly" console logs
            // Idea from: https://www.spigotmc.org/threads/moved-too-quickly-warn-after-player-teleport.599036/#post-4569377
            Bukkit.getScheduler().runTask(instance, () -> {
                if (!p.teleport(destination, PlayerTeleportEvent.TeleportCause.COMMAND)) {
                    Lang.Message.COMMAND_TP_FAIL.send(sender, placeholders);
                    return;
                }

                Lang.Message.COMMAND_TP_SUCCESS.send(sender, placeholders);
            });

            return;
        }

        // Tp entity/entities to another entity
        boolean entityToEntity = args.length == 2 || !canTpLocation;
        if (args.length == 3 && args[2].equalsIgnoreCase("-s")) entityToEntity = true;
        if (entityToEntity) {
            String targetArg = args[0];
            String destinationArg = args[1];

            List<Entity> targetEntities = getSelectedEntities(sender, targetArg, PERMISSION_USE_SELECTORS);
            List<Entity> destinationEntities = getSelectedEntities(sender, destinationArg, PERMISSION_USE_SELECTORS);

            // Target entity
            placeholders.put("player", targetArg);
            if (targetEntities == null) {
                Lang.Message.COMMAND_TP_FAIL_PLAYER_DOESNT_EXIST.send(sender, placeholders);
                return;
            }

            if (targetEntities.isEmpty()) {
                if (targetArg.startsWith("@")) {
                    Lang.Message.COMMAND_TP_FAIL_SELECTOR_NO_TARGETS.send(sender, placeholders);
                    return;
                }

                Lang.Message.COMMAND_TP_FAIL_SELECTOR_TARGET_DOESNT_EXIST.send(sender, placeholders);
                return;
            }

            // Destination entity
            placeholders.put("player", destinationArg);
            if (destinationEntities == null) {
                Lang.Message.COMMAND_TP_FAIL_PLAYER_DOESNT_EXIST.send(sender, placeholders);
                return;
            }

            if (destinationEntities.isEmpty()) {
                if (destinationArg.startsWith("@")) {
                    Lang.Message.COMMAND_TP_FAIL_SELECTOR_NO_DESTINATION.send(sender, placeholders);
                    return;
                }

                Lang.Message.COMMAND_TP_FAIL_SELECTOR_DESTINATION_DOESNT_EXIST.send(sender, placeholders);
                return;
            }

            if (destinationEntities.size() > 1) {
                Lang.Message.COMMAND_TP_FAIL_SELECTOR_MANY_DESTINATIONS.send(sender, placeholders);
                return;
            }


            Entity destination = destinationEntities.get(0);

            int targetCount = targetEntities.size();
            placeholders.put("target-count", targetCount);

            if (targetCount == 1) addEntityPlaceholders(placeholders, targetEntities.get(0), "target");
            addEntityPlaceholders(placeholders, destination, "destination");

            // Using scheduler to omit "moved too quickly" console logs
            Bukkit.getScheduler().runTask(instance, () -> {
                boolean silent = args.length > 2 && args[2].equalsIgnoreCase("-s");

                for (Entity e : targetEntities) {
                    if (!e.teleport(destination.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND, TeleportFlag.EntityState.RETAIN_PASSENGERS)) {
                        Messenger.send(sender, String.format("<click:copy_to_clipboard:'%s'>%s", e.getUniqueId(), e.getUniqueId()));
                        Lang.Message.COMMAND_TP_FAIL.send(sender, placeholders);
                        return;
                    }

                    if (silent) continue;
                    if (!(e instanceof Player)) continue;
                    if (e.equals(sender)) continue;
                    Lang.Message.COMMAND_TP_SUCCESS_RECEIVE_SELECTOR.send(e, placeholders);
                }


                if (targetCount > 1) {
                    Lang.Message.COMMAND_TP_SUCCESS_SELECTOR_MANY.send(sender, placeholders);
                    return;
                }

                Lang.Message.COMMAND_TP_SUCCESS_SELECTOR.send(sender, placeholders);
            });

            return;
        }


        List<Entity> targetEntities;
        Location destination;
        if (args.length == 3) {
            if (!(sender instanceof Player teleported)) {
                Lang.Message.COMMAND_TP_FAIL_ONLY_PLAYER.send(sender, placeholders);
                return;
            }

            targetEntities = new ArrayList<>();
            targetEntities.add(teleported);
            destination = getLocationFromArgs(teleported, args);
        } else {
            String targetArg = args[0];
            targetEntities = getSelectedEntities(sender, targetArg, PERMISSION_USE_SELECTORS);

            placeholders.put("player", targetArg);
            if (targetEntities == null) {
                Lang.Message.COMMAND_TP_FAIL_PLAYER_DOESNT_EXIST.send(sender, placeholders);
                return;
            }

            if (targetEntities.isEmpty()) {
                if (targetArg.startsWith("@")) {
                    Lang.Message.COMMAND_TP_FAIL_SELECTOR_NO_TARGETS.send(sender, placeholders);
                    return;
                }

                Lang.Message.COMMAND_TP_FAIL_SELECTOR_TARGET_DOESNT_EXIST.send(sender, placeholders);
                return;
            }

            destination = getLocationFromArgs(sender, Arrays.copyOfRange(args, 1, args.length));
        }
        if (destination == null) return;


        int targetCount = targetEntities.size();
        placeholders.put("target-count", targetCount);
        if (targetCount == 1) addEntityPlaceholders(placeholders, targetEntities.get(0), "target");

        placeholders.put("x", df.format(destination.getX()));
        placeholders.put("y", df.format(destination.getY()));
        placeholders.put("z", df.format(destination.getZ()));
        placeholders.put("yaw", df.format(destination.getYaw()));
        placeholders.put("pitch", df.format(destination.getPitch()));

        // Method that entity.teleportAsync uses
        destination.getWorld().getChunkAtAsyncUrgently(destination).thenAccept((chunk) -> {
            // Using scheduler to omit "moved too quickly" console logs
            Bukkit.getScheduler().runTask(instance, () -> {
                // Silent if: /tp <selector> <x> <y> <z> -s
                boolean silent = args.length > 4 && args[4].equalsIgnoreCase("-s");
                // Silent if: /tp <selector> <x> <y> <z> <yaw> <pitch> -s
                if (args.length > 6 && args[5].equalsIgnoreCase("-s")) silent = true;

                for (Entity target : targetEntities) {
                    if (!target.teleport(destination, PlayerTeleportEvent.TeleportCause.COMMAND, TeleportFlag.EntityState.RETAIN_PASSENGERS)) {
                        Lang.Message.COMMAND_TP_FAIL.send(sender, placeholders);
                        return;
                    }

                    if (silent) continue;
                    if (!(target instanceof Player)) continue;
                    if (target.equals(sender)) continue;
                    Lang.Message.COMMAND_TP_SUCCESS_RECEIVE_LOCATION.send(target, placeholders);
                }

                if (targetCount > 1) {
                    Lang.Message.COMMAND_TP_SUCCESS_MANY_TO_LOCATION.send(sender, placeholders);
                    return;
                }

                // If player executing the command was the target
                if (targetCount == 1 && targetEntities.get(0).equals(sender)) {
                    Lang.Message.COMMAND_TP_SUCCESS_LOCATION.send(sender, placeholders);
                    return;
                }

                Lang.Message.COMMAND_TP_SUCCESS_ENTITY_TO_LOCATION.send(sender, placeholders);
            });
        });
    }


    private static Location getLocationFromArgs(CommandSender sender, String[] args) {
        Location loc = getSenderLocation(sender);

        // X, y and z coordinates
        double[] xyz = new double[3];
        for (int i = 0; i < 3; i++) {
            String arg = args[i];
            if (!arg.matches(locationArgPattern.pattern())) {
                Lang.Message.COMMAND_TP_FAIL_INVALID_LOCATION_ARGUMENT.send(sender, new HashMap<>() {{put("argument", arg);}});
                return null;
            }

            if (arg.startsWith("~")) {
                double position = i == 0 ? loc.getX() : (i == 1 ? loc.getY() : loc.getZ());
                Double relative = arg.equals("~") ? 0 : parseDouble(sender, arg.substring(1));
                if (relative == null) return null;

                xyz[i] = position + relative;
                continue;
            }

            Double position = parseDouble(sender, arg);
            if (position == null) return null;
            // Center x and z if argument doesn't have specified decimal places
            if (i != 1 && !arg.contains(".")) position += 0.5;
            xyz[i] = position;
        }

        loc.set(xyz[0], xyz[1], xyz[2]);

        // Yaw and pitch
        if (args.length >= 5) {
            Double yaw = parseDouble(sender, args[3]);
            if (yaw == null) return null;
            Double pitch = parseDouble(sender, args[4]);
            if (pitch == null) return null;

            loc.setYaw(yaw.floatValue());
            loc.setPitch(pitch.floatValue());
        }

        return loc;
    }

    private static Location getSenderLocation(CommandSender sender) {
        if (sender instanceof Entity) return ((Entity) sender).getLocation().clone();
        if (sender instanceof BlockCommandSender) return ((BlockCommandSender) sender).getBlock().getLocation().toCenterLocation();

        return new Location(Bukkit.getWorlds().get(0), 0, 100, 0).toCenterLocation();
    }

    private static Double parseDouble(CommandSender sender, String string) {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException e) {
            Lang.Message.COMMAND_TP_FAIL_INVALID_LOCATION_ARGUMENT.send(sender, new HashMap<>() {{put("argument", string);}});
            return null;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        Player p = sender instanceof Player player ? player : null;
        if (sender.hasPermission(PERMISSION_TP_LOCATION) && p != null) {
            completions.addAll(getLocationCompletions(p, args));
        }


        int length = args.length;
        if (length == 1 || (sender.hasPermission(PERMISSION_TP_OTHERS) && length == 2)) {
            if (length == 2 && args[0].matches(locationArgPattern.pattern())) return completions;

            String arg = args[length - 1].toLowerCase();

            // Target selectors
            if (sender.hasPermission(PERMISSION_USE_SELECTORS)) {
                completions.addAll(getSelectorCompletions(arg));
            }

            // Target entity uuid
            String uuid = getTargetEntityUUID(p, PERMISSION_USE_SELECTORS);
            if (uuid != null && uuid.startsWith(arg)) completions.add(uuid);

            // Player list
            completions.addAll(getPlayerListCompletions(sender, arg));
            return completions;
        }

        return completions;
    }

    private static List<String> getLocationCompletions(Player p, String[] args) {
        List<String> completions = new ArrayList<>();

        int argsLength = args.length;
        String currentArg = args[args.length - 1].toLowerCase();

        // Return empty list if second argument is (probably) a player
        if (argsLength > 1 && !args[1].isBlank() && !args[1].matches(locationArgPattern.pattern())) return completions;
        // Decrease args length if first arg is (probably) a player
        if (!args[0].isBlank() && !args[0].matches(locationArgPattern.pattern())) {
            if (!p.hasPermission(PERMISSION_TP_OTHERS)) return completions;
            argsLength--;
        }
        if (argsLength < 1 || argsLength > 3) return completions;


        // Relative positions
        String[] relatives = new String[] {"~", "~", "~"};
        String completion = null;
        for (String value : Arrays.copyOfRange(relatives, argsLength - 1, relatives.length)) {
            completion = completion == null ? value : completion + " " + value;
            if (completion.startsWith(currentArg)) completions.add(completion);
        }

        // Target block location
        RayTraceResult result = p.rayTraceBlocks(5, FluidCollisionMode.NEVER);
        if (result == null) return completions;

        Vector hit = result.getHitPosition();

        String x = df.format(hit.getX());
        String y = df.format(hit.getY());
        String z = df.format(hit.getZ());

        String[] xyz = new String[] {x, y, z};
        completion = null;
        for (String value : Arrays.copyOfRange(xyz, argsLength - 1, xyz.length)) {
            completion = completion == null ? value : completion + " " + value;
            if (completion.startsWith(currentArg)) completions.add(completion);
        }

        return completions;
    }
}
