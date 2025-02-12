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
            console.log("✅ WebSocket connected");

            this.client.subscribe("/topic/queue", (message) => {
                const data = JSON.parse(message.body);
                if (data.playerId === playerId) {
                    console.log("🎮 Match found:", data);
                    callback(data);
                }
            });
        };

        this.client.activate();
    }

    subscribeToGame(gameId, callback) {
        this.client.subscribe(`/topic/game/${gameId}`, (message) => {
            console.log("🎮 Game state update:", message.body);
            callback(JSON.parse(message.body));
        });
    }

    disconnect() {
        if (this.client) {
            this.client.deactivate();
            console.log("❌ WebSocket disconnected");
        }
    }
}

export default new WebSocketService();
