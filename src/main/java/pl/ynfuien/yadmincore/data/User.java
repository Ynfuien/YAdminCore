package pl.ynfuien.yadmincore.data;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.ynfuien.ydevlib.messages.YLogger;

import java.lang.reflect.Type;
import java.util.Map;

public class User {
    // Data saved in the database
    private boolean godMode = false;
    private Location lastLocation = null;
    private Location logoutLocation = null;

    public User() {};

    /**
     * Constructor to use by the internal code of the plugin.
     */
    public User(boolean godMode, String lastLocation, String logoutLocation) {
        this.godMode = godMode;

        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        if (lastLocation != null) {
            try {
                Map<String, Object> lastLocationSerialized = new Gson().fromJson(lastLocation, type);
                this.lastLocation = Location.deserialize(lastLocationSerialized);
            } catch (JsonSyntaxException|IllegalArgumentException e) {
                if (!e.getMessage().equalsIgnoreCase("unknown world")) {
                    YLogger.error("An error occurred while parsing last_location of a player.");
                    YLogger.error("Location: " + lastLocation);
                    YLogger.error("Error:");
                    e.printStackTrace();
                }
            }
        }

        if (logoutLocation != null) {
            try {
                Map<String, Object> logoutLocationSerialized = new Gson().fromJson(logoutLocation, type);
                this.logoutLocation = Location.deserialize(logoutLocationSerialized);
            } catch (JsonSyntaxException|IllegalArgumentException e) {
                if (e.getMessage().equalsIgnoreCase("unknown world")) return;

                YLogger.error("An error occurred while parsing logout_location of a player.");
                YLogger.error("Location: " + logoutLocation);
                YLogger.error("Error:");
                e.printStackTrace();
            }
        }
    }

    /**
     * @return whether god mode is enabled
     */
    public boolean isGodModeEnabled() {
        return godMode;
    }

    /**
     * Gets the last location before teleportation.
     */
    @Nullable
    public Location getLastLocation() {
        return lastLocation;
    }

    /**
     * Gets the last logout location.
     */
    @Nullable
    public Location getLogoutLocation() {
        return logoutLocation;
    }

    /**
     * Sets god mode
     */
    public void setGodMode(boolean enabled) {
        this.godMode = enabled;
    }

    /**
     * Sets the last location
     */
    public void setLastLocation(@NotNull Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    /**
     * Sets last logout location
     */
    public void setLogoutLocation(@NotNull Location logoutLocation) {
        this.logoutLocation = logoutLocation;
    }
}
