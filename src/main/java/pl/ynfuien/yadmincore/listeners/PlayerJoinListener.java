package pl.ynfuien.yadmincore.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.ynfuien.yadmincore.YAdminCore;
import pl.ynfuien.yadmincore.data.Storage;

import java.util.UUID;

public class PlayerJoinListener implements Listener {
    private final YAdminCore instance;

    public PlayerJoinListener(YAdminCore instance) {
        this.instance = instance;
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            if (Storage.getDatabase().userExists(uuid)) {
                Storage.getUser(uuid);
                return;
            }

            Storage.updateUser(uuid);
        });
    }
}
