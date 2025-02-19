import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

class WebSocketService {
    constructor() {
        this.client = null;
    }

    connectToQueue(playerId, callback) {
        const socket = new SockJS("http://localhost:8080/mindbug-ws");
        this.client = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
        });

        this.client.onConnect = () => {
            console.log("WebSocket connected");
            this.client.subscribe("/topic/game-queue", (message) => {
                console.log("Received message", message.body);
                try {
                    const response = JSON.parse(message.body);
            
                    if (response.messageID === "MATCH_FOUND" && response.data) {
                        console.log("Match found:", response.data);
            
                        if (String(response.data.playerId) === String(playerId)) {
                            console.log("✅ playerId matched");
                            callback(response.data);
                        }
                    } else {
                        console.warn("⚠️ not MATCHFOUND:", response);
                    }
                } catch (error) {
                    console.error("❌  WebSocket message error:", message.body);
                }
            });
        };
        this.client.activate();
    }

    subscribeToGame(gameId, callback) {
        if (!this.client) {
            console.error("❌ WebSocket client is not connected!");
            return;
        }
    
        console.log(`📡 Game Status: /topic/game/${gameId}`);
    
        this.client.subscribe(`/topic/game/${gameId}`, (message) => {
            console.log("🎮 Game state update:", message.body);
            callback(JSON.parse(message.body));
        });
    }
}

export default new WebSocketService();
