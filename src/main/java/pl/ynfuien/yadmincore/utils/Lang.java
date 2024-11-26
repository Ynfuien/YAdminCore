package pl.ynfuien.yadmincore.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import pl.ynfuien.ydevlib.messages.LangBase;
import pl.ynfuien.ydevlib.messages.Messenger;
import pl.ynfuien.ydevlib.messages.colors.ColorFormatter;

import java.util.HashMap;

public class Lang extends LangBase {
    public enum Message implements LangBase.Message {
        PREFIX,
        COMMANDS_NO_PERMISSION,

        // Main command
        COMMAND_MAIN_USAGE,
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
        COMMAND_MEMORY_CPU_SPARK_ENABLED,
        COMMAND_MEMORY_CPU_SPARK_DISABLED,
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

        /**
         * Gets name/path of this message.
         */
        @Override
        public String getName() {
            return name().toLowerCase().replace('_', '-');
        }

        /**
         * Gets original unformatted message.
         */
        public String get() {
            return Lang.get(getName());
        }

        /**
         * Gets message with parsed:
         * - {prefix} placeholder
         * - additional provided placeholders
         */
        public String get(HashMap<String, Object> placeholders) {
            return Lang.get(getName(), placeholders);
        }

        /**
         * Gets message with parsed:
         * - PlaceholderAPI
         * - {prefix} placeholder
         * - additional provided placeholders
         */
        public String get(CommandSender sender, HashMap<String, Object> placeholders) {
            return ColorFormatter.parsePAPI(sender, Lang.get(getName(), placeholders));
        }

        /**
         * Gets message as component with parsed:
         * - MiniMessage
         * - PlaceholderAPI
         * - {prefix} placeholder
         * - additional provided placeholders
         */
        public Component getComponent(CommandSender sender, HashMap<String, Object> placeholders) {
            return Messenger.parseMessage(sender, Lang.get(getName()), placeholders);
        }

        /**
         * Sends this message to provided sender.<br/>
         * Parses:<br/>
         * - MiniMessage<br/>
         * - PlaceholderAPI<br/>
         * - {prefix} placeholder
         */
        public void send(CommandSender sender) {
            this.send(sender, new HashMap<>());
        }

        /**
         * Sends this message to provided sender.<br/>
         * Parses:<br/>
         * - MiniMessage<br/>
         * - PlaceholderAPI<br/>
         * - {prefix} placeholder<br/>
         * - additional provided placeholders
         */
        public void send(CommandSender sender, HashMap<String, Object> placeholders) {
            Lang.sendMessage(sender, this, placeholders);
        }
    }
}