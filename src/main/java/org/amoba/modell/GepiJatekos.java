package org.amoba.modell;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A gépi játékost reprezentáló osztály.
 */
public class GepiJatekos extends Jatekos {

    private final Random random = new Random();

    /**
     * Létrehoz egy új gépi játékost.
     *
     * @param name a gépi játékos neve.
     *
     * @param symbol a gépi játékos jele.
     */
    public GepiJatekos(String name, char symbol) {
        super(name, symbol);
    }

    /**
     * Visszaadja a gép lépését.
     *
     * @param tabla a játéktábla.
     *
     * @return a gép lépése, vagy null, ha nincs legális lépés.
     */
    public int[] getGepiLepes(Tabla tabla) {
        List<int[]> legalMoves = new ArrayList<>();
        for (int i = 0; i < tabla.getSize(); i++) {
            for (int j = 0; j < tabla.getSize(); j++) {
                if (tabla.isMoveLegal(i, j)) {
                    legalMoves.add(new int[]{i, j});
                }
            }
        }

        if (legalMoves.isEmpty()) {
            return null;
        }
        return legalMoves.get(random.nextInt(legalMoves.size()));
    }
}