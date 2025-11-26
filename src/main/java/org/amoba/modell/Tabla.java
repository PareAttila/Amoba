package org.amoba.modell;

import java.util.Arrays;

/**
 * A játéktáblát és a hozzá kapcsolódó logikát kezelő osztály.
 */
public class Tabla {
    private final int size;
    private final char[][] grid;
    private final int winLength; // Hány jel kell a győzelemhez

    public Tabla(int size, int winLength) {
        this.size = size;
        this.winLength = winLength;
        this.grid = new char[size][size];
        for (char[] row : grid) {
            Arrays.fill(row, '.');
        }
    }

    public int getSize() {
        return size;
    }

    public int getWinLength() {
        return winLength;
    }

    public char[][] getGrid() {
        return grid;
    }

    public boolean isCellEmpty(int row, int col) {
        return isCellValid(row, col) && grid[row][col] == '.';
    }

    public boolean isCellValid(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }

    public void makeMove(int row, int col, char symbol) {
        if (isCellValid(row, col)) {
            grid[row][col] = symbol;
        }
    }

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

    public boolean checkWin(char symbol) {
        // Vízszintes, függőleges és átlós ellenőrzés
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] == symbol) {
                    if (checkDirection(i, j, 1, 0, symbol) || // Vízszintes
                        checkDirection(i, j, 0, 1, symbol) || // Függőleges
                        checkDirection(i, j, 1, 1, symbol) || // Átlós (jobbra le)
                        checkDirection(i, j, 1, -1, symbol)) { // Átlós (jobbra fel)
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

    public void printBoard() {
        // A bal felső sarok behúzásának dinamikus beállítása a sorszámok szélessége alapján
        String headerPadding = new String(new char[String.valueOf(size).length() + 1]).replace('\0', ' ');
        System.out.print(headerPadding);
        for (int i = 0; i < size; i++) {
            System.out.print((char) ('a' + i) + " ");
        }
        System.out.println();

        for (int i = 0; i < size; i++) {
            System.out.printf("%" + String.valueOf(size).length() + "d ", i + 1);
            for (int j = 0; j < size; j++) {
                System.out.print(grid[i][j] + " "); // Visszaállítjuk szimpla szóközre
            }
            System.out.println();
        }
    }
}