package pl.ynfuien.yadmincore.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import pl.ynfuien.yadmincore.data.Storage;

public class EntityDamageListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    private void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player p)) return;

        if (!Storage.getUser(p.getUniqueId()).isGodModeEnabled()) return;

        event.setCancelled(true);
    }
}
