package org.amoba.jatek;

import org.amoba.modell.GepiJatekos;
import org.amoba.modell.Jatekos;
import org.amoba.modell.Tabla;


/**
 * A központi játéklogikát és állapotot kezelő osztály.
 */
public class AmobaJatek {
    private final Tabla tabla;
    private final Jatekos[] jatekosok;
    private int aktualisJatekosIndex;
    private boolean jatekVege;
    private Jatekos gyoztes;
    private final boolean gepiEllenfel; // Igaz, ha a második játékos gép
    private final int winLength; // Hány jel kell a győzelemhez

    public AmobaJatek(String jatekos1Nev, String jatekos2Nev, int tablaMeret, boolean gepiEllenfel) {
        this.winLength = (tablaMeret == 3) ? 3 : 5;
        this.tabla = new Tabla(tablaMeret, this.winLength);
        this.jatekosok = new Jatekos[2];
        this.jatekosok[0] = new Jatekos(jatekos1Nev, 'X'); // jelenleg az első játékos mindig ember
        if (gepiEllenfel) {
            this.jatekosok[1] = new GepiJatekos(jatekos2Nev, 'O');
        } else {
            this.jatekosok[1] = new Jatekos(jatekos2Nev, 'O');
        }
        this.aktualisJatekosIndex = 0;
        this.jatekVege = false;
        this.gyoztes = null;
        this.gepiEllenfel = gepiEllenfel;
    }

    // Betöltéshez használt konstruktor
    public AmobaJatek(Tabla tabla, Jatekos[] jatekosok, int aktualisJatekosIndex, boolean gepiEllenfel) {
        this.tabla = tabla;
        this.jatekosok = jatekosok;
        // Biztosítjuk, hogy a gépi játékos GepiJatekos típusú legyen betöltés után is
        if (gepiEllenfel && !(this.jatekosok[1] instanceof GepiJatekos)) {
            this.jatekosok[1] = new GepiJatekos(this.jatekosok[1].getName(), this.jatekosok[1].getSymbol());
        }
        this.winLength = tabla.getWinLength();
        this.aktualisJatekosIndex = aktualisJatekosIndex;
        this.jatekVege = false;
        this.gyoztes = null;
        this.gepiEllenfel = gepiEllenfel;
    }

    public void lep(int sor, int oszlop) {
        if (jatekVege) {
            throw new IllegalStateException("A játék már véget ért.");
        }
        if (!tabla.isCellEmpty(sor, oszlop)) {
            throw new IllegalArgumentException("Ez a mező már foglalt!");
        }

        Jatekos aktualisJatekos = getAktualisJatekos();
        tabla.makeMove(sor, oszlop, aktualisJatekos.getSymbol());

        if (tabla.checkWin(aktualisJatekos.getSymbol())) {
            jatekVege = true;
            gyoztes = aktualisJatekos;
        } else if (tabla.isBoardFull()) {
            jatekVege = true;
        } else {
            aktualisJatekosIndex = (aktualisJatekosIndex + 1) % 2;
        }
    }

    /**
     * A gépi ellenfél végrehajt egy véletlenszerű, érvényes lépést.
     */
    public void gepiLepes() {
        Jatekos aktualisJatekos = getAktualisJatekos();
        if (jatekVege || !isGepiJatekos(aktualisJatekos)) {
            return;
        }

        GepiJatekos gep = (GepiJatekos) aktualisJatekos;
        int[] lepes = gep.getGepiLepes(tabla);
        if (lepes != null) {
            lep(lepes[0], lepes[1]);
        }
    }


    public Tabla getTabla() {
        return tabla;
    }

    public Jatekos[] getJatekosok() {
        return jatekosok;
    }

    public Jatekos getAktualisJatekos() {
        return jatekosok[aktualisJatekosIndex];
    }

    public int getAktualisJatekosIndex() {
        return aktualisJatekosIndex;
    }

    public boolean isJatekVege() {
        return jatekVege;
    }

    public Jatekos getGyoztes() {
        return gyoztes;
    }

    public boolean isGepiJatekos(Jatekos jatekos) {
        return gepiEllenfel && jatekos instanceof GepiJatekos;
    }

    public int getWinLength() {
        return winLength;
    }
}