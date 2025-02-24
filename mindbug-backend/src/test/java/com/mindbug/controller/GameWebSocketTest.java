// package com.mindbug.controller;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;

// import java.util.concurrent.TimeUnit;

// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.boot.test.context.SpringBootTest;

// import com.mindbug.WebSocketTestHelper;

// @SpringBootTest
// public class GameWebSocketTest {

    
//     private WebSocketTestHelper wsHelper;
    
//     @BeforeEach
//     void setup() throws Exception {
//         wsHelper = new WebSocketTestHelper(port);
//         wsHelper.connect();
//     }
    
//     @AfterEach
//     void cleanup() {
//         wsHelper.disconnect();
//     }
    
//     @Test
//     void testGameWebSocketCommunication() throws InterruptedException {
//         // Souscription au topic du jeu
//         wsHelper.subscribe("/topic/game-queue");
        
//         // Envoi d'un message
//         GameAction action = new GameAction("MOVE", "player1");
//         wsHelper.sendMessage("/app/game/123/action", action);
        
//         // Réception et vérification du message
//         String message = wsHelper.getNextMessage(5, TimeUnit.SECONDS);
//         GameState gameState = wsHelper.convertMessage(message, GameState.class);
        
//         assertNotNull(gameState);
//         assertEquals("player1", gameState.getCurrentPlayer());
//     }
    
//     @Test
//     void testMultipleMessages() throws InterruptedException {
//         wsHelper.subscribe("/topic/game/456");
        
//         // Test avec plusieurs messages
//         wsHelper.sendMessage("/app/game/456/start", new GameStart());
        
//         String message1 = wsHelper.getNextMessage(2, TimeUnit.SECONDS);
//         String message2 = wsHelper.getNextMessage(2, TimeUnit.SECONDS);
        
//         assertNotNull(message1);
//         assertNotNull(message2);
//     }
// }