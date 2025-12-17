package org.amoba.modell;


/**
 * A játékost reprezentáló osztály.
 */
public class Jatekos {
    private final String name;
    private final char symbol;

    /**
     * Létrehoz egy új játékost.
     *
     * @param name a játékos neve.
     *
     * @param symbol a játékos jele.
     */
    public Jatekos(String name, char symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    /**
     * Visszaadja a játékos nevét.
     *
     * @return a játékos neve.
     */
    public String getName() {
        return name;
    }

    /**
     * Visszaadja a játékos jelét.
     *
     * @return a játékos jele.
     */
    public char getSymbol() {
        return symbol;
    }

    /**
     * Visszaadja a játékos szöveges reprezentációját.
     *
     * @return a játékos szöveges reprezentációja.
     */
    @Override
    public String toString() {
        return name + " (" + symbol + ")";
    }
}