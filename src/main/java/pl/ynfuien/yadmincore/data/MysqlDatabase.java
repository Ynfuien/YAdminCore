package pl.ynfuien.yadmincore.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;
import pl.ynfuien.ydevlib.messages.YLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MysqlDatabase extends Database {
    @Override
    public boolean setup(ConfigurationSection config) {
        close();

        dbName = config.getString("name");

        HikariConfig dbConfig = new HikariConfig();
        dbConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dbConfig.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s", config.getString("host"), config.getString("port"), dbName));

        dbConfig.setUsername(config.getString("login"));
        dbConfig.setPassword(config.getString("password"));
        dbConfig.setMaximumPoolSize(config.getInt("max-connections"));


        try {
            dbSource = new HikariDataSource(dbConfig);
        } catch (Exception e) {
            YLogger.error("Plugin couldn't connect to a database! Please check connection data, because some plugin's functionality requires the database!");
            e.printStackTrace();
            return false;
        }

        usersTableName = config.getString("table");
        return true;
    }

    @Override
    public boolean createUsersTable() {
        String query = String.format("CREATE TABLE IF NOT EXISTS `%s` (`id` INT NOT NULL AUTO_INCREMENT, `uuid` VARCHAR(36) NOT NULL, `godmode` BOOLEAN DEFAULT FALSE, `last_location` TEXT NULL DEFAULT NULL, `logout_location` TEXT NULL DEFAULT NULL, PRIMARY KEY (`id`)) DEFAULT CHARSET=utf8mb4", usersTableName);

        try (Connection conn = dbSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.execute();
        } catch (SQLException e) {
            YLogger.error(String.format("Couldn't create table '%s' in database '%s'", usersTableName, dbName));
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
