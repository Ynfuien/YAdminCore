package pl.ynfuien.yessentials;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import pl.ynfuien.ydevlib.messages.YLogger;
import pl.ynfuien.yessentials.commands.*;
import pl.ynfuien.yessentials.commands.inventories.*;
import pl.ynfuien.yessentials.commands.main.MainCommand;
import pl.ynfuien.yessentials.config.ConfigHandler;
import pl.ynfuien.yessentials.config.ConfigName;
import pl.ynfuien.yessentials.config.ConfigObject;
import pl.ynfuien.yessentials.data.Database;
import pl.ynfuien.yessentials.data.MysqlDatabase;
import pl.ynfuien.yessentials.data.SqliteDatabase;
import pl.ynfuien.yessentials.data.Storage;
import pl.ynfuien.yessentials.hooks.Hooks;
import pl.ynfuien.yessentials.listeners.EntityDamageListener;
import pl.ynfuien.yessentials.listeners.PlayerJoinListener;
import pl.ynfuien.yessentials.listeners.PlayerQuitListener;
import pl.ynfuien.yessentials.listeners.PlayerTeleportListener;
import pl.ynfuien.yessentials.utils.Lang;

import java.util.HashMap;
import java.util.List;

public final class YEssentials extends JavaPlugin {
    private static YEssentials instance;
    private final ConfigHandler configHandler = new ConfigHandler(this);
    private ConfigObject config;
    private Database database = null;

    private boolean reloading = false;

    @Override
    public void onEnable() {
        instance = this;
        YLogger.setup("<dark_aqua>[<aqua>Y<red>Essentials<dark_aqua>] <white>", getComponentLogger());

        loadConfigs();
        loadLang();
        config = configHandler.get(ConfigName.CONFIG);

        ConfigurationSection dbConfig = config.getConfig().getConfigurationSection("database");
        database = getDatabase(dbConfig);
        if (database != null && database.setup(dbConfig)) database.createUsersTable();
        Storage.setup(this);

        setupCommands();
        registerListeners();

        Hooks.load(this);

        YCommand.updateConfig();

        YLogger.info("Plugin successfully <green>enabled<white>!");
    }

    @Override
    public void onDisable() {
        database.close();

        YLogger.info("Plugin successfully <red>disabled<white>!");
    }

    private void setupCommands() {
        HashMap<String, CommandExecutor> commands = new HashMap<>() {{
            put("yessentials", new MainCommand());
            put("heal", new HealCommand(instance));
            put("feed", new FeedCommand(instance));
            put("item", new ItemCommand(instance));
            put("memory", new MemoryCommand(instance));
            put("tp", new TpCommand(instance));
            put("tphere", new TphereCommand(instance));
            put("god", new GodCommand(instance));
            put("gm", new GamemodeCommand(instance));
            put("back", new BackCommand(instance));
            put("tpoffline", new TpofflineCommand(instance));
            put("anvil", new AnvilCommand(instance));
            put("cartographytable", new CartographyTableCommand(instance));
            put("enderchest", new EnderchestCommand(instance));
            put("grindstone", new GrindstoneCommand(instance));
            put("loom", new LoomCommand(instance));
            put("smithingtable", new SmithingTableCommand(instance));
            put("stonecutter", new StonecutterCommand(instance));
            put("workbench", new WorkbenchCommand(instance));
            put("entity", new EntityCommand(instance));
            put("fly", new FlyCommand(instance));
        }};

        for (String name : commands.keySet()) {
            CommandExecutor cmd = commands.get(name);

            getCommand(name).setExecutor(cmd);
            getCommand(name).setTabCompleter((TabCompleter) cmd);
        }
    }

    private void registerListeners() {
        Listener[] listeners = new Listener[] {
                new EntityDamageListener(),
                new PlayerTeleportListener(this),
                new PlayerJoinListener(this),
                new PlayerQuitListener(this),
        };

        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    private Database getDatabase(ConfigurationSection config) {
        String type = config.getString("type");
        if (type.equalsIgnoreCase("sqlite")) return new SqliteDatabase();
        else if (type.equalsIgnoreCase("mysql")) return new MysqlDatabase();

        YLogger.error("Database type is incorrect! Available database types: sqlite, mysql");
        return null;
    }


    private void loadLang() {
        // Get lang config
        FileConfiguration config = configHandler.getConfig(ConfigName.LANG);

        // Reload lang
        Lang.loadLang(config);
    }

    private void loadConfigs() {
        configHandler.load(ConfigName.CONFIG, true, false, List.of("commands.entity.fields"));
        configHandler.load(ConfigName.LANG, true, true);
    }

    public boolean reloadPlugin() {
        reloading = true;

        // Reload all configs
        configHandler.reloadAll();

        YCommand.updateConfig();

        // Reload lang
        instance.loadLang();

        reloading = false;
        return true;
    }

    public boolean isReloading() {
        return reloading;
    }

    public static YEssentials getInstance() {
        return instance;
    }
    public ConfigHandler getConfigHandler() {
        return configHandler;
    }
    public FileConfiguration getConfig() {
        return config.getConfig();
    }

    public Database getDatabase() {
        return database;
    }
}
