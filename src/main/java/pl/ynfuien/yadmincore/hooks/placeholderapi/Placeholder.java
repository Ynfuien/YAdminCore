package pl.ynfuien.yadmincore.hooks.placeholderapi;

import org.bukkit.OfflinePlayer;

public interface Placeholder {
    String name();

    String getPlaceholder(String id, OfflinePlayer p);
}
