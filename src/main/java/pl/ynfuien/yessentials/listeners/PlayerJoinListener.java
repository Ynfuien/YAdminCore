package pl.ynfuien.yessentials.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.ynfuien.yessentials.YEssentials;
import pl.ynfuien.yessentials.data.Storage;

import java.util.UUID;

public class PlayerJoinListener implements Listener {
    private final YEssentials instance;

    public PlayerJoinListener(YEssentials instance) {
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
