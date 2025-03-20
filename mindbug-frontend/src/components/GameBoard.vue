<template>
  <div class="game-board">
    <div class="top-hand">
      <img
        v-for="(_, index) in cardCount"
        :key="index"
        :src="getCardBackImage()"
        class="card-image hand-card"
      />
    </div>

    <div class="side-left">
      <div class="card voir-container">
        <button class="voir-button">Voir</button>
        <span class="count">4</span>
      </div>
      <div class="card voir-container">
        <button class="voir-button">Voir</button>
        <span class="count">2</span>
      </div>
    </div>

    <div class="battlefield"
    @click="handleBattlefieldClick"
    @dragover.prevent
    @drop="handleDropOnBattlefield">
      <img
        v-for="(card, index) in enemyBattlefieldCards.slice(0)"
        :key="index"
        :src="getCardImage(card)"
        class="card-image center first-card"
      />
      <div class="row">
        <img
          v-for="(card, index) in myBattlefieldCards.slice(0)"
          :key="index"
          :src="getCardImage(card)"
          class="card-image"
        />
      </div>
    </div>
    <div v-if="isMyTurn" class="turn-indicator">Your turn</div>
     <div v-else class="turn-indicator">Waiting for opponent...</div>
     <button @click="endTurn" :disabled="endTurnButtonDisabled">
       End Turn
     </button>

    <div class="hand-area">
      <img
        v-for="(handCards, index) in handCards"
        :key="index"
        :src="getCardImage(handCards)"
        class="card-image hand-card"
        :class="{ 'selected': selectedCard === index }"
        @click="handleCardClick(index)"
        draggable="true"
        @dragstart="handleDragStart($event, index)"  
      />
    </div>
  </div>
</template>

<script>
import WebSocketService from "@/services/websocket.js";
import axios from 'axios';
export default {
  name: "GameBoard",
  data() {
    return {
      cardCount: 5,
      handCards: [
        "Bee_Bear.jpg", "Killer_Bee.jpg", "Gorillion.jpg", "Lone_Yeti.jpg", "Shark_Dog.jpg"
      ],
      playerId: null,
      gameId: null,
      myBattlefieldCards: ["Elephantopus.jpg", "Ferret_Bomber.jpg", "Giraffodile.jpg"],
      enemyBattlefieldCards: ["Deathweaver.jpg"],
      selectedCard: null, 
      draggingCard: null, 

      isMyTurn: false,
    };
  },
  mounted() {
    this.gameId = this.$route.params.gameId;
     this.playerId = this.$route.params.playerId;
 
     WebSocketService.subscribeToGameState(
     this.gameId,
     this.onGameStateReceived.bind(this),
     this.onTurnChanged.bind(this)
    );
     this.confirmJoinGame();
  },
  methods: {
    async confirmJoinGame() {
       try {
         const payload = {
           gameId: this.gameId,
           playerId: this.playerId
         };
 
         await axios.post('http://localhost:8080/api/game/confirm_join', payload);
 
       } catch (error) {
         console.error('❌ confirmJoin failed', error);
       }
     },
     subscribeGameState() {
       if (!this.gameId || !this.playerId) {
         console.error("gameId or playerId not exist, cannot subscribe");
         return;
       }
       console.log(`📡gameState: /topic/game/${this.gameId}`);
 
       WebSocketService.subscribeToGameState(this.gameId, this.onGameStateReceived);
     },
 
     onGameStateReceived(gameState) {
       const isPlayer1 = gameState.player1.id === this.playerId;
 
       if (isPlayer1) {
         this.myHp = gameState.player1.lifepoints;
         this.myHandCards = gameState.player1.handCards || [];
         this.myBattlefieldCards = gameState.player1.battlefield || [];
 
         this.enemyHp = gameState.player2.lifepoints;
         this.enemyHandCount = gameState.player2.handCardsCount || 0;
         this.enemyBattlefieldCards = gameState.player2.battlefield || [];
       } else {
         this.myHp = gameState.player2.lifepoints;
         this.myHandCards = gameState.player2.handCards || [];
         this.myBattlefieldCards = gameState.player2.battlefield || [];
 
         this.enemyHp = gameState.player1.lifepoints;
         this.enemyHandCount = gameState.player1.handCardsCount || 0;
         this.enemyBattlefieldCards = gameState.player1.battlefield || [];
       }
 
       console.log(`🕒 Current turn: ${this.isMyTurn ? 'My turn' : 'opponent turn'}`);
      },
    
      onTurnChanged(message) {
       const currentPlayerId = message.data.currentPlayer;
       const gameState = message.data.gameState;
       if (String(currentPlayerId) === String(this.playerId)) {
         this.isMyTurn = true;
       } else {
         this.isMyTurn = false;
       }
 
       this.updateActionButtons();
       if (gameState) {
         this.onGameStateReceived(gameState);
       }
     },
 
     endTurn() {
       WebSocketService.sendAction('END_TURN', {
         gameId: this.gameId,
         playerId: this.playerId
       });
     },
 
     updateActionButtons() {
       this.endTurnButtonDisabled = !this.isMyTurn;
       this.attackButtonDisabled = !this.isMyTurn;
       this.playCardDisabled = !this.isMyTurn;
     },

    getCardImage(card) {
      return require(`@/assets/Sets/First_Contact/${card}`);
    },
    getCardBackImage() {
      return require(`@/assets/Sets/First_Contact/card_Back.png`);
    },

    handleCardClick(index) {
      if (this.selectedCard === index) {
        this.selectedCard = null;
      } else {
        this.selectedCard = index; 
      }
    },

    handleBattlefieldClick() {
      if (this.selectedCard !== null) {

        const cardToPlay = this.handCards[this.selectedCard];

        this.playCard(cardToPlay);

        this.handCards.splice(this.selectedCard, 1);

        this.myBattlefieldCards.push(cardToPlay);

        this.selectedCard = null; 
      }
    },

    handleDragStart(event, index) {
      this.draggingCard = index;

      event.dataTransfer.effectAllowed = "move";  
      
      const cardImage = event.target;

      event.dataTransfer.setDragImage(cardImage, 50, 50);  
    },

    handleDropOnBattlefield(event) {
      event.preventDefault();

      if (this.draggingCard !== null) {

        const cardToPlay = this.handCards[this.draggingCard];

        this.playCard(cardToPlay);

        this.handCards.splice(this.draggingCard, 1);

        this.myBattlefieldCards.push(cardToPlay); 

        this.draggingCard = null;
      }
    },
    
    playCard(card) {      
      fetch('/api/game/game/play_card', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          playerId: this.playerId,
          sessioncardId: card.sessioncardId,
          gameId: this.gameId
        })
      })
      .then(response => response.json())
      .then(data => {
        if (data.success) {
          //this.updateBattlefield(card); TODO : when api will work we will update the field here after api answer
          // for now the update logic is in handleBattlefieldClick and handleDropOnBattlefield
        } else {
          console.error("Erreur lors de la tentative de jouer la carte :", data.error);
        }
      })
      .catch(error => {
        console.error("Erreur réseau ou backend :", error);
      });
    }
  },
};
</script>

<style scoped>
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  width: 100%;
}

.game-board {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  height: 100%;
  width: 100%;
  font-size: 2rem;
  color: black;
  background-color: #f5f5fa;
  padding: 10px;
  overflow-y: auto;
  position: relative;
}

.top-hand,
.hand-area {
  display: flex;
  justify-content: center;
  gap: 1.5vw;
  padding: 10px;
  margin-bottom: 10px;
}

.hand-area {
  overflow-y: auto; 
  max-height: 200px; 
}

.hand-card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  width: 8vw;
  max-width: 120px;
  height: auto;
  border-radius: 12px;
  object-fit: cover;
}

.hand-area .hand-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.hand-card.selected {
  outline: 4px solid yellow; 
  transform: translateY(-10px);
}

.side-left, .side-right {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.side-left {
  left: 15px;
}

.battlefield {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 5px;
}

.first-card {
  margin-bottom: 50px;
}

.row {
  display: flex;
  gap: 5px;
}

.card {
  width: 100px;
  height: 150px;
  background-color: white;
  border: 2px solid black;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  font-size: 1rem;
  padding: 5px;
  border-radius: 10px;
  box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.2);
  position: relative;
}

.voir-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: white;
  border: 2px solid black;
  padding: 10px;
  position: relative;
}

.voir-button {
  background-color: lightgray;
  border: none;
  padding: 5px 10px;
  font-size: 1rem;
  cursor: pointer;
  border-radius: 5px;
  text-align: center;
}

.voir-button:hover {
  background-color: darkgray;
}

.count {
  font-size: 0.8rem;
  color: orange;
}

.card-image {
  width: 100px;
  height: 150px;
  object-fit: cover;
  border: 2px solid black;
  border-radius: 10px;
  box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.2);
}

.card-image:active {
  opacity: 0.5;  
  cursor: move;  
}

</style>
