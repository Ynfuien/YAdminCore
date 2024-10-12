package pl.ynfuien.yessentials.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class Lang {
    private static String prefix;
    private static FileConfiguration langConfig;

    public static void loadLang(FileConfiguration langConfig) {
        Lang.langConfig = langConfig;
        prefix = Message.PREFIX.get();
    }

    // Gets message by message enum
    @Nullable
    public static String get(Message message) {
        return get(message.getName());
    }
    // Gets message by path
    @Nullable
    public static String get(String path) {
        return langConfig.getString(path);
    }
    // Gets message by path and replaces placeholders
    @Nullable
    public static String get(String path, HashMap<String, Object> placeholders) {
        placeholders.put("prefix", prefix);
        // Return message with used placeholders
        return Messenger.parsePluginPlaceholders(langConfig.getString(path), placeholders);
    }

    public static void sendMessage(CommandSender sender, Message message) {
        sendMessage(sender, message.getName());
    }
    public static void sendMessage(CommandSender sender, String path) {
        sendMessage(sender, path, new HashMap<>());
    }
    public static void sendMessage(CommandSender sender, String path, HashMap<String, Object> placeholders) {
        List<String> messages;

        if (langConfig.isList(path)) {
            messages = langConfig.getStringList(path);
        } else {
            messages = List.of(langConfig.getString(path));
            if (messages.get(0) == null) {
                YLogger.error(String.format("There is no message '%s'!", path));
                return;
            }
        }

        for (String message : messages) {
            // Return if message is empty
            if (message.isEmpty()) continue;

            // Get message with used placeholders
            placeholders.put("prefix", prefix);
//            message = Messenger.replacePlaceholders(message, placeholders);

            Messenger.send(sender, message, placeholders);
        }
    }

    // Messages enum
    public enum Message {
        PREFIX,
        PLUGIN_IS_RELOADING,
        COMMANDS_INCORRECT,
        COMMANDS_NO_PERMISSION,

        // Main command
        COMMAND_MAIN_HELP,
        COMMAND_MAIN_RELOAD_FAIL,
        COMMAND_MAIN_RELOAD_SUCCESS,
        COMMAND_MAIN_VERSION,

        //<editor-fold desc="Heal" defaultstate="collapsed">
        COMMAND_HEAL_FAIL_NEED_PLAYER,
        COMMAND_HEAL_FAIL_PLAYER_DOESNT_EXIST,
        COMMAND_HEAL_SUCCESS_YOURSELF,
        COMMAND_HEAL_SUCCESS_ANOTHER_PLAYER,
        COMMAND_HEAL_SUCCESS_RECEIVE,
        //</editor-fold>

        //<editor-fold desc="Feed" defaultstate="collapsed">
        COMMAND_FEED_FAIL_NEED_PLAYER,
        COMMAND_FEED_FAIL_PLAYER_DOESNT_EXIST,
        COMMAND_FEED_SUCCESS_YOURSELF,
        COMMAND_FEED_SUCCESS_ANOTHER_PLAYER,
        COMMAND_FEED_SUCCESS_RECEIVE,
        //</editor-fold>

        //<editor-fold desc="Item" defaultstate="collapsed">
        COMMAND_ITEM_USAGE,
        COMMAND_ITEM_FAIL_ITEM_DOESNT_EXIST,
        COMMAND_ITEM_FAIL_ITEM_UNOBTAINABLE,
        COMMAND_ITEM_FAIL_NOT_PLAYER,
        COMMAND_ITEM_FAIL_INCORRECT_AMOUNT,
        COMMAND_ITEM_SUCCESS,
        //</editor-fold>

        //<editor-fold desc="Memory" defaultstate="collapsed">
        COMMAND_MEMORY,
        COMMAND_MEMORY_UPTIME_SECONDS,
        COMMAND_MEMORY_UPTIME_MINUTES,
        COMMAND_MEMORY_UPTIME_HOURS,
        COMMAND_MEMORY_UPTIME_DAYS,
        //</editor-fold>

        //<editor-fold desc="Tp" defaultstate="collapsed">
        COMMAND_TP_USAGE,
        COMMAND_TP_USAGE_PLAYER_TO_PLAYER,
        COMMAND_TP_FAIL,
        COMMAND_TP_FAIL_ONLY_PLAYER,
        COMMAND_TP_FAIL_SELECTOR_NO_TARGETS,
        COMMAND_TP_FAIL_SELECTOR_NO_DESTINATION,
        COMMAND_TP_FAIL_SELECTOR_MANY_DESTINATIONS,
        COMMAND_TP_FAIL_SELECTOR_TARGET_DOESNT_EXIST,
        COMMAND_TP_FAIL_SELECTOR_DESTINATION_DOESNT_EXIST,
        COMMAND_TP_FAIL_PLAYER_DOESNT_EXIST,
        COMMAND_TP_FAIL_INVALID_LOCATION_ARGUMENT,
        COMMAND_TP_SUCCESS,
        COMMAND_TP_SUCCESS_SELECTOR,
        COMMAND_TP_SUCCESS_SELECTOR_MANY,
        COMMAND_TP_SUCCESS_LOCATION,
        COMMAND_TP_SUCCESS_ENTITY_TO_LOCATION,
        COMMAND_TP_SUCCESS_MANY_TO_LOCATION,
        COMMAND_TP_SUCCESS_RECEIVE_LOCATION,
        COMMAND_TP_SUCCESS_RECEIVE_SELECTOR,
        //</editor-fold>

        //<editor-fold desc="Tphere" defaultstate="collapsed">
        COMMAND_TPHERE_USAGE,
        COMMAND_TPHERE_FAIL,
        COMMAND_TPHERE_FAIL_NEED_PLAYER,
        COMMAND_TPHERE_FAIL_PLAYER_DOESNT_EXIST,
        COMMAND_TPHERE_SUCCESS,
        COMMAND_TPHERE_SUCCESS_RECEIVE,
        //</editor-fold>

        //<editor-fold desc="Tpoffline" defaultstate="collapsed">
        COMMAND_TPOFFLINE_USAGE,
        COMMAND_TPOFFLINE_FAIL,
        COMMAND_TPOFFLINE_FAIL_NO_LOGOUT_LOCATION,
        COMMAND_TPOFFLINE_FAIL_ONLY_PLAYER,
        COMMAND_TPOFFLINE_FAIL_PLAYER_DOESNT_EXIST,
        COMMAND_TPOFFLINE_SUCCESS,
        //</editor-fold>

        //<editor-fold desc="Back" defaultstate="collapsed">
        COMMAND_BACK_FAIL,
        COMMAND_BACK_FAIL_NO_LAST_LOCATION,
        COMMAND_BACK_FAIL_ONLY_PLAYER,
        COMMAND_BACK_FAIL_PLAYER_DOESNT_EXIST,
        COMMAND_BACK_SUCCESS,
        COMMAND_BACK_SUCCESS_OTHER,
        COMMAND_BACK_SUCCESS_RECEIVE,
        //</editor-fold>

        //<editor-fold desc="God" defaultstate="collapsed">
        COMMAND_GOD_FAIL_NEED_PLAYER,
        COMMAND_GOD_FAIL_PLAYER_DOESNT_EXIST,
        COMMAND_GOD_SUCCESS,
        COMMAND_GOD_SUCCESS_OTHER,
        COMMAND_GOD_SUCCESS_RECEIVE,
        COMMAND_GOD_ENABLED,
        COMMAND_GOD_DISABLED,
        //</editor-fold>

        //<editor-fold desc="Gamemode" defaultstate="collapsed">
        COMMAND_GM_USAGE,
        COMMAND_GM_USAGE_OTHER,
        COMMAND_GM_FAIL_NEED_PLAYER,
        COMMAND_GM_FAIL_PLAYER_DOESNT_EXIST,
        COMMAND_GM_FAIL_INCORRECT_GAMEMODE,
        COMMAND_GM_FAIL_NO_PERMISSION,
        COMMAND_GM_SUCCESS,
        COMMAND_GM_SUCCESS_OTHER,
        COMMAND_GM_SUCCESS_RECEIVE,
        COMMAND_GM_SURVIVAL,
        COMMAND_GM_CREATIVE,
        COMMAND_GM_ADVENTURE,
        COMMAND_GM_SPECTATOR,
        //</editor-fold>

        // Inventories
        COMMAND_INVENTORIES_FAIL_ONLY_PLAYER,

        //<editor-fold desc="Itemname" defaultstate="collapsed">
        COMMAND_ITEMNAME_USAGE,
        COMMAND_ITEMNAME_INFO,
        COMMAND_ITEMNAME_INFO_NO_NAME,
        COMMAND_ITEMNAME_FAIL_ONLY_PLAYER,
        COMMAND_ITEMNAME_FAIL_NO_ITEM,
        COMMAND_ITEMNAME_SUCCESS,
        COMMAND_ITEMNAME_SUCCESS_RESET,
        //</editor-fold>

        //<editor-fold desc="Itemlore" defaultstate="collapsed">
        COMMAND_ITEMLORE_USAGE,
        COMMAND_ITEMLORE_FAIL_ONLY_PLAYER,
        COMMAND_ITEMLORE_FAIL_NO_ITEM,
        COMMAND_ITEMLORE_FAIL_ADD_NO_LORE,
        COMMAND_ITEMLORE_FAIL_ADD_REACH_LIMIT,
        COMMAND_ITEMLORE_FAIL_SET_NO_LORE,
        COMMAND_ITEMLORE_FAIL_SET_NO_NUMBER,
        COMMAND_ITEMLORE_FAIL_SET_REACH_LIMIT,
        COMMAND_ITEMLORE_FAIL_REMOVE_NO_NUMBER,
        COMMAND_ITEMLORE_FAIL_REMOVE_LINE_DOESNT_EXIST,
        COMMAND_ITEMLORE_FAIL_INCORRECT_NUMBER,
        COMMAND_ITEMLORE_FAIL_NO_LORE,
        COMMAND_ITEMLORE_CONFIRM_CLEAR,
        COMMAND_ITEMLORE_SUCCESS_ADD,
        COMMAND_ITEMLORE_SUCCESS_ADD_EMPTY,
        COMMAND_ITEMLORE_SUCCESS_REMOVE,
        COMMAND_ITEMLORE_SUCCESS_SET,
        COMMAND_ITEMLORE_SUCCESS_SET_EMPTY,
        COMMAND_ITEMLORE_SUCCESS_CLEAR,
        COMMAND_ITEMLORE_SHOW_HEADER,
        COMMAND_ITEMLORE_SHOW_LINE,
        //</editor-fold>

        //<editor-fold desc="Editsign" defaultstate="collapsed">
        COMMAND_EDITSIGN_USAGE,
        COMMAND_EDITSIGN_FAIL_ONLY_PLAYER,
        COMMAND_EDITSIGN_FAIL_NO_SIGN,
        COMMAND_EDITSIGN_FAIL_SET_NO_NUMBER,
        COMMAND_EDITSIGN_FAIL_SET_NO_TEXT,
        COMMAND_EDITSIGN_FAIL_SET_ABOVE_LIMIT,
        COMMAND_EDITSIGN_FAIL_CLEAR_NO_NUMBER,
        COMMAND_EDITSIGN_FAIL_CLEAR_EMPTY,
        COMMAND_EDITSIGN_FAIL_INCORRECT_NUMBER,
        COMMAND_EDITSIGN_SUCCESS_SET,
        COMMAND_EDITSIGN_SUCCESS_CLEAR,
        COMMAND_EDITSIGN_SHOW_HEADER,
        COMMAND_EDITSIGN_SHOW_LINE,
        //</editor-fold>

        //<editor-fold desc="Entity" defaultstate="collapsed">
        COMMAND_ENTITY_USAGE,
        COMMAND_ENTITY_FAIL_SELECTOR_NO_TARGETS,
        COMMAND_ENTITY_FAIL_SELECTOR_MANY_TARGETS,
        COMMAND_ENTITY_FAIL_SELECTOR_TARGET_DOESNT_EXIST,
        COMMAND_ENTITY_FAIL_PLAYER_DOESNT_EXIST,
        COMMAND_ENTITY_INFO_PLAYER,
        COMMAND_ENTITY_INFO_OTHER,
        COMMAND_ENTITY_INFO_PLACEHOLDER_TRUE,
        COMMAND_ENTITY_INFO_PLACEHOLDER_FALSE,
        //</editor-fold>

        //<editor-fold desc="Fly" defaultstate="collapsed">
        COMMAND_FLY_FAIL_NEED_PLAYER,
        COMMAND_FLY_FAIL_PLAYER_DOESNT_EXIST,
        COMMAND_FLY_SUCCESS,
        COMMAND_FLY_SUCCESS_OTHER,
        COMMAND_FLY_SUCCESS_RECEIVE,
        COMMAND_FLY_ENABLED,
        COMMAND_FLY_DISABLED,
        //</editor-fold>
        ;

        // Gets message name
        public String getName() {
            return name().toLowerCase().replace('_', '-');
        }

        // Gets message
        public String get() {
            return Lang.get(getName());
        }
        // Gets message with replaced normal placeholders
        public String get(HashMap<String, Object> placeholders) {
            return Lang.get(getName(), placeholders);
        }
        // Gets component message with replaced all placeholders
        public Component get(CommandSender sender, HashMap<String, Object> placeholders) {
            return Messenger.parseMessage(sender, Lang.get(getName()), placeholders);
        }

        // Sends message
        public void send(CommandSender sender) {
            Lang.sendMessage(sender, getName());
        }
        // Sends message with replaced placeholders
        public void send(CommandSender sender, HashMap<String, Object> placeholders) {
            Lang.sendMessage(sender, getName(), placeholders);
        }
    }
}
