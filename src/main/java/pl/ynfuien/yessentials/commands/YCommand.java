package pl.ynfuien.yessentials.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.ynfuien.yessentials.YEssentials;
import pl.ynfuien.yessentials.utils.Lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class YCommand implements CommandExecutor, TabCompleter {
    protected final YEssentials instance;
    protected static ConfigurationSection config;

    public YCommand(YEssentials instance) {
        this.instance = instance;
    }

    public static void updateConfig() {
        config = YEssentials.getInstance().getConfig().getConfigurationSection("commands");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        HashMap<String, Object> placeholders = new HashMap<>() {{put("command", label);}};

        // Return if plugin is reloading
        if (YEssentials.getInstance().isReloading()) {
            Lang.Message.PLUGIN_IS_RELOADING.send(sender, placeholders);
            return true;
        }

        run(sender, args, placeholders);
        return true;
    }

    abstract protected void run(@NotNull CommandSender sender, @NotNull String[] args, @NotNull HashMap<String, Object> placeholders);

    // Returns entity list selected by a target selector (@a, @e etc.), or by just entity uuid or player name.
    @Nullable
    protected static List<Entity> getSelectedEntities(CommandSender sender, String arg, String selectorsPermission) {
        List<Entity> entities;
        try {
            entities = Bukkit.selectEntities(sender, arg);
        } catch (IllegalArgumentException e) {
            entities = new ArrayList<>();
        }

        // Sender without this permission, can select only players by their username or uuid
        if (!sender.hasPermission(selectorsPermission)) {
            if (arg.startsWith("@")) return null;
            if (entities.isEmpty()) return null;
            if (!(entities.get(0) instanceof Player)) return null;
        }

        return entities;
    }

    //// Placeholder methods, for messages
    protected static void addPlayerPlaceholders(HashMap<String, Object> phs, OfflinePlayer p) {
        addPlayerPlaceholders(phs, p, null);
    }
    protected static void addPlayerPlaceholders(HashMap<String, Object> phs, OfflinePlayer p, String placeholderPrefix) {
        String pp = placeholderPrefix == null ? "" : placeholderPrefix + "-";

        phs.put(pp+"player-uuid", p.getUniqueId());
        phs.put(pp+"player-username", p.getName());
        phs.put(pp+"player-name", p.getName());
        if (p.isOnline()) phs.put(pp+"player-display-name", MiniMessage.miniMessage().serialize(p.getPlayer().displayName()));
    }

    protected static void addEntityPlaceholders(HashMap<String, Object> phs, Entity e, String placeholderPrefix) {
        String pp = placeholderPrefix == null ? "" : placeholderPrefix + "-";

        phs.put(pp+"uuid", e.getUniqueId());
        phs.put(pp+"type", e.getType().getKey().asString());
        phs.put(pp+"name", e.getName());
        if (e instanceof Player p) {
            phs.put(pp+"display-name", MiniMessage.miniMessage().serialize(p.displayName()));
            return;
        }

        Component customName = e.customName();
        phs.put(pp+"display-name", customName != null ? MiniMessage.miniMessage().serialize(customName) : e.getName());
    }


    //// Tab complete methods
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }

    // Completions for player list, that provided sender can see
    protected List<String> getPlayerListCompletions(CommandSender sender, String arg) {
        List<String> players = new ArrayList<>();
        arg = arg.toLowerCase();

        Player player = null;
        if (sender instanceof Player) player = (Player) sender;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (player != null && !player.canSee(p)) continue;

            String name = p.getName();
            if (name.toLowerCase().startsWith(arg)) {
                players.add(name);
            }
        }

        return players;
    }

    // Completions for @p, @a, @s etc.
    protected static List<String> getSelectorCompletions(String arg) {
        List<String> completions = new ArrayList<>();

        // Complicated @e[something=completions]
        if (arg.startsWith("@") && arg.length() >= 2) {
            try {
                // Get completions for vanilla tp command, for provided selector argument
                List<String> vanillaCompletions = Bukkit.getCommandMap().tabComplete(Bukkit.getConsoleSender(), "minecraft:tp " + arg);
                if (vanillaCompletions != null) {
                    // Because completions are only for the current 'sub argument',
                    // I have to concatenate used selector argument with received completions.
                    // Example time:
                    // For argument "@e[limit=1,t", received completions are "tag=", "team=" and "type=".
                    // So we concatenate them with provided argument:
                    // "@e[limit=1,ttag=", "@e[limit=1,tteam=", "@e[limit=1,ttype="
                    // But also remove the last 'sub argument', from this selector,
                    // before concatenating them: "@e[limit=1,tag=", "@e[limit=1,team=", "@e[limit=1,type="

                    String[] subArgs = arg.split("[,=\\[]", -1);
                    String lastSubArg = subArgs[subArgs.length - 1];
                    String selector = arg.substring(0, arg.length() - lastSubArg.length());

                    for (int i = 0; i < vanillaCompletions.size(); i++) {
                        String completion = vanillaCompletions.get(i);

                        String fitArg = selector;
                        if (subArgs.length == 1) fitArg = arg.substring(0, 2);
                        if (completion.equals(",") || completion.equals("]")) fitArg = arg;

                        vanillaCompletions.set(i, fitArg + completion);
                    }
                    return vanillaCompletions;
                }
            } catch (CommandException | IllegalArgumentException e) {}
        }

        for (String selector : new String[] {"@p", "@a", "@r", "@s", "@e"}) {
            if (selector.startsWith(arg)) completions.add(selector);
        }

        return completions;
    }

    // Completion for an entity's uuid, that the player is looking at
    protected static String getTargetEntityUUID(Player player, String selectorsPermission) {
        if (player == null) return null;

        RayTraceResult result = player.rayTraceEntities(5, false);
        if (result == null) return null;

        Entity target = result.getHitEntity();
        if (target == null) return null;

        if (!player.hasPermission(selectorsPermission) && !(target instanceof Player)) return null;

        return target.getUniqueId().toString();
    }


    public static ConfigurationSection getCommandsConfig() {
        return config;
    }
}
