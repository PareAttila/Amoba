package org.amoba.mentes;

import org.amoba.modell.Tabla;

/**
 * Ez az osztály egyetlen objektumba csomagolja a mentéshez szükséges adatokat:
 * a tábla állapotát és a soron következő játékos indexét.
 */
public class JatekAllapot {
    private final Tabla tabla;
    private final int currentPlayerIndex;

    public JatekAllapot(Tabla tabla, int currentPlayerIndex) {
        this.tabla = tabla;
        this.currentPlayerIndex = currentPlayerIndex;
    }

    public Tabla getTabla() {
        return tabla;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }
}