package pl.ynfuien.yessentials.hooks.placeholderapi.placeholders;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import pl.ynfuien.yessentials.data.Storage;
import pl.ynfuien.yessentials.data.User;
import pl.ynfuien.yessentials.hooks.placeholderapi.Placeholder;
import pl.ynfuien.yessentials.utils.DoubleFormatter;

public class UserPlaceholders implements Placeholder {
    private static final DoubleFormatter df = new DoubleFormatter();

    @Override
    public String name() {
        return "user";
    }

    @Override
    public String getPlaceholder(String id, OfflinePlayer p) {
        User user = Storage.getUser(p.getUniqueId());

        // Placeholder: %yessentials_user_godmode%
        // Returns: whether user has godmode enabled
        if (id.equals("godmode")) {
            return user.isGodModeEnabled() ? "yes" : "no";
        }

        // Placeholder: %yessentials_user_lastlocation%
        // Returns: last location before a teleport
        if (id.equals("lastlocation")) {
            Location loc = user.getLastLocation();
            if (loc == null) return "none";

            return formatLocation(loc);
        }

        // Placeholder: %yessentials_user_logoutlocation%
        // Returns: last logout location
        if (id.equals("logoutlocation")) {
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
