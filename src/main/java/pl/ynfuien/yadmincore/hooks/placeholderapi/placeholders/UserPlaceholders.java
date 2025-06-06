package pl.ynfuien.yadmincore.hooks.placeholderapi.placeholders;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import pl.ynfuien.yadmincore.data.Storage;
import pl.ynfuien.yadmincore.data.User;
import pl.ynfuien.yadmincore.hooks.placeholderapi.Placeholder;
import pl.ynfuien.ydevlib.utils.DoubleFormatter;

public class UserPlaceholders implements Placeholder {
    private static final DoubleFormatter df = new DoubleFormatter();

    @Override
    public String name() {
        return "user";
    }

    @Override
    public String getPlaceholder(String id, OfflinePlayer p) {
        User user = Storage.getUser(p.getUniqueId());

        // Placeholder: %yadmincore_user_god-mode%
        // Returns: whether user has god mode enabled
        if (id.equals("god-mode")) {
            return user.isGodModeEnabled() ? "yes" : "no";
        }

        // Placeholder: %yadmincore_user_last-location%
        // Returns: last location before a teleport
        if (id.equals("last-location")) {
            Location loc = user.getLastLocation();
            if (loc == null) return "none";

            return formatLocation(loc);
        }

        // Placeholder: %yadmincore_user_logout-location%
        // Returns: last logout location
        if (id.equals("logout-location")) {
            Location loc = user.getLogoutLocation();
            if (loc == null) return "none";

            return formatLocation(loc);
        }

        return null;
    }

    private static String formatLocation(Location loc) {
        return String.join(", ", new String[] {
                loc.getWorld().getName(),
                df.format(loc.getX()),
                df.format(loc.getY()),
                df.format(loc.getZ())
        });
    }
}
