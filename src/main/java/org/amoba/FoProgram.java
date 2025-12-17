package org.amoba;

import org.amoba.db.DatabaseManager;
import org.amoba.konzol.KonzolVezerlo;

/**
 * A program belépési pontja.
 */
public class FoProgram {
    /**
     * Inicializálja az adatbázist és elindítja a konzolos vezérlőt.
     *
     * @param args a parancssori argumentumok.
     */
    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();
        new KonzolVezerlo().start();
    }
}