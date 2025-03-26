package com.mindbug;

import com.fasterxml.jackson.databind.JsonNode;
import com.mindbug.controller.WebSocketTestHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class GameServerTest {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private MockMvc mockMvc;

    private WebSocketTestHelper wsHelper;

    // Setup before each test to establish WebSocket connection
    @BeforeEach
    void setup() throws Exception {
        wsHelper = new WebSocketTestHelper(port);
        wsHelper.connect();
        // Wait for the connection to establish
        Thread.sleep(1000);
    }

    // Clean up after each test by disconnecting the WebSocket
    @AfterEach
    void cleanup() {
        wsHelper.disconnect();
    }

    @Test
    @DisplayName("Check join_game responses for 2 players")
    void testJoinGameResponses() throws Exception {
        wsHelper.subscribe("/topic/game-queue");
        Thread.sleep(500);

        // 1st player joins the game
        MvcResult result1 = mockMvc.perform(post("/api/game/join_game"))
                .andExpect(status().isOk())
                .andReturn();
        Map<String, Object> player1Info = Helper.mvcResultToObject(result1, Map.class);
        assertNotNull(player1Info.get("playerId"), "playerId of 1st player should not be null");
        assertNotNull(player1Info.get("nickname"), "nickname of 1st player should not be null");

        // 2nd player joins the game
        MvcResult result2 = mockMvc.perform(post("/api/game/join_game"))
                .andExpect(status().isOk())
                .andReturn();
        Map<String, Object> player2Info = Helper.mvcResultToObject(result2, Map.class);
        assertNotNull(player2Info.get("playerId"), "playerId of 2nd player should not be null");
        assertNotNull(player2Info.get("nickname"), "nickname of 2nd player should not be null");
    }

    @Test
    @DisplayName("Check MATCH_FOUND messages on WebSocket")
    void testMatchFoundMessages() throws Exception {
        wsHelper.subscribe("/topic/game-queue");
        Thread.sleep(500);

        // Perform 2 join_game calls to trigger MATCH_FOUND messages
        MvcResult result1 = mockMvc.perform(post("/api/game/join_game"))
                .andExpect(status().isOk())
                .andReturn();
        Map<String, Object> player1Info = Helper.mvcResultToObject(result1, Map.class);

        MvcResult result2 = mockMvc.perform(post("/api/game/join_game"))
                .andExpect(status().isOk())
                .andReturn();
        Map<String, Object> player2Info = Helper.mvcResultToObject(result2, Map.class);

        // Retrieve two MATCH_FOUND messages from WebSocket
        Thread.sleep(500);
        JsonNode message1 = wsHelper.getNextMessage(5, TimeUnit.SECONDS);
        JsonNode message2 = wsHelper.getNextMessage(5, TimeUnit.SECONDS);

        assertNotNull(message1, "1st MATCH_FOUND message should not be null");
        assertNotNull(message2, "2nd MATCH_FOUND message should not be null");

        // Test the contents of the MATCH_FOUND messages
        testWSMatchFoundMsg(message1, player1Info, player2Info);
        testWSMatchFoundMsg(message2, player1Info, player2Info);
    }

    @Test
    @DisplayName("Check confirm join and newGame message")
    void testConfirmJoinAndNewGameMessage() throws Exception {
        wsHelper.subscribe("/topic/game-queue");
        Thread.sleep(500);

        // Perform join_game to get the gameId from the MATCH_FOUND message
        MvcResult result1 = mockMvc.perform(post("/api/game/join_game"))
                .andExpect(status().isOk())
                .andReturn();
        Map<String, Object> player1Info = Helper.mvcResultToObject(result1, Map.class);

        MvcResult result2 = mockMvc.perform(post("/api/game/join_game"))
                .andExpect(status().isOk())
                .andReturn();
        Map<String, Object> player2Info = Helper.mvcResultToObject(result2, Map.class);

        // Retrieve the MATCH_FOUND message to get the gameId
        Thread.sleep(500);
        JsonNode matchFoundMsg = wsHelper.getNextMessage(5, TimeUnit.SECONDS);
        int gameId = matchFoundMsg.get("data").get("gameId").asInt();

        // Confirm the join for both players
        testConfirmJoin(player1Info, gameId);
        testConfirmJoin(player2Info, gameId);

        // Verify the newGame message
        Thread.sleep(500);
        JsonNode wsMsgNewGame = wsHelper.getNextMessage(5, TimeUnit.SECONDS);
        assertNotNull(wsMsgNewGame, "newGame message should not be null");
        assertEquals("newGame", wsMsgNewGame.get("messageID").asText(), "MessageID should be 'newGame'");
        assertEquals(gameId, wsMsgNewGame.get("data").get("id").asInt(),
                "The gameId in newGame message should match the gameId in MATCH_FOUND message");
    }

    // Helper method to validate MATCH_FOUND WebSocket message
    private void testWSMatchFoundMsg(JsonNode message, Map<String, Object> player1Info, Map<String, Object> player2Info) {
        assertTrue(message.has("messageID"), "MATCH_FOUND message should have 'messageID'");
        assertEquals("MATCH_FOUND", message.get("messageID").asText(),
                "messageID should be 'MATCH_FOUND'");

        assertTrue(message.has("data"), "MATCH_FOUND message should have 'data'");
        JsonNode data = message.get("data");
        assertNotNull(data, "The 'data' field in MATCH_FOUND message should not be null");
        assertTrue(data.has("playerId"), "Data should have 'playerId'");
        int playerId = data.get("playerId").intValue();
        assertTrue(playerId == ((Integer) player1Info.get("playerId"))
                        || playerId == ((Integer) player2Info.get("playerId")),
                "playerId should match either player 1 or player 2");

        assertTrue(data.has("gameId"), "Data should have 'gameId'");
        assertNotNull(data.get("gameId"), "gameId should not be null");
    }

    // Helper method to test the join confirmation
    private void testConfirmJoin(Map<String, Object> playerInfo, int gameId) throws Exception {
        Map<String, Object> reqBody = new HashMap<>();
        reqBody.put("playerId", playerInfo.get("playerId"));
        reqBody.put("gameId", gameId);

        String reqBodyJSON = Helper.convertMapToJson(reqBody);

        mockMvc.perform(post("/api/game/confirm_join")
                .contentType("application/json")
                .content(reqBodyJSON))
                .andExpect(status().isOk());
    }
}
