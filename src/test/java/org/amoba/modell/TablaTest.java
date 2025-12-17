package org.amoba.modell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TablaTest {

    private Tabla tabla;

    @BeforeEach
    void setUp() {
        tabla = new Tabla(10, 5);
    }

    @Test
    void testMakeMove() {
        tabla.makeMove(0, 0, 'X');
        assertEquals('X', tabla.getGrid()[0][0]);
    }

    @Test
    void testMakeMove_OutOfBounds() {
        tabla.makeMove(10, 10, 'X');
        assertTrue(tabla.isBoardEmpty());
    }

    @Test
    void testIsBoardFull() {
        assertFalse(tabla.isBoardFull());
        for (int i = 0; i < tabla.getSize(); i++) {
            for (int j = 0; j < tabla.getSize(); j++) {
                tabla.makeMove(i, j, 'X');
            }
        }
        assertTrue(tabla.isBoardFull());
    }

    @Test
    void testCheckWin_Horizontal() {
        for (int i = 0; i < 5; i++) {
            tabla.makeMove(0, i, 'X');
        }
        assertTrue(tabla.checkWin('X'));
    }

    @Test
    void testCheckWin_Vertical() {
        for (int i = 0; i < 5; i++) {
            tabla.makeMove(i, 0, 'O');
        }
        assertTrue(tabla.checkWin('O'));
    }

    @Test
    void testCheckWin_Diagonal() {
        for (int i = 0; i < 5; i++) {
            tabla.makeMove(i, i, 'X');
        }
        assertTrue(tabla.checkWin('X'));
    }

    @Test
    void testCheckWin_AntiDiagonal() {
        for (int i = 0; i < 5; i++) {
            tabla.makeMove(i, 4 - i, 'O');
        }
        assertTrue(tabla.checkWin('O'));
    }

    @Test
    void testCheckWin_NoWin() {
        assertFalse(tabla.checkWin('X'));
    }

    @Test
    void testCheckWin_Draw() {
        for (int i = 0; i < tabla.getSize(); i++) {
            for (int j = 0; j < tabla.getSize(); j++) {
                char symbol;
                if (i % 2 == 0) {
                    symbol = (j % 4 < 2) ? 'X' : 'O';
                } else {
                    symbol = (j % 4 < 2) ? 'O' : 'X';
                }
                tabla.makeMove(i, j, symbol);
            }
        }
        assertFalse(tabla.checkWin('X'), "X should not win");
        assertFalse(tabla.checkWin('O'), "O should not win");
        assertTrue(tabla.isBoardFull(), "Board should be full");
    }

    @Test
    void testIsMoveLegal() {
        assertTrue(tabla.isMoveLegal(5, 5));
        tabla.makeMove(0, 0, 'X');
        assertTrue(tabla.isMoveLegal(0, 1));
        assertFalse(tabla.isMoveLegal(0, 0));
        assertFalse(tabla.isMoveLegal(5, 5));
    }
}