<template>
    <div id="home">
      <h1>Welcome to Mindbug's Cards</h1>
      <div class="button-group">
        <button @click="goToSets" class="styled-button">Set of Cards</button>
        <button @click="startGame" class="styled-button">Start Game</button>
      </div>
      <p v-if="message">📢 {{ message }}</p>
    </div>
</template>
  
<script>
import WebSocketService from "@/services/websocket.js";
import axios from "axios";

export default {
  data() {
    return {
      message: "",
      playerId: "player-" + Math.random().toString(36).substr(2, 9),
    };
  },
  methods: {
    goToSets() {
      this.$router.push('/setsofcards');
    },

    async startGame() {
        try {
            WebSocketService.connectToQueue(this.playerId, (data) => {
                console.log("🎮 Match found:", data);
                this.message = `🎮 Match found! Game ID: ${data.gameId}`;

                axios.post("/api/game/confirm_join", {
                    gameId: data.gameId,
                    playerId: this.playerId
                });

                WebSocketService.subscribeToGame(data.gameId, (gameState) => {
                    console.log("🚀 Game started:", gameState);
                    this.message = "🚀 Game has started!";
                    this.$router.push(`/gameterrain/${data.gameId}`);
                });
            });
            console.log("🔍 Sending join_game request...");
            const response = await axios.post("/api/game/join_game", null, {
                params: { playerId: this.playerId }
            });
            console.log("✅ Joined match queue:", response.data);
            this.message = "🔍 Waiting for an opponent...";

            
        } catch (error) {
            console.error("❌ Error occurred while searching for a game:", error.response ? error.response.data : error.message);
            this.message = "❌ Matchmaking error.";
        }
    }

  },
};
</script>
  
<style scoped>
  #home {
    text-align: center;
    margin-top: 50px;
  }
  
  h1 {
    font-size: 2.5rem;
    color: #2c3e50;
    margin-bottom: 40px;
  }
  
  .button-group {
    display: flex;
    justify-content: center;
    gap: 20px; 
  }
  
  .styled-button {
    padding: 12px 30px;
    font-size: 18px;
    color: #fff;
    background-color: #3498db;
    border: none;
    border-radius: 25px;
    cursor: pointer;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    transition: background-color 0.3s, transform 0.2s;
  }
  
  .styled-button:hover {
    background-color: #2980b9;
    transform: scale(1.05); 
  }
  
  .styled-button:active {
    background-color: #1e6f93;
    transform: scale(0.98); 
  }
</style>
