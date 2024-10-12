package pl.ynfuien.yessentials.hooks.spark;

import me.lucko.spark.api.Spark;
import me.lucko.spark.api.SparkProvider;
import org.jetbrains.annotations.Nullable;

public class SparkHook {
    private static Spark spark = null;

    public SparkHook() {
        spark = SparkProvider.get();
    }

    public static boolean isEnabled() {
        return spark != null;
    }
    @Nullable
    public static Spark getSpark() {
        return spark;
    }
}
