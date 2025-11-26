package org.amoba.modell;

//A Jatekos osztály tárolja egy játékos adatait.
public class Jatekos {
    private final String name;
    private final char symbol;

    public Jatekos(String name, char symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public char getSymbol() {
        return symbol;
    }

    //Szép formátumú kiíráshoz (pl. "Alma (X)")
    @Override
    public String toString() {
        return name + " (" + symbol + ")";
    }
}