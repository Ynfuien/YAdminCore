package pl.ynfuien.yessentials.hooks.placeholderapi;

import org.bukkit.OfflinePlayer;

public interface Placeholder {
    String name();

    String getPlaceholder(String id, OfflinePlayer p);
}
