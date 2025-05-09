<template>
  <div class="game-board">

    <div class="playerInfo enemySide">
      <img class="playerAvatar" :src="getAvatar()" alt="Avatar">
      <div class="playerDetails">
        <div class="playerStats">
          <span class="lifePoint">❤️ {{ enemyHp }}</span>
          <span class="mbPoint">🧠 {{ enemyMindbug }}</span>
        </div>
        <p class="playerName">{{ enemyName }}</p>
      </div>
    </div>

    <div class="top-hand">
      <img v-for="(_, index) in enemyHandCard" :key="index" :src="getCardBackImage()" class="card-image hand-card" />
    </div>


    <div class="side-left">
      <div class="dps">
        <div class="pile-row">
          <div class="draw-pile">
            <img src="../assets/Sets/First_Contact/card_Back.png" />
            <span class="draw-count">×{{ enemyNumDP }}</span>
          </div>
          <div class="discard-pile">
            <button class="voir-button">Voir</button>
            <div class="count-label">{{ enemyDiscardPile }}</div>
          </div>
        </div>
      </div>
      <div class="dps">
        <div class="pile-row">
          <div class="draw-pile">
            <img src="../assets/Sets/First_Contact/card_Back.png" />
            <span class="draw-count">×{{ myNumDP }}</span>
          </div>
          <div class="discard-pile">
            <button class="voir-button">Voir</button>
            <div class="count-label">{{ myDiscardPile }}</div>
          </div>
        </div>
      </div>
    </div>



    <div class="battlefield" @click="handleBattlefieldClick" @dragover.prevent @drop="handleDropOnBattlefield">
      <div class="row">
        <img v-for="(card, index) in enemyBattlefieldCards.slice(0)" :key="index" :src="getCardImage(card)"
          class="card-image center first-card" />
      </div>

      <div class="divider"></div>

      <div class="row">
        <img v-for="(card, index) in myBattlefieldCards.slice(0)" :key="index" :src="getCardImage(card)"
          class="card-image" :class="{ 'battlefield-selected': selectedBattleFieldCard === index }"
          @click="selectAttacker(index)" />
        <div class="combat-controls" v-if="isMyTurn">
          <button v-if="canAttack && selectedAttacker" @click="handleAttackCardClick(); selectedAttacker = true"
            :disabled="!canAttack" class="combat-button attack" :class="{
              'selected': selectedAttacker,
              'attacker': selectedAttacker
            }">
            🗡️ Attack
          </button>
        </div>
      </div>


    </div>
    <div v-if="isMyTurn" class="turn-indicator">Your turn</div>
    <div v-else class="turn-indicator">Waiting for opponent...</div>

    <div class="hand-area">
      <img v-for="(handCards, index) in handCards" :key="index" :src="getCardImage(handCards)"
        class="card-image hand-card" :class="{ 'selected': selectedCard === index }" @click="handleCardClick(index)"
        draggable="true" @dragstart="handleDragStart($event, index)" />
    </div>


    <div class="playerInfo mySide">
      <img class="playerAvatar" :src="getAvatar()" alt="Avatar">
      <div class="playerDetails">
        <div class="playerStats">
          <span class="lifePoint">❤️ {{ myHp }}</span>
          <span class="mbPoint">🧠 {{ myMindbug }}</span>
        </div>
        <p class="playerName">{{ myName }}</p>
      </div>
    </div>

    <!-- Global modals for both players-->
    <div v-if="showAskBlockDialog" class="ask-block-modal-overlay">
      <div class="ask-block-modal">
        <p class="modal-message">You are attacked! Do you want to block?</p>
        <div class="modal-buttons">
          <button @click="handleBlockCardClick()" class="block-button">Block</button>
          <button @click="dontBlock()" class="dont-block-button">Don't Block</button>

        </div>
      </div>
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
      enemyHandCard: 0,
      handCards: [],
      playerId: null,
      gameId: null,

      selectedCard: null,
      selectedBattleFieldCard: null,

      draggingCard: null,

      isMyTurn: false,

      myHp: 0,
      myHandCards: [],
      myBattlefieldCards: [],
      myName: null,
      myMindbug: 0,
      myDrawPile: [],

      enemyHp: 0,
      enemyHandCount: [],
      enemyBattlefieldCards: [],
      enemyName: null,
      enemyMindbug: 0,
      enemyDrawPile: [],

      myNumDP: 0,
      enemyNumDP: 0,

      myDiscardPile: 0,
      enemyDiscardPile: 0,

      canAttack: false,

      indexTarget: 0,

      blockIndex: 0,
      askBlockDialogVisible: false,
      currentAskBlockData: null,
      askBlockToPlayer: null,

      blocking: false,
    };
  },

  computed: {
    showAskBlockDialog() {
      return this.askBlockDialogVisible && this.playerId == this.askBlockToPlayer;
    }
  },

  mounted() {
    this.gameId = this.$route.params.gameId;
    this.playerId = this.$route.params.playerId;

    // Map websocket handlers
    WebSocketService.onGameStateReceived = this.onGameStateReceived.bind(this);
    WebSocketService.onTurnChanged = this.onTurnChanged.bind(this);
    WebSocketService.onAttacked = this.onAttacked.bind(this);
    WebSocketService.onAskBlock = this.onAskblock.bind(this);

    WebSocketService.subscribeToGameState(this.gameId);
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

    },

    onGameStateReceived(gameState) {

      const isPlayer1 = String(gameState.player1.id) === this.playerId;

      if (isPlayer1) {
        this.myHp = gameState.player1.lifepoints;
        this.myHandCards = gameState.player1.hand || [];
        this.myBattlefieldCards = gameState.player1.battlefield || [];
        this.myName = gameState.player1.nickname;
        this.myMindbug = gameState.player1.mindbug;
        this.myDrawPile = gameState.player1.drawPile;

        this.enemyHp = gameState.player2.lifepoints;
        this.enemyHandCount = gameState.player2.handCardsCount || 0;
        this.enemyBattlefieldCards = gameState.player2.battlefield || [];
        this.enemyName = gameState.player2.nickname;
        this.enemyMindbug = gameState.player2.mindbug;
        this.enemyDrawPile = gameState.player2.drawPile;

        this.myNumDP = gameState.player1.drawPile.length;
        this.enemyNumDP = gameState.player2.drawPile.length;
        this.myDiscardPile = gameState.player1.discardPile.length;
        this.enemyDiscardPile = gameState.player2.discardPile.length;

        this.enemyHandCard = gameState.player2.hand.length;

      } else {
        this.myHp = gameState.player2.lifepoints;
        this.myHandCards = gameState.player2.hand || [];
        this.myBattlefieldCards = gameState.player2.battlefield || [];
        this.myName = gameState.player2.nickname;
        this.myMindbug = gameState.player2.mindbug;
        this.myDrawPile = gameState.player2.drawPile;

        this.enemyHp = gameState.player1.lifepoints;
        this.enemyHandCount = gameState.player1.handCardsCount || 0;
        this.enemyBattlefieldCards = gameState.player1.battlefield || [];
        this.enemyName = gameState.player1.nickname;
        this.enemyMindbug = gameState.player1.mindbug;
        this.enemyDrawPile = gameState.player1.drawPile;

        this.myNumDP = gameState.player2.drawPile.length;
        this.enemyNumDP = gameState.player1.drawPile.length;
        this.myDiscardPile = gameState.player2.discardPile.length;
        this.enemyDiscardPile = gameState.player1.discardPile.length;

        this.enemyHandCard = gameState.player1.hand.length;
      }
      this.handCards = this.myHandCards.map(handCard => {
        console.log("handCard:", handCard);
        console.log("handCard.card:", handCard.card.name);
        return {
          ...handCard.card,
          name: handCard.card.name,
          sessioncardId: handCard.id
        };
      });
      if (this.isBlocking) {
        this.attackerCard = gameState.attackingCard;
      }


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

    updateActionButtons() {
      this.attackButtonDisabled = !this.isMyTurn;
      this.playCardDisabled = !this.isMyTurn;
    },

    getCardImage(card) {
      // Access for the proxies
      const cardName = card?.name || card?.card?.name;

      if (cardName) {
        return require(`@/assets/Sets/First_Contact/${cardName}.jpg`);
      }

      console.error('Card name not found in:', card);
      return '';
    },

    getCardBackImage() {
      return require(`@/assets/Sets/First_Contact/card_Back.png`);
    },
    getAvatar(playerName) {
      try {
        return require(`@/assets/avatars/${playerName}.jpg`);
      } catch (e) {
        console.error(`Avatar not found for ${playerName}, using default.`);
        return require("@/assets/avatars/default.jpg");
      }
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
        this.updateBattlefield(cardToPlay);
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

      if (!this.isMyTurn) {
        alert("Ce n'est pas votre tour !");
        return;
      }

      if (this.draggingCard !== null) {

        const cardToPlay = this.handCards[this.draggingCard];
        this.playCard(cardToPlay);
        this.updateBattlefield(cardToPlay);
        this.draggingCard = null;
      }
    },

    playCard(card) {
      if (!this.isMyTurn) {
        alert("Ce n'est pas votre tour, vous ne pouvez pas jouer de carte.");
        return;
      }
      fetch('http://localhost:8080/api/game/play_card', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          playerId: this.playerId,
          sessioncardId: card.sessioncardId,
          gameId: this.gameId
        })
      })
        .then(response => {
          if (!response.ok) {
            return response.text().then(text => {
              throw new Error("Erreur: " + text);
            });
          }
        })
        .catch(error => {
          console.error("Erreur réseau ou backend :", error);
          alert(error.message);
        });
    },

    onCardDrawed(cardData) {
      console.log("onCardDrawed called with:", cardData);

      if (!this.handCards.some(card => card.sessioncardId === cardData.sessioncardId)) {
        this.handCards.push(cardData);
        console.log("Card added to handCards:", cardData);
      }
    },
    updateBattlefield(card) {

      const index = this.handCards.findIndex(c => c.sessioncardId == card.sessioncardId);
      if (index !== -1) {

        this.handCards.splice(index, 1);

        this.myBattlefieldCards.push(card);
      }
    },

    selectAttacker(cardIndex) {

      if (this.selectedBattleFieldCard === cardIndex) {
        this.selectedBattleFieldCard = null;
      } else {
        this.selectedBattleFieldCard = cardIndex;
      }


      if (this.blocking) {
        const blockingCard = this.myBattlefieldCards[this.selectedBattleFieldCard];
        this.block(blockingCard)
        return
      }



      console.log("CARDINDEX:", this.selectedBattleFieldCard);
      this.selectedAttacker = true;
      this.canAttack = true;
    },

    selectBlockCard(cardIndex) {
      if (!this.isMyTurn) return;
      this.blockIndex = cardIndex;
      console.log("Block CARDINDEX:", this.blockIndex);
    },


    attack(card) {
      if (!this.isMyTurn) {
        alert("Ce n'est pas votre tour, vous ne pouvez pas jouer de carte.");
        return;
      }
      fetch('http://localhost:8080/api/game/attack', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          playerId: this.playerId,
          gameId: this.gameId,
          sessioncardId: card.id
        })
      });
    },

    block(card) {
      fetch('http://localhost:8080/api/game/block', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          playerId: this.playerId,
          sessioncardId: card.id,
          gameId: this.gameId
        })
      });
      this.blocking = false;
      this.resetCombatState();
      this.resetAskBlockDialog();
    },

    onAttacked(data) {
      console.log("Youre attacked", data);
      this.currentAttackInfo = data;
      this.showAttackedPopup = true;
    },

    onAskblock(data) {
      this.askBlockDialogVisible = true
      this.askBlockToPlayer = data.data.playerId;
      this.currentAskBlockData = data;
    },

    dontBlock() {
      fetch('http://localhost:8080/api/game/dont_block', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          playerId: this.playerId,
          gameId: this.gameId
        })
      });
      this.resetAskBlockDialog();
    },

    handleAttackCardClick() {
      this.selectedAttacker = true;
      if (this.selectAttacker) {
        const selectedActionCard = this.myBattlefieldCards[this.selectedBattleFieldCard];



        this.attack(selectedActionCard);
        this.canAttack = true;

      } else {
        alert("Cannot attack with this card");
      }

      this.resetCombatState();
    },

    handleBlockCardClick() {
      // this.selectedAttacker = true;
      // if (this.selectAttacker) {
      //   const cardToAttack = this.myBattlefieldCards[this.selectedBattleFieldCard];
      //   this.block(cardToAttack);
      //   this.canAttack = true;

      // } else {
      //   alert("Cannot block with this card");
      // }
      this.blocking = true;
      this.askBlockDialogVisible = false;

    },

    resetCombatState() {
      this.selectedAttacker = false;
      this.selectedBattleFieldCard = -1;
      this.canAttack = false;
      this.indexTarget = -1;
    },

    resetAskBlockDialog() {
      this.askBlockDialogVisible = false;
      this.askBlockToPlayer = null;
      this.currentAskBlockData = null
    },

    resetBatllefieldSelected() {
      this.selectedBattleFieldCard = null;
    }
  },
};
</script>

<style scoped>
html,
body {
  overflow: hidden;
  margin: 0;
  padding: 0;
  height: 100%;
  width: 100%;
  position: fixed;
}

.game-board {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  width: 100%;
  font-size: 2rem;
  color: black;
  background-color: #f5f5fa;
  padding: 10px;
  position: fixed;
  top: 0;
  left: 0;
  overflow: hidden;
}


.top-hand,
.hand-area {
  overflow-y: auto;
  min-height: 150px;
  min-width: 100px;
  max-height: 200px;
  display: flex;
  justify-content: center;
  gap: 1.5vw;
  padding: 10px;
  margin-bottom: 10px;
}

.hand-area {
  overflow-y: auto;
  max-height: 200px;
  background-color: #e0e0e0;
  border-radius: 10px;
  margin-top: 10px;
}


.hand-area .hand-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.hand-card.selected {
  outline: 4px solid yellow;
  transform: translateY(-10px);
}

.battlefield-selected {
  outline: 4px solid yellow;
  transform: translateY(-10px);
}

.side-left {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  left: 15px;
}


.battlefield {
  min-height: 300px;
  min-width: 600px;
  background-color: #e0e0e0;
  padding: 10px;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 5px;
  position: relative;
}

.divider {
  width: 30%;
  height: 1px;
  background-color: #000;
  margin: 10px 0;
}

.first-card {
  margin-bottom: 50px;
}

.row {
  display: flex;
  gap: 5px;
}

.discard-pile {
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


.card-image {
  width: 10vw;
  height: auto;
  max-width: 150px;
  object-fit: cover;
  border: 2px solid black;
  border-radius: 10px;
  box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.2);
}

.playerInfo {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.8);
  padding: 10px;
  border-radius: 10px;
  box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.2);
}

.mySide {
  position: absolute;
  bottom: 20px;
  left: 20px;
}

.enemySide {
  position: absolute;
  top: 20px;
  left: 20px;
}

.playerAvatar {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  border: 2px solid black;
  margin-right: 10px;
}

.playerDetails {
  display: flex;
  flex-direction: column;
}

.playerName {
  width: 150px;
  height: 35px;
  text-align: left;
  line-height: 30px;
  font-size: 26px;
  font-weight: bold;
  border: 2px solid black;
  border-radius: 5px;
  background-color: white;
}

.playerStats {
  display: flex;
  flex-direction: column;
  gap: 5px;
  align-items: left;
  transform: translateY(15px);
}


.lifePoint {
  color: red;
  font-weight: bold;
}

.mbPoint {
  color: black;
  font-weight: bold;
}

.card-image:active {
  opacity: 0.5;
  cursor: move;
}


.dps {
  display: flex;
  justify-content: center;
}

.pile-row {
  display: flex;
  flex-direction: row;
  gap: 80px;
  align-items: center;
}

.draw-pile {
  display: flex;
  align-items: center;
  gap: 20px;
}

.draw-pile img {
  width: 100px;
  height: 150px;
  border-radius: 8px;
  box-shadow: 0 0 6px rgba(0, 0, 0, 0.3);
}

.draw-count {
  font-size: 40px;
  font-weight: bold;
  color: #333;
}

.count-label {
  position: absolute;
  top: 4px;
  right: 4px;
  background: black;
  color: white;
  font-size: 14px;
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: bold;
}

.combat-controls {
  position: absolute;
  bottom: -60px;
  display: flex;
  gap: 20px;
  z-index: 100;
}

.combat-button {
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
  transform: translateX(50);
}

.attack {
  background: #ff4444;
  border: 2px solid #cc0000;
  color: white;
  transform: translateX(50px);
}

.attack:disabled {
  background: #ff9999;
  cursor: not-allowed;
}

.targetable {
  border: 3px solid #ff4444 !important;
  cursor: crosshair;
  transform: translateY(10px);
}

.attacker {
  border: 3px solid gold;
  box-shadow: 0 0 15px rgba(255, 215, 0, 0.5);
  transform: translateY(-10px);
}

.ask-block-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.ask-block-modal {
  background: #1e1e1e;
  color: #fff;
  padding: 20px 30px;
  border-radius: 10px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.3);
  width: 300px;
  text-align: center;
  font-family: 'Roboto', sans-serif;
}

.modal-message {
  font-size: 18px;
  margin-bottom: 20px;
  font-weight: bold;
}

.modal-buttons {
  display: flex;
  justify-content: space-around;
  gap: 10px;
}

button {
  padding: 10px 20px;
  font-size: 16px;
  border: none;
  cursor: pointer;
  border-radius: 5px;
  transition: all 0.3s ease;
}

.block-button {
  background-color: #4CAF50;
  color: white;
}

.dont-block-button {
  background-color: #F44336;
  color: white;
}

button:hover {
  opacity: 0.8;
}

button:active {
  transform: scale(0.98);
}

.selected-card {
  border: 2px solid #00f;
  transform: scale(1.05);
  box-shadow: 0 0 10px rgba(0, 0, 255, 0.5);
}
</style>
