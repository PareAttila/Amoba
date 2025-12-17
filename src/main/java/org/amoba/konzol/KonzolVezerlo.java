package org.amoba.konzol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import org.amoba.db.DatabaseManager;
import org.amoba.jatek.AmobaJatek;
import org.amoba.mentes.AdatPerzisztencia;
import org.amoba.mentes.Export;
import org.amoba.modell.Jatekos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A játék konzolos vezérléséért felelős osztály.
 */
public class KonzolVezerlo {
    private static final Logger LOGGER = LoggerFactory.getLogger(KonzolVezerlo.class);
    private final Scanner scanner;
    private AmobaJatek game;

    /**
     * Létrehoz egy új konzolos vezérlőt.
     */
    public KonzolVezerlo() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Elindítja a játékot.
     */
    public void start() {
        LOGGER.info("Üdv az amőba játékban!");
        displayHighScores();

        if (hasSavedGame() && confirmLoadGame()) {
            game = AdatPerzisztencia.loadGame();
            if (game == null) {
                LOGGER.warn("Nem sikerült betölteni a mentést, új játék indul.");
                game = startNewGame();
            }
        } else {
            game = startNewGame();
        }

        gameLoop();
        scanner.close();
    }

    private void displayHighScores() {
        LOGGER.info("--- Ranglista ---");
        String sql = "SELECT name, wins FROM players ORDER BY wins DESC LIMIT 10";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            int rank = 1;
            while (rs.next()) {
                String name = rs.getString("name");
                int wins = rs.getInt("wins");
                LOGGER.info("{}. {}: {} pont", rank++, name, wins);
            }
            LOGGER.info("-----------------");
        } catch (SQLException e) {
            LOGGER.error("Hiba a ranglista lekérdezése során: {}", e.getMessage());
        }
    }

    private boolean hasSavedGame() {
        String sql = "SELECT COUNT(*) FROM saved_games WHERE id = 1";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.error("Hiba a mentett játék ellenőrzése során: {}", e.getMessage());
        }
        return false;
    }

    private boolean confirmLoadGame() {
        while (true) {
            LOGGER.info("Szeretnéd betölteni az előző játékot? (i/n): ");
            String answer = scanner.nextLine().trim().toLowerCase();
            if ("i".equals(answer)) {
                return true;
            }
            if ("n".equals(answer)) {
                return false;
            }
            LOGGER.warn("Érvénytelen válasz. 'i' vagy 'n'.");
        }
    }

    private int chooseBoardSize() {
        while (true) {
            LOGGER.info("Válassz táblaméretet (3, 5, vagy 10): ");
            try {
                int size = Integer.parseInt(scanner.nextLine().trim());
                if (size == 3 || size == 5 || size == 10) {
                    return size;
                }
                LOGGER.warn("Érvénytelen méret. Kérlek, 3, 5, vagy 10 értéket adj meg.");
            } catch (NumberFormatException e) {
                LOGGER.error("Érvénytelen bemenet. Kérlek, számot adj meg.");
            }
        }
    }

    private AmobaJatek startNewGame() {
        LOGGER.info("Első játékos neve (X): ");
        String name1 = getPlayerName();

        LOGGER.info("Második játékos neve (O), vagy írd be, hogy 'Gép' a gépi ellenfélhez: ");
        String name2 = scanner.nextLine();

        boolean isAiOpponent = "Gép".equalsIgnoreCase(name2.trim());
        if (isAiOpponent) {
            name2 = "Gép";
        } else {
            logPlayerWins(name2);
        }

        int size = chooseBoardSize();
        return new AmobaJatek(name1, name2, size, isAiOpponent);
    }

    private String getPlayerName() {
        String name = scanner.nextLine();
        logPlayerWins(name);
        return name;
    }

    private void logPlayerWins(String name) {
        int wins = getPlayerWins(name);
        LOGGER.info("{} játékosnak eddig {} győzelme van.", name, wins);
    }

    private int getPlayerWins(String name) {
        String sql = "SELECT wins FROM players WHERE name = ?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("wins");
            }
        } catch (SQLException e) {
            LOGGER.error("Hiba a játékos győzelmeinek lekérdezése során: {}", e.getMessage());
        }
        return 0;
    }

    private void handleExport() {
        while (true) {
            LOGGER.info("Milyen formátumban szeretnéd exportálni? (xml/json): ");
            String format = scanner.nextLine().trim().toLowerCase();
            if ("xml".equals(format) || "json".equals(format)) {
                String player1 = game.getJatekosok()[0].getName();
                String player2 = game.getJatekosok()[1].getName();
                String fileName = String.format("%s_vs_%s.%s", player1, player2, format);
                Export.exportToFile(game, fileName, format);
                break;
            } else {
                LOGGER.warn("Érvénytelen formátum. Kérlek, 'xml' vagy 'json' értéket adj meg.");
            }
        }
    }

    private void gameLoop() {
        while (!game.isJatekVege()) {
            game.getTabla().printBoard();
            Jatekos currentPlayer = game.getAktualisJatekos();
            LOGGER.info("{} következik.", currentPlayer);

            if (game.isGepiJatekos(currentPlayer)) {
                LOGGER.info("A gép lép...");
                game.gepiLepes();
                continue;
            }

            LOGGER.info("Add meg a lépést (pl. '1a'), vagy írj 'mentés'-t, 'export'-ot vagy 'kilépés'-t: ");
            String input = scanner.nextLine().trim().toLowerCase();

            if ("mentés".equalsIgnoreCase(input)) {
                AdatPerzisztencia.saveGame(game);
                continue;
            }

            if ("export".equalsIgnoreCase(input)) {
                handleExport();
                continue;
            }

            if ("kilépés".equalsIgnoreCase(input)) {
                LOGGER.info("Viszlát!");
                System.exit(0);
            }

            try {
                if (input.length() < 2) {
                    LOGGER.warn("Érvénytelen formátum.");
                }
                char colChar = input.charAt(input.length() - 1);
                int col = colChar - 'a';
                String rowStr = input.substring(0, input.length() - 1);
                int row = Integer.parseInt(rowStr) - 1;

                if (!game.getTabla().isMoveLegal(row, col)) {
                    LOGGER.warn("Érvénytelen lépés! A mezőnek egy már meglévő jel mellett kell lennie.");
                }
                game.lep(row, col);
            } catch (Exception e) {
                LOGGER.error("Hiba: {}", e.getMessage());
            }
        }

        endGame();
    }

    private void endGame() {
        game.getTabla().printBoard();
        Jatekos winner = game.getGyoztes();
        if (winner != null) {
            LOGGER.info("Gratulálok, {} nyert!", winner.getName());
            updatePlayerWins(winner.getName());
        } else {
            LOGGER.info("A játék döntetlen!");
        }
    }

    private void updatePlayerWins(String name) {
        String sql = "MERGE INTO players (name, wins) KEY(name) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int wins = getPlayerWins(name) + 1;
            pstmt.setString(1, name);
            pstmt.setInt(2, wins);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Hiba a játékos győzelmeinek frissítése során: {}", e.getMessage());
        }
    }
}