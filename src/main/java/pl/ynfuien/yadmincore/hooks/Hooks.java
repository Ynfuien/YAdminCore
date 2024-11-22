package pl.ynfuien.yadmincore.hooks;

import org.bukkit.Bukkit;
import pl.ynfuien.ydevlib.messages.YLogger;
import pl.ynfuien.yadmincore.YAdminCore;
import pl.ynfuien.yadmincore.hooks.placeholderapi.PlaceholderAPIHook;
import pl.ynfuien.yadmincore.hooks.spark.SparkHook;

public class Hooks {
    private static PlaceholderAPIHook papiHook = null;

    public static void load(YAdminCore instance) {
        // Register PlaceholderAPI hook
        if (isPapiEnabled()) {
            papiHook = new PlaceholderAPIHook(instance);
            if (!papiHook.register()) {
                papiHook = null;
                YLogger.error("[Hooks] Something went wrong while registering PlaceholderAPI hook!");
            }
            else {
                YLogger.info("[Hooks] Successfully registered hook for PlaceholderAPI!");
            }
        }

        Bukkit.getScheduler().runTask(instance, () -> {
            new SparkHook();
            if (SparkHook.isEnabled()) {
                YLogger.info("[Hooks] Successfully registered hook for Spark!");            }
        });
    }

    public static boolean isPapiEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }
    public static boolean isPapiHookEnabled() {
        return papiHook != null;
    }
}
