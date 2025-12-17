package org.amoba.mentes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.amoba.db.DatabaseManager;
import org.amoba.jatek.AmobaJatek;
import org.amoba.modell.Jatekos;
import org.amoba.modell.Tabla;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A játékállás mentéséért és betöltéséért felelős osztály.
 */
public class AdatPerzisztencia {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdatPerzisztencia.class);

    /**
     * Menti a játékot az adatbázisba.
     *
     * @param jatek a mentendő játék.
     */

    //https://www.geeksforgeeks.org/sql/merge-statement-sql-explained/
    public static void saveGame(AmobaJatek jatek) {
        String sql = "MERGE INTO saved_games (id, player1_name, player2_name, board_size, " +
                "win_length, current_player_index, board_state) " +
                "KEY(id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, 1); // Overwrite the same game slot (ID=1)
            pstmt.setString(2, jatek.getJatekosok()[0].getName());
            pstmt.setString(3, jatek.getJatekosok()[1].getName());
            pstmt.setInt(4, jatek.getTabla().getSize());
            pstmt.setInt(5, jatek.getWinLength());
            pstmt.setInt(6, jatek.getAktualisJatekosIndex());
            pstmt.setString(7, boardToString(jatek.getTabla()));
            pstmt.executeUpdate();
            LOGGER.info("Játék sikeresen mentve az adatbázisba!");
        } catch (SQLException e) {
            LOGGER.error("Hiba a játék mentése során: {}", e.getMessage());
        }
    }

    /**
     * Betölti a játékot az adatbázisból.
     *
     * @return a betöltött játék, vagy null, ha nem sikerült.
     */
    public static AmobaJatek loadGame() {
        String sql = "SELECT * FROM saved_games WHERE id = 1";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                String player1Name = rs.getString("player1_name");
                String player2Name = rs.getString("player2_name");
                int boardSize = rs.getInt("board_size");
                int winLength = rs.getInt("win_length");
                int currentPlayerIndex = rs.getInt("current_player_index");
                String boardState = rs.getString("board_state");

                Jatekos[] jatekosok = {new Jatekos(player1Name, 'X'), new Jatekos(player2Name, 'O')};
                Tabla tabla = new Tabla(boardSize, winLength);
                stringToBoard(boardState, tabla);

                boolean isAiOpponent = player2Name.equals("Gép");
                LOGGER.info("Játék sikeresen betöltve az adatbázisból.");
                return new AmobaJatek(tabla, jatekosok, currentPlayerIndex, isAiOpponent);
            }
        } catch (SQLException e) {
            LOGGER.error("Hiba a játék betöltése során: {}", e.getMessage());
        }
        return null;
    }

    private static String boardToString(Tabla tabla) {
        StringBuilder sb = new StringBuilder();
        for (char[] row : tabla.getGrid()) {
            sb.append(new String(row));
        }
        return sb.toString();
    }

    private static void stringToBoard(String boardState, Tabla tabla) {
        int size = tabla.getSize();
        char[][] grid = tabla.getGrid();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = boardState.charAt(i * size + j);
            }
        }
    }
}