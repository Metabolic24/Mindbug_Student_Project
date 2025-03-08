package com.mindbug;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.mindbug.controller.WebSocketTestHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

// @RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class GameServerTest {

    @Autowired
    private MockMvc mockMvc;

    private WebSocketTestHelper wsHelper;

    @Value("${local.server.port}")
    private int port;

    @BeforeEach
    void setup() throws Exception {
        wsHelper = new WebSocketTestHelper(port);
        wsHelper.connect();
        Thread.sleep(1000);
    }

    @AfterEach
    void cleanup() {
        wsHelper.disconnect();
    }

    @Test
    void testJoinGame() throws Exception {
        // Subscribe to general websocket
        wsHelper.subscribe("/topic/game-queue");
        Thread.sleep(500);

        // 1st player call join game
        System.out.println("*** Testing 1st player calling join game: ***");
        MvcResult result = mockMvc.perform(post("/api/game/join_game"))
                .andExpect(status().isOk())
                .andReturn();

        Map<String, Object> player1Info = Helper.mvcResultToObject(result, Map.class);

        assertNotNull(player1Info,
                "1st call of join game: response should not be null");
        assertNotNull(player1Info.get("playerId"),
                "1st call of join game: playerid should not be null");
        assertNotNull(player1Info.get("nickname"),
                "1st call of join game: nickname should not be null");
        
        System.out.println("*** Testing 1st player calling join game: OK *** \n");

        // 1st player call join game
        System.out.println("*** Testing 2nd player calling join game: ***");
        result = mockMvc.perform(post("/api/game/join_game"))
                .andExpect(status().isOk())
                .andReturn();

        Map<String, Object> player2Info = Helper.mvcResultToObject(result, Map.class);
        assertNotNull(player2Info,
                "2nd call of join game: response should not be null");
        assertNotNull(player2Info.get("playerId"),
                "2nd call of join game: playerid should not be null");
        assertNotNull(player2Info.get("nickname"),
                "1st call of join game: nickname should not be null");

        System.out.println("*** Testing 2nd player calling join game: OK***\n");

        // Check match found message recevied twice
        System.out.println("*** Testing match found messages: ***");
        Thread.sleep(500);
        JsonNode message1 = wsHelper.getNextMessage(5, TimeUnit.SECONDS);
        JsonNode message2 = wsHelper.getNextMessage(5, TimeUnit.SECONDS);

        assertNotNull(message1, "Websocket match found first message should not be null");
        assertNotNull(message2, "Websocket match found second  message should not be null");

        System.out.println("Testing 1st match found message");
        testWSMachtFoundMsg(message1, player1Info, player2Info);

        System.out.println("Testing 2nd match found message");
        testWSMachtFoundMsg(message2, player1Info, player2Info);

        System.out.println("*** Testing match found messages: OK ***\n");

        // Subscirbe to game websocket
        wsHelper.subscribe("/topic/game/" + message1.get("data").get("gameId"));
        Thread.sleep(500);
        
        // Testing confirm join
        System.out.println("*** Testing 1st player confirm join: ***");
        testConfirmJoin(player1Info, message1.get("data").get("gameId").asInt());
        System.out.println("*** Testing 1st player confirm join: OK \n***");

        System.out.println("*** Testing 1st player confirm join: ***");
        testConfirmJoin(player2Info, message1.get("data").get("gameId").asInt());
        System.out.println("*** Testing 1st player confirm join: OK \n***");

        // Testing new game ws message
        System.out.println("*** Testing new game websocket messages: ***");
        Thread.sleep(500);
        JsonNode wsMsgNewGame = wsHelper.getNextMessage(5, TimeUnit.SECONDS);
        
        assertNotNull(wsMsgNewGame, "New game websocket message should not be null");
        assertTrue(wsMsgNewGame.has("messageID"), "New game websoket message should have field messageID");
        assertEquals("newGame", wsMsgNewGame.get("messageID").asText(), "New game websoket message field messageID should be equals to newGame");

        assertTrue(wsMsgNewGame.has("data"), "New game websoket message  should have field data");
        assertTrue(wsMsgNewGame.get("data").has("id"), "New game websoket message should have field data.id");
        assertEquals(message1.get("data").get("gameId").asInt(),
        wsMsgNewGame.get("data").get("id").asInt(),
        "New game websoket message data.id should be equals to match found websocket match found msg gameId");
        System.out.println("*** Testing new game websocket messages: OK ***\n");


    }

    private void testWSMachtFoundMsg(JsonNode message, Map<String, Object>  player1Info, Map<String, Object>  player2Info) {
        assertTrue(message.has("messageID"), "Match found websocket message should have field messsageID");
        assertEquals("MATCH_FOUND", message.get("messageID").asText(),
        "Match found websocket message field messageID should be equals to MATCH_FOUND");

        assertTrue(message.has("data"), "Match found websocket message should have field data");
        assertNotNull(message.get("data"), "Match found websocket message field data should not be null");

        assertTrue(message.get("data").has("playerId"), "Match found websocket message shoudl have field data.playerId");
        assertTrue(message.get("data").get("playerId").intValue() == ((Integer)player1Info.get("playerId")) ||
        message.get("data").get("playerId").intValue() == ((Integer)player2Info.get("playerId")),
        "Match found websocket message field data.playerId shoulb equals to either player1 id or player 2 id");

        assertTrue(message.get("data").has("gameId"), "Match found websocket message should have field data.gameId");
        assertNotNull(message.get("data").get("gameId")
        , "Match found websocket message field gameId should not be null");
    }

    private void testConfirmJoin(Map<String, Object>  playerInfo, int gameId) throws Exception {
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
