package org.amoba.mentes;

import org.amoba.db.DatabaseManager;
import org.amoba.jatek.AmobaJatek;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdatPerzisztenciaTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockStatement;
    @Mock
    private ResultSet mockResultSet;

    @Test
    void testSaveGame() throws Exception {
        try (MockedStatic<DatabaseManager> mockedDbManager = mockStatic(DatabaseManager.class)) {
            mockedDbManager.when(DatabaseManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

            AmobaJatek jatek = new AmobaJatek("Player1", "Player2", 10, false);
            AdatPerzisztencia.saveGame(jatek);

            verify(mockStatement).executeUpdate();
        }
    }

    @Test
    void testSaveGame_DatabaseError() throws Exception {
        try (MockedStatic<DatabaseManager> mockedDbManager = mockStatic(DatabaseManager.class)) {
            mockedDbManager.when(DatabaseManager::getConnection).thenThrow(new SQLException("Database error"));

            AmobaJatek jatek = new AmobaJatek("Player1", "Player2", 10, false);
            AdatPerzisztencia.saveGame(jatek);
        }
    }

    @Test
    void testLoadGame() throws Exception {
        try (MockedStatic<DatabaseManager> mockedDbManager = mockStatic(DatabaseManager.class)) {
            mockedDbManager.when(DatabaseManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getString("player1_name")).thenReturn("Player1");
            when(mockResultSet.getString("player2_name")).thenReturn("Player2");
            when(mockResultSet.getInt("board_size")).thenReturn(10);
            when(mockResultSet.getInt("win_length")).thenReturn(5);
            when(mockResultSet.getInt("current_player_index")).thenReturn(0);
            when(mockResultSet.getString("board_state")).thenReturn(new String(new char[100]).replace('\0', '.'));

            AmobaJatek loadedGame = AdatPerzisztencia.loadGame();

            assertNotNull(loadedGame);
            assertEquals("Player1", loadedGame.getJatekosok()[0].getName());
            assertEquals(10, loadedGame.getTabla().getSize());
        }
    }

    @Test
    void testLoadGame_NoGameFound() throws Exception {
        try (MockedStatic<DatabaseManager> mockedDbManager = mockStatic(DatabaseManager.class)) {
            mockedDbManager.when(DatabaseManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);

            AmobaJatek loadedGame = AdatPerzisztencia.loadGame();

            assertNull(loadedGame);
        }
    }

    @Test
    void testLoadGame_DatabaseError() throws Exception {
        try (MockedStatic<DatabaseManager> mockedDbManager = mockStatic(DatabaseManager.class)) {
            mockedDbManager.when(DatabaseManager::getConnection).thenThrow(new SQLException("Database error"));

            AmobaJatek loadedGame = AdatPerzisztencia.loadGame();

            assertNull(loadedGame);
        }
    }
}