package org.amoba;

import org.amoba.db.DatabaseManager;
import org.amoba.konzol.KonzolVezerlo;

import java.io.File;

/**
 * A program belépési pontja.
 */
public class FoProgram {
    /**
     * A program belépési pontja.
     *
     * @param args a parancssori argumentumok.
     */
    public static void main(String[] args) {
        File dbFile = new File("./amoba_db.mv.db");
        if (!dbFile.exists()) {
            DatabaseManager.initializeDatabase();
        }
        KonzolVezerlo vezerlo = new KonzolVezerlo();
        vezerlo.start();
    }
}
