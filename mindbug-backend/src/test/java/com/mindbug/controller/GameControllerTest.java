package com.mindbug.controller;

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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindbug.dtos.PlayerBasicInfoDto;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

// @RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private WebSocketTestHelper wsHelper;

    @Value("${local.server.port}")
    private int port;

    @BeforeEach
    void setup() throws Exception {
       
        
    }

    @AfterEach
    void cleanup() {
        wsHelper.disconnect();
    }

    @Test
    void testJoinGame() throws Exception {
        System.out.println("heloooo");

        // Testing 2 player joining game

        wsHelper = new WebSocketTestHelper(port);
        wsHelper.connect();

        Thread.sleep(1000);

        // Subscirbe to websocket
        wsHelper.subscribe("/topic/game-queue");

        Thread.sleep(500);

        // 1st call
        MvcResult result = mockMvc.perform(post("/api/game/join_game"))
                .andExpect(status().isOk())
                .andReturn();

        Map<String, Object> player1Info = mvcResultToObject(result, Map.class);

        assertNotNull(player1Info,
                "1st call of join game: response should not be null");
        assertNotNull(player1Info.get("playerId"),
                "1st call of join game: playerid should not be null");
        assertNotNull(player1Info.get("nickname"),
                "1st call of join game: nickname should not be null");

        // 2nd call
        result = mockMvc.perform(post("/api/game/join_game"))
                .andExpect(status().isOk())
                .andReturn();

        Map<String, Object> player2Info = mvcResultToObject(result, Map.class);
        assertNotNull(player2Info,
                "2nd call of join game: response should not be null");
        assertNotNull(player2Info.get("playerId"),
                "2nd call of join game: playerid should not be null");
        assertNotNull(player2Info.get("nickname"),
                "1st call of join game: nickname should not be null");

        System.out.println(player2Info);

        // Check message ws
        JsonNode message = wsHelper.getNextMessage(5, TimeUnit.SECONDS);
        System.out.println(message);
        // Map<String, Object> wsMsg = wsHelper.convertMessage(message, Map.class);
        // System.out.println(wsMsg);

        // Testing confirm join by 2 players
        // 1st
        // Map<String, Object> data = new HashMap<>();
        // data.put("playerId", player1Info.getPlayerId());
        // mockMvc.perform(post("/api/game/join_game")
        // .contentType("application/json") // Spécifie le type de contenu
        // .content(playerJson)) // Le corps de la requête JSON
        // .andExpect(status().isOk())

    }

    public <T> T mvcResultToObject(MvcResult result, Class<T> objectType) throws Exception {
        String jsonString = result.getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(jsonString, objectType);
    }
}
