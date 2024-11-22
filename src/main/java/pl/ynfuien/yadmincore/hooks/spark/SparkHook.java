package pl.ynfuien.yadmincore.hooks.spark;

import me.lucko.spark.api.Spark;
import me.lucko.spark.api.SparkProvider;
import org.jetbrains.annotations.Nullable;

public class SparkHook {
    private static Spark spark = null;

    public SparkHook() {
        if (!isSparkAvailable()) return;

        spark = SparkProvider.get();
    }

    public static boolean isEnabled() {
        return spark != null;
    }
    @Nullable
    public static Spark getSpark() {
        return spark;
    }

    private static boolean isSparkAvailable() {
        try {
            Class.forName("me.lucko.spark.api.SparkProvider");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }
}
