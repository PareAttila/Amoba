package org.amoba.modell;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


//A gépi játékost reprezentáló osztály.
//A Jatekos osztályból öröklődik, és a gépi lépés logikáját tartalmazza.

public class GepiJatekos extends Jatekos {

    private final Random random = new Random();

    public GepiJatekos(String name, char symbol) {
        super(name, symbol);
    }

    //Kiszámolja és visszaadja a gép következő lépését.
    //A lehetséges érvényes lépések közül választ véletlenszerűen.

    public int[] getGepiLepes(Tabla tabla) {
        List<int[]> szabadHelyek = new ArrayList<>();
        for (int i = 0; i < tabla.getSize(); i++) {
            for (int j = 0; j < tabla.getSize(); j++) {
                // Az összes üres helyet összegyűjtjük.
                if (tabla.isCellEmpty(i, j)) {
                    szabadHelyek.add(new int[]{i, j});
                }
            }
        }

        if (szabadHelyek.isEmpty()) {
            return null;
        }
        return szabadHelyek.get(random.nextInt(szabadHelyek.size()));
    }
}