package org.amoba.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adatbázis-kezelő osztály.
 */
public class DatabaseManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseManager.class);
    private static final String DB_URL = "jdbc:h2:./amoba_db";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    /**
     * Visszaad egy adatbázis-kapcsolatot.
     *
     * @return az adatbázis-kapcsolat.
     * @throws SQLException ha hiba történik a kapcsolat létrehozása során.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Inicializálja az adatbázist, létrehozza a táblákat, ha azok nem léteznek.
     */
    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            String createPlayersTable = "CREATE TABLE IF NOT EXISTS players (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(255) UNIQUE," +
                    "wins INT DEFAULT 0)";
            stmt.execute(createPlayersTable);

            String createSavedGamesTable = "CREATE TABLE IF NOT EXISTS saved_games (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "player1_name VARCHAR(255)," +
                    "player2_name VARCHAR(255)," +
                    "board_size INT," +
                    "win_length INT," +
                    "current_player_index INT," +
                    "board_state VARCHAR(1000))";
            stmt.execute(createSavedGamesTable);
            LOGGER.info("Adatbázis sikeresen inicializálva.");
        } catch (SQLException e) {
            throw new RuntimeException("Nem sikerült inicializálni az adatbázist", e);
        }
    }
}