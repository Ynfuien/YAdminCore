package pl.ynfuien.yadmincore.listeners;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import pl.ynfuien.yadmincore.YAdminCore;
import pl.ynfuien.yadmincore.config.ConfigName;
import pl.ynfuien.yadmincore.data.Storage;
import pl.ynfuien.ydevlib.messages.YLogger;

import java.util.ArrayList;
import java.util.List;

public class PlayerTeleportListener implements Listener {
    private final YAdminCore instance;
    private final List<PlayerTeleportEvent.TeleportCause> teleportCauses = new ArrayList<>();

    public PlayerTeleportListener(YAdminCore instance) {
        this.instance = instance;

        FileConfiguration config = instance.getConfigHandler().getConfig(ConfigName.CONFIG);
        ConfigurationSection section = config.getConfigurationSection("commands.back");
        if (section == null) return;

        List<String> list = section.getStringList("teleport-causes");

        for (String item : list) {
            try {
                PlayerTeleportEvent.TeleportCause cause = PlayerTeleportEvent.TeleportCause.valueOf(item.toUpperCase());
                teleportCauses.add(cause);
            } catch (IllegalArgumentException e) {
                YLogger.warn(String.format("Teleport cause '%s' for command 'back' is incorrect!", item));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerTeleport(PlayerTeleportEvent event) {
        if (!teleportCauses.contains(event.getCause())) return;
        if (event.getFrom().equals(event.getTo())) return;

        Storage.getUser(event.getPlayer().getUniqueId()).setLastLocation(event.getFrom().clone());
    }
}
