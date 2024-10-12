package pl.ynfuien.yessentials.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.ynfuien.yessentials.YEssentials;
import pl.ynfuien.yessentials.data.Storage;

import java.util.UUID;

public class PlayerQuitListener implements Listener {
    private final YEssentials instance;

    public PlayerQuitListener(YEssentials instance) {
        this.instance = instance;
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();

        UUID uuid = p.getUniqueId();
        Storage.getUser(uuid).setLogoutLocation(p.getLocation().clone());
        Storage.updateUser(uuid);

        Bukkit.getScheduler().runTaskLaterAsynchronously(instance, () -> {
            if (p.isOnline()) return;

            Storage.removeUserFromCache(uuid);
        }, 60 * 20);
    }
}
