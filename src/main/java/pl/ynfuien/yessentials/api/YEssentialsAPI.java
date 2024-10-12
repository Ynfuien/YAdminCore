package pl.ynfuien.yessentials.api;

import com.google.common.base.Preconditions;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.ynfuien.yessentials.data.Storage;
import pl.ynfuien.yessentials.data.User;

import java.util.UUID;

public class YEssentialsAPI {

    //// Users
    @NotNull
    public static User getUser(@NotNull Player player) {
        Preconditions.checkArgument(player != null, "Player cannot be null");

        return Storage.getUser(player.getUniqueId());
    }

    @NotNull
    public static User getUser(@NotNull OfflinePlayer player) {
        Preconditions.checkArgument(player != null, "Player cannot be null");

        return Storage.getUser(player.getUniqueId());
    }

    @NotNull
    public static User getUser(@NotNull UUID uuid) {
        Preconditions.checkArgument(uuid != null, "Uuid cannot be null");

        return Storage.getUser(uuid);
    }
}
