package org.amoba;

import org.amoba.db.DatabaseManager;
import org.amoba.konzol.KonzolVezerlo;

public class FoProgram {
    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();
        new KonzolVezerlo().start();
    }
}