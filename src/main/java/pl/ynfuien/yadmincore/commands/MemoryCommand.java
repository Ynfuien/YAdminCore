package pl.ynfuien.yadmincore.commands;

import me.lucko.spark.api.statistic.StatisticWindow;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.ynfuien.yadmincore.YAdminCore;
import pl.ynfuien.yadmincore.hooks.spark.SparkHook;
import pl.ynfuien.yadmincore.utils.Lang;
import pl.ynfuien.ydevlib.utils.DoubleFormatter;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemoryCommand extends YCommand {
    private final DoubleFormatter df = new DoubleFormatter();

    public MemoryCommand(YAdminCore instance) {
        super(instance);
    }

    @Override
    protected void run(@NotNull CommandSender sender, @NotNull String[] args, @NotNull HashMap<String, Object> placeholders) {
        // Uptime
        Duration uptime = Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime());
        long days = uptime.toDaysPart();
        int hours = uptime.toHoursPart();
        int minutes = uptime.toMinutesPart();
        int seconds = uptime.toSecondsPart();

        placeholders.put("days", days);
        placeholders.put("hours", hours);
        placeholders.put("minutes", minutes);
        placeholders.put("seconds", seconds);

        Lang.Message uptimeMessage = Lang.Message.COMMAND_MEMORY_UPTIME_SECONDS;
        if (days > 0) uptimeMessage = Lang.Message.COMMAND_MEMORY_UPTIME_DAYS;
        else if (hours > 0) uptimeMessage = Lang.Message.COMMAND_MEMORY_UPTIME_HOURS;
        else if (minutes > 0) uptimeMessage = Lang.Message.COMMAND_MEMORY_UPTIME_MINUTES;
        placeholders.put("uptime", uptimeMessage.get(placeholders));

        // TPS
        double tps = Bukkit.getTPS()[0];
        if (SparkHook.isEnabled()) {
            tps = SparkHook.getSpark().tps().poll(StatisticWindow.TicksPerSecond.SECONDS_10);
        }

        placeholders.put("tps", df.format(tps));

        // Memory
        long freeMemory = Runtime.getRuntime().freeMemory() / 1024 / 1024;
        long maxMemory = Runtime.getRuntime().maxMemory() / 1024 / 1024;
        long usedMemory = maxMemory - freeMemory;
        long totalMemory = Runtime.getRuntime().totalMemory() / 1024 / 1024;

        placeholders.put("free-memory", freeMemory);
        placeholders.put("used-memory", usedMemory);
        placeholders.put("max-memory", maxMemory);
        placeholders.put("allocated-memory", totalMemory);

        // CPU
        Lang.Message message = Lang.Message.COMMAND_MEMORY_CPU_SPARK_DISABLED;
        if (SparkHook.isEnabled()) {
            message = Lang.Message.COMMAND_MEMORY_CPU_SPARK_ENABLED;
            double usage = SparkHook.getSpark().cpuProcess().poll(StatisticWindow.CpuUsage.MINUTES_1) * 100;
            placeholders.put("cpu-usage", df.format(usage));
        }
        placeholders.put("cpu", message.get(sender, placeholders));


        Lang.Message.COMMAND_MEMORY.send(sender, placeholders);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
