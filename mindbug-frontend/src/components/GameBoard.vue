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

    <div class="battlefield">
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

    <div class="hand-area">
      <img
        v-for="(card, index) in handCards"
        :key="index"
        :src="getCardImage(card)"
        class="card-image hand-card"
      />
    </div>
  </div>
</template>

<script>
export default {
  name: "GameBoard",
  data() {
    return {
      cardCount: 5,
      handCards: [
        "Bee_Bear.jpg",
        "Killer_Bee.jpg",
        "Gorillion.jpg",
        "Lone_Yeti.jpg",
        "Shark_Dog.jpg"
      ],
      myBattlefieldCards: ["Elephantopus.jpg", "Ferret_Bomber.jpg", "Giraffodile.jpg"],
      enemyBattlefieldCards: ["Deathweaver.jpg"]
    };
  },
  methods: {
    getCardImage(card) {
      return require(`@/assets/Sets/First_Contact/${card}`);
    },
    getCardBackImage() {
      return require(`@/assets/Sets/First_Contact/card_Back.png`);
    },
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

/* Applique les effets uniquement pour les cartes dans la hand-area */
.hand-area .hand-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
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

</style>
