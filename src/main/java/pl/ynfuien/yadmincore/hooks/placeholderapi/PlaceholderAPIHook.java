package pl.ynfuien.yadmincore.hooks.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import pl.ynfuien.yadmincore.YAdminCore;
import pl.ynfuien.yadmincore.hooks.placeholderapi.placeholders.UserPlaceholders;

public class PlaceholderAPIHook extends PlaceholderExpansion {
    private final YAdminCore instance;

    private final Placeholder[] placeholders;

    public PlaceholderAPIHook(YAdminCore instance) {
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
        return "yadmincore";
    }

    @Override @NotNull
    public String getVersion() {
        return instance.getPluginMeta().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    // User
    // %yadmincore_user_godmode%
    // %yadmincore_user_lastlocation%
    // %yadmincore_user_logoutlocation%

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