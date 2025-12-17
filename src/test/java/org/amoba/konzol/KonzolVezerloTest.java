package org.amoba.konzol;

import org.amoba.db.DatabaseManager;
import org.amoba.jatek.AmobaJatek;
import org.amoba.mentes.AdatPerzisztencia;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KonzolVezerloTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;

    private ByteArrayOutputStream outContent;

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockHighScoresStatement;
    @Mock
    private PreparedStatement mockHasSavedGameStatement;
    @Mock
    private PreparedStatement mockPlayerWinsStatement;
    @Mock
    private ResultSet mockHighScoresResultSet;
    @Mock
    private ResultSet mockHasSavedGameResultSet;
    @Mock
    private ResultSet mockPlayerWinsResultSet;

    @BeforeEach
    void setUp() throws SQLException {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        lenient().when(mockConnection.prepareStatement(anyString())).thenReturn(mockPlayerWinsStatement);
        lenient().when(mockPlayerWinsStatement.executeQuery()).thenReturn(mockPlayerWinsResultSet);
        lenient().when(mockPlayerWinsResultSet.next()).thenReturn(false);

        lenient().when(mockConnection.prepareStatement("SELECT name, wins FROM players ORDER BY wins DESC LIMIT 10")).thenReturn(mockHighScoresStatement);
        lenient().when(mockHighScoresStatement.executeQuery()).thenReturn(mockHighScoresResultSet);
        lenient().when(mockHighScoresResultSet.next()).thenReturn(false);

        lenient().when(mockConnection.prepareStatement("SELECT COUNT(*) FROM saved_games WHERE id = 1")).thenReturn(mockHasSavedGameStatement);
        lenient().when(mockHasSavedGameStatement.executeQuery()).thenReturn(mockHasSavedGameResultSet);
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    private void provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    @Test
    void testStart_NewGameAndExit() throws SQLException {
        when(mockHasSavedGameResultSet.next()).thenReturn(false);

        try (MockedStatic<DatabaseManager> mockedDbManager = mockStatic(DatabaseManager.class)) {
            mockedDbManager.when(DatabaseManager::getConnection).thenReturn(mockConnection);

            String input = "n\nPlayer1\nPlayer2\n10\nkilépés\n";
            provideInput(input);

            KonzolVezerlo vezerlo = new KonzolVezerlo();
            vezerlo.start();

            String output = outContent.toString();
            assertTrue(output.contains("a b c d e f g h i j"));
        }
    }

    @Test
    void testStart_LoadGameAndExit() throws SQLException {
        when(mockHasSavedGameResultSet.next()).thenReturn(true);
        when(mockHasSavedGameResultSet.getInt(1)).thenReturn(1);

        try (MockedStatic<DatabaseManager> mockedDbManager = mockStatic(DatabaseManager.class);
             MockedStatic<AdatPerzisztencia> mockedAdatPerzisztencia = mockStatic(AdatPerzisztencia.class)) {
            
            mockedDbManager.when(DatabaseManager::getConnection).thenReturn(mockConnection);
            
            AmobaJatek mockGame = new AmobaJatek("LoadedPlayer1", "LoadedPlayer2", 10, false);
            mockedAdatPerzisztencia.when(AdatPerzisztencia::loadGame).thenReturn(mockGame);
            mockedAdatPerzisztencia.when(() -> AdatPerzisztencia.saveGame(any())).thenAnswer(i -> null);

            String input = "i\nkilépés\n";
            provideInput(input);

            KonzolVezerlo vezerlo = new KonzolVezerlo();
            vezerlo.start();

            String output = outContent.toString();
            assertTrue(output.contains("Szeretnéd betölteni az előző játékot? (i/n):"));
            assertTrue(output.contains("LoadedPlayer1"));
        }
    }
}