package org.amoba;

import org.amoba.konzol.KonzolVezerlo;

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
        KonzolVezerlo vezerlo = new KonzolVezerlo();
        vezerlo.start();
    }
}