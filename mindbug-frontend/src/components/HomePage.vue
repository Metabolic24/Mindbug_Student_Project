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
          playerId: null, 
          hasConfirmed: false,
      };
  },
  methods: {
      goToSets() {
          this.$router.push('/setsofcards');
      },

      async startGame() {
          try {
              console.log("Websocket connecting...");
              alert("Matching...");
              const response = await axios.post("http://localhost:8080/api/game/join_game");
              this.playerId = response.data.playerId; //get the playerId from backend generated
              console.log("Verify the Id got from back:", this.playerId);
              // const webSocketInstance = new WebSocketService();
              WebSocketService.connectToQueue(this.playerId, async (data) => {
                  console.log("Match found:", data);

                  if (String(data.playerId) === String(this.playerId)) {
                      console.log("playerId matched");
                      this.message = `Match found! Game ID: ${data.gameId}`;

                      if (!this.hasConfirmed) {
                          this.hasConfirmed = true;
                          await this.confirmJoinGame(data.gameId);
                          alert("Match found! Game gonna start!");
                          this.$router.push('/gameterrain');

                      }
                  } else {
                      console.warn("⚠️ playerId unmatch:", data.playerId, "front", this.playerId);
                  }
              });
          } catch (error) {
              console.error("❌ Error occurred while searching for a game:", error.response ? error.response.data : error.message);
              this.message = "❌ Matchmaking error.";
          }
      },

      async confirmJoinGame(gameId) {
          try {
              const url = "http://localhost:8080/api/game/confirm_join";
              const requestData = { gameId: gameId, playerId: this.playerId };

              console.log("📢 sending request:", requestData);

              await axios.post(url, requestData, {
                  headers: { "Content-Type": "application/json" }
              });

              console.log(`✅ Player ${this.playerId} confirmed game ${gameId}`);

              WebSocketService.subscribeToGame(gameId, (gameState) => {
                  console.log("🎮 Game state updated:", gameState);
                  this.message = `🃏 Game Started! Current state: ${JSON.stringify(gameState)}`;
              });

          } catch (error) {
              console.error("❌ Failed to confirm game join:", error.response ? error.response.data : error.message);
          }
      }
  }
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
