package pl.ynfuien.yessentials.hooks.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import pl.ynfuien.yessentials.YEssentials;
import pl.ynfuien.yessentials.hooks.placeholderapi.placeholders.UserPlaceholders;

public class PlaceholderAPIHook extends PlaceholderExpansion {
    private final YEssentials instance;

    private final Placeholder[] placeholders;

    public PlaceholderAPIHook(YEssentials instance) {
        this.instance = instance;


        placeholders = new Placeholder[] {
            new UserPlaceholders()
        };
    }

    @Override @NotNull
    public String getAuthor() {
        return "Ynfuien";
    }

    @Override @NotNull
    public String getIdentifier() {
        return "yessentials";
    }

    @Override @NotNull
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    // Profile
    // %yessentials_user_godmode%
    // %yessentials_user_lastlocation%
    // %yessentials_user_logoutlocation%

    @Override
    public String onRequest(OfflinePlayer p, @NotNull String params) {
        Placeholder placeholder = null;

        // Loop through placeholders and get that provided by name
        for (Placeholder ph : placeholders) {
            if (params.startsWith(ph.name() + "_")) {
                placeholder = ph;
                break;
            }
        }

        // If provided placeholder is incorrect
        if (placeholder == null) return "incorrect placeholder";

        // Get placeholder properties from params
        String id = params.substring(placeholder.name().length() + 1);
        // Get placeholder result
        String result = placeholder.getPlaceholder(id, p);

        // If result is null
        if (result == null) return "incorrect property";

        // Return result
        return result;
    }
}