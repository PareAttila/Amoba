package org.amoba.konzol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import org.amoba.db.DatabaseManager;
import org.amoba.jatek.AmobaJatek;
import org.amoba.mentes.AdatPerzisztencia;
import org.amoba.modell.Jatekos;

public class KonzolVezerlo {
    private final Scanner scanner;
    private AmobaJatek game;

    public KonzolVezerlo() {
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Üdv az amőba játékban!");

        if (hasSavedGame() && confirmLoadGame()) {
            game = AdatPerzisztencia.loadGame();
            if (game == null) {
                System.out.println("Nem sikerült betölteni a mentést, új játék indul.");
                game = startNewGame();
            }
        } else {
            game = startNewGame();
        }

        gameLoop();
        scanner.close();
    }

    private boolean hasSavedGame() {
        String sql = "SELECT COUNT(*) FROM saved_games WHERE id = 1";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Hiba a mentett játék ellenőrzése során: " + e.getMessage());
        }
        return false;
    }

    private boolean confirmLoadGame() {
        while (true) {
            System.out.print("Szeretnéd betölteni az előző játékot? (i/n): ");
            String answer = scanner.nextLine().trim().toLowerCase();
            if ("i".equals(answer)) return true;
            if ("n".equals(answer)) return false;
            System.out.println("Érvénytelen válasz. 'i' vagy 'n'.");
        }
    }

    private int chooseBoardSize() {
        while (true) {
            System.out.print("Válassz táblaméretet (3, 5, vagy 10): ");
            try {
                int size = Integer.parseInt(scanner.nextLine().trim());
                if (size == 3 || size == 5 || size == 10) return size;
                System.out.println("Érvénytelen méret. Kérlek, 3, 5, vagy 10 értéket adj meg.");
            } catch (NumberFormatException e) {
                System.out.println("Érvénytelen bemenet. Kérlek, számot adj meg.");
            }
        }
    }

    private AmobaJatek startNewGame() {
        System.out.print("Első játékos neve (X): ");
        String name1 = getPlayerName();

        System.out.print("Második játékos neve (O), vagy írd be, hogy 'Gép' a gépi ellenfélhez: ");
        String name2 = scanner.nextLine();

        boolean isAiOpponent = "Gép".equalsIgnoreCase(name2.trim());
        if (isAiOpponent) {
            name2 = "Gép";
        } else {
            name2 = getPlayerName(name2);
        }

        int size = chooseBoardSize();
        return new AmobaJatek(name1, name2, size, isAiOpponent);
    }

    private String getPlayerName() {
        String name = scanner.nextLine();
        int wins = getPlayerWins(name);
        System.out.println(name + " játékosnak eddig " + wins + " győzelme van.");
        return name;
    }

    private String getPlayerName(String name) {
        int wins = getPlayerWins(name);
        System.out.println(name + " játékosnak eddig " + wins + " győzelme van.");
        return name;
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
            System.out.println("Hiba a játékos győzelmeinek lekérdezése során: " + e.getMessage());
        }
        return 0;
    }

    private void gameLoop() {
        while (!game.isJatekVege()) {
            game.getTabla().printBoard();
            Jatekos currentPlayer = game.getAktualisJatekos();
            System.out.println(currentPlayer + " következik.");

            if (game.isGepiJatekos(currentPlayer)) {
                System.out.println("A gép lép...");
                game.gepiLepes();
                continue;
            }

            System.out.print("Add meg a lépést (pl. '1a'), vagy írj 'mentés'-t vagy 'kilépés'-t: ");
            String input = scanner.nextLine().trim().toLowerCase();

            if ("mentés".equalsIgnoreCase(input)) {
                AdatPerzisztencia.saveGame(game);
                continue;
            }

            if ("kilépés".equalsIgnoreCase(input)) {
                System.out.println("Viszlát!");
                System.exit(0);
            }

            try {
                if (input.length() < 2) throw new IllegalArgumentException("Érvénytelen formátum.");
                char colChar = input.charAt(input.length() - 1);
                int col = colChar - 'a';
                String rowStr = input.substring(0, input.length() - 1);
                int row = Integer.parseInt(rowStr) - 1;

                if (!game.getTabla().isCellValid(row, col)) {
                    throw new IllegalArgumentException("Érvénytelen index!");
                }
                game.lep(row, col);
            } catch (Exception e) {
                System.out.println("Hiba: " + e.getMessage());
            }
        }

        endGame();
    }

    private void endGame() {
        game.getTabla().printBoard();
        Jatekos winner = game.getGyoztes();
        if (winner != null) {
            System.out.println("Gratulálok, " + winner.getName() + " nyert!");
            updatePlayerWins(winner.getName());
        } else {
            System.out.println("A játék döntetlen!");
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
            System.out.println("Hiba a játékos győzelmeinek frissítése során: " + e.getMessage());
        }
    }
}