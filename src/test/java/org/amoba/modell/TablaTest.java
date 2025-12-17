package org.amoba.modell;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
}