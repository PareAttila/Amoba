package org.amoba.modell;

import java.util.Arrays;

/**
 * A játéktáblát reprezentáló osztály.
 */
public class Tabla {
    private final int size;
    private final char[][] grid;
    private final int winLength;

    /**
     * Létrehoz egy új játéktáblát.
     *
     * @param size a tábla mérete.
     *
     * @param winLength a győzelemhez szükséges jelek száma.
     */
    public Tabla(int size, int winLength) {
        this.size = size;
        this.winLength = winLength;
        this.grid = new char[size][size];
        for (char[] row : grid) {
            Arrays.fill(row, '.');
        }
    }

    /**
     * Visszaadja a tábla méretét.
     *
     * @return a tábla mérete.
     */
    public int getSize() {
        return size;
    }

    /**
     * Visszaadja a győzelemhez szükséges jelek számát.
     *
     * @return a győzelemhez szükséges jelek száma.
     */
    public int getWinLength() {
        return winLength;
    }

    /**
     * Visszaadja a tábla rácsát.
     *
     * @return a tábla rácsa.
     */
    public char[][] getGrid() {
        return grid;
    }

    /**
     * Visszaadja, hogy a cella üres-e.
     *
     * @param row a sor.
     *
     * @param col az oszlop.
     *
     * @return igaz, ha a cella üres.
     */
    public boolean isCellEmpty(int row, int col) {
        return isCellValid(row, col) && grid[row][col] == '.';
    }

    /**
     * Visszaadja, hogy a cella érvényes-e.
     *
     * @param row a sor.
     *
     * @param col az oszlop.
     *
     * @return igaz, ha a cella érvényes.
     */
    public boolean isCellValid(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }

    /**
     * Lépést tesz a táblán.
     *
     * @param row a sor.
     *
     * @param col az oszlop.
     *
     * @param symbol a játékos jele.
     */
    public void makeMove(int row, int col, char symbol) {
        if (isCellValid(row, col)) {
            grid[row][col] = symbol;
        }
    }

    /**
     * Visszaadja, hogy a tábla üres-e.
     *
     * @return igaz, ha a tábla üres.
     */
    public boolean isBoardEmpty() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] != '.') {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Visszaadja, hogy a lépés legális-e.
     *
     * @param row a sor.
     *
     * @param col az oszlop.
     *
     * @return igaz, ha a lépés legális.
     */
    public boolean isMoveLegal(int row, int col) {
        if (!isCellEmpty(row, col)) {
            return false;
        }
        if (isBoardEmpty()) {
            return true;
        }
        return isAdjacentToExistingMove(row, col);
    }

    private boolean isAdjacentToExistingMove(int row, int col) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                int newRow = row + i;
                int newCol = col + j;
                if (isCellValid(newRow, newCol) && !isCellEmpty(newRow, newCol)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Visszaadja, hogy a tábla tele van-e.
     *
     * @return igaz, ha a tábla tele van.
     */
    public boolean isBoardFull() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] == '.') {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Ellenőrzi, hogy a játékos nyert-e.
     *
     * @param symbol a játékos jele.
     *
     * @return igaz, ha a játékos nyert.
     */
    public boolean checkWin(char symbol) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] == symbol) {
                    if (checkDirection(i, j, 1, 0, symbol) ||
                        checkDirection(i, j, 0, 1, symbol) ||
                        checkDirection(i, j, 1, 1, symbol) ||
                        checkDirection(i, j, 1, -1, symbol)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkDirection(int startRow, int startCol, int drow, int dcol, char symbol) {
        for (int i = 0; i < winLength; i++) {
            int row = startRow + i * drow;
            int col = startCol + i * dcol;
            if (!isCellValid(row, col) || grid[row][col] != symbol) {
                return false;
            }
        }
        return true;
    }

    /**
     * Kiírja a táblát a konzolra.
     */
    public void printBoard() {
        String headerPadding = new String(new char[String.valueOf(size).length() + 1]).replace('\0', ' ');
        System.out.print(headerPadding);
        for (int i = 0; i < size; i++) {
            System.out.print((char) ('a' + i) + " ");
        }
        System.out.println();

        for (int i = 0; i < size; i++) {
            System.out.printf("%" + String.valueOf(size).length() + "d ", i + 1);
            for (int j = 0; j < size; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
    }
}