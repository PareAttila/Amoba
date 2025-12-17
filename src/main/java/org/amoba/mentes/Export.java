package org.amoba.mentes;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.amoba.jatek.AmobaJatek;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A játékállás exportálásáért felelős osztály.
 */
public class Export {
    private static final Logger LOGGER = LoggerFactory.getLogger(Export.class);

    /**
     * Exportálja a játékállást a megadott formátumban.
     *
     * @param jatek a játék, amit exportálni kell.
     *
     * @param fileName a fájl neve, amibe exportálni kell.
     *
     * @param format az exportálás formátuma (xml vagy json).
     */
    public static void exportToFile(AmobaJatek jatek, String fileName, String format) {
        if ("xml".equalsIgnoreCase(format)) {
            exportToXml(jatek, fileName);
        } else if ("json".equalsIgnoreCase(format)) {
            exportToJson(jatek, fileName);
        } else {
            LOGGER.warn("Ismeretlen export formátum: {}", format);
        }
    }

    private static void exportToXml(AmobaJatek jatek, String fileName) {
        ObjectMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            xmlMapper.writeValue(new File(fileName), jatek);
            LOGGER.info("Játékállás sikeresen exportálva a(z) {} fájlba.", fileName);
        } catch (IOException e) {
            LOGGER.error("Hiba történt az XML exportálás során: {}", e.getMessage());
        }
    }

    private static void exportToJson(AmobaJatek jatek, String fileName) {
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            jsonMapper.writeValue(new File(fileName), jatek);
            LOGGER.info("Játékállás sikeresen exportálva a(z) {} fájlba.", fileName);
        } catch (IOException e) {
            LOGGER.error("Hiba történt a JSON exportálás során: {}", e.getMessage());
        }
    }
}