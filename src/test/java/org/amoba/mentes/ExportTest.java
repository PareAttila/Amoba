package org.amoba.mentes;

import org.amoba.jatek.AmobaJatek;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ExportTest {

    private AmobaJatek jatek;
    private final String testXmlFile = "test_game.xml";
    private final String testJsonFile = "test_game.json";

    @BeforeEach
    void setUp() {
        jatek = new AmobaJatek("Player1", "Player2", 10, false);
    }

    @AfterEach
    void tearDown() {
        new File(testXmlFile).delete();
        new File(testJsonFile).delete();
    }

    @Test
    void testExportToXml() throws IOException {
        Export.exportToFile(jatek, testXmlFile, "xml");
        File file = new File(testXmlFile);
        assertTrue(file.exists());
        String content = new String(Files.readAllBytes(Paths.get(testXmlFile)));
        assertTrue(content.contains("<player1Name>Player1</player1Name>"));
    }

    @Test
    void testExportToJson() throws IOException {
        Export.exportToFile(jatek, testJsonFile, "json");
        File file = new File(testJsonFile);
        assertTrue(file.exists());
        String content = new String(Files.readAllBytes(Paths.get(testJsonFile)));
        assertTrue(content.contains("\"player1Name\" : \"Player1\""));
    }

    @Test
    void testExport_UnknownFormat() {
        Export.exportToFile(jatek, "test.txt", "txt");
        File file = new File("test.txt");
        assertFalse(file.exists());
    }
}