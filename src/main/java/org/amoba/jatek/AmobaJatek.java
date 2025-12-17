package org.amoba.jatek;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.amoba.modell.Tabla;

/**
 * Az amőba játék logikáját tartalmazó osztály.
 */
public class AmobaJatek {
    private final Tabla tabla;
    private final Jatekos[] jatekosok;
    private int aktualisJatekosIndex;
    private boolean jatekVege;
    private Jatekos gyoztes;
    private final boolean gepiEllenfel;
    private final int winLength;

    /**
     * Létrehoz egy új amőba játékot.
     *
     * @param jatekos1Nev  az első játékos neve.
     * @param jatekos2Nev  a második játékos neve.
     * @param tablaMeret   a játéktábla mérete.
     * @param gepiEllenfel igaz, ha a második játékos gép.
     */
    public AmobaJatek(String jatekos1Nev, String jatekos2Nev, int tablaMeret, boolean gepiEllenfel) {
        this.winLength = (tablaMeret == 3) ? 3 : 5;
        this.tabla = new Tabla(tablaMeret, this.winLength);
        this.jatekosok = new Jatekos[2];
        this.jatekosok[0] = new Jatekos(jatekos1Nev, 'X');
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

    /**
     * Létrehoz egy amőba játékot egy meglévő állapotból.
     *
     * @param tabla                a játéktábla.
     * @param jatekosok            a játékosok.
     * @param aktualisJatekosIndex az aktuális játékos indexe.
     * @param gepiEllenfel         igaz, ha a második játékos gép.
     */
    public AmobaJatek(Tabla tabla, Jatekos[] jatekosok, int aktualisJatekosIndex, boolean gepiEllenfel) {
        this.tabla = tabla;
        this.jatekosok = jatekosok;
        if (gepiEllenfel && !(this.jatekosok[1] instanceof GepiJatekos)) {
            this.jatekosok[1] = new GepiJatekos(this.jatekosok[1].getName(), this.jatekosok[1].getSymbol());
        }
        this.winLength = tabla.getWinLength();
        this.aktualisJatekosIndex = aktualisJatekosIndex;
        this.jatekVege = false;
        this.gyoztes = null;
        this.gepiEllenfel = gepiEllenfel;
    }

    /**
     * A játékos lépése.
     *
     * @param sor    a sor, ahova a játékos lép.
     * @param oszlop az oszlop, ahova a játékos lép.
     */
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
            kovetkezoJatekosraValt();
        }
    }

    private void kovetkezoJatekosraValt() {
        aktualisJatekosIndex = (aktualisJatekosIndex + 1) % 2;
    }

    /**
     * A gép lépése.
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

    /**
     * Visszaadja a játéktáblát.
     *
     * @return a játéktábla.
     */
    public Tabla getTabla() {
        return tabla;
    }

    /**
     * Visszaadja a játékosokat.
     *
     * @return a játékosok.
     */
    public Jatekos[] getJatekosok() {
        return jatekosok;
    }

    /**
     * Visszaadja az aktuális játékost.
     *
     * @return az aktuális játékos.
     */
    @JsonIgnore
    public Jatekos getAktualisJatekos() {
        return jatekosok[aktualisJatekosIndex];
    }

    /**
     * Visszaadja az aktuális játékos indexét.
     *
     * @return az aktuális játékos indexe.
     */
    public int getAktualisJatekosIndex() {
        return aktualisJatekosIndex;
    }

    /**
     * Visszaadja, hogy a játéknak vége van-e.
     *
     * @return igaz, ha a játéknak vége van.
     */
    public boolean isJatekVege() {
        return jatekVege;
    }

    /**
     * Visszaadja a győztes játékost.
     *
     * @return a győztes játékos, vagy null, ha nincs győztes.
     */
    public Jatekos getGyoztes() {
        return gyoztes;
    }

    /**
     * Visszaadja, hogy a játékos gép-e.
     *
     * @param jatekos a játékos.
     * @return igaz, ha a játékos gép.
     */
    public boolean isGepiJatekos(Jatekos jatekos) {
        return gepiEllenfel && jatekos instanceof GepiJatekos;
    }

    /**
     * Visszaadja a győzelemhez szükséges jelek számát.
     *
     * @return a győzelemhez szükséges jelek száma.
     */
    public int getWinLength() {
        return winLength;
    }

    public String getPlayer1Name() {
        return jatekosok[0].getName();
    }

    public String getPlayer2Name() {
        return jatekosok[1].getName();
    }
}