package pl.ynfuien.yadmincore.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.ynfuien.yadmincore.YAdminCore;
import pl.ynfuien.yadmincore.data.Storage;

import java.util.UUID;

public class PlayerQuitListener implements Listener {
    private final YAdminCore instance;

    public PlayerQuitListener(YAdminCore instance) {
        this.instance = instance;
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();

        UUID uuid = p.getUniqueId();
        Storage.getUser(uuid).setLogoutLocation(p.getLocation().clone());
        Storage.updateUser(uuid);

        Storage.removeUserFromCache(uuid);
    }
}
