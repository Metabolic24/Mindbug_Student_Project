<template>
    <div class="game-board">
      <div class="top-hand">
        <img v-for="(_, index) in 5" :key="index" :src="getCardBackImage()" class="card-image" />
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
        <img :src="getCardImage(battlefieldCards[0])" class="card-image center first-card" />
        <div class="row">
          <img v-for="(card, index) in battlefieldCards.slice(1)" :key="index" :src="getCardImage(card)" class="card-image" />
        </div>
      </div>
      
      <div class="side-right">
        <img v-for="(_, index) in 2" :key="index" :src="getCardBackImage()" class="card-image" />
      </div>
      
      <div class="hand-area">
        <img v-for="(card, index) in handCards" :key="index" :src="getCardImage(card)" class="card-image" />
      </div>
    </div>
  </template>
  
  <script>
  export default {
  name: "GameBoard",
  data() {
    return {
      handCards: [
        "Bee_Bear.jpg",
        "Killer_Bee.jpg",
        "Gorillion.jpg",
        "Lone_Yeti.jpg",
        "Shark_Dog.jpg"
      ],
      battlefieldCards: [
        "Deathweaver.jpg",
        "Elephantopus.jpg",
        "Ferret_Bomber.jpg",
        "Giraffodile.jpg"
      ],
      cardBackImage: "card_Back.png" 
    };
  },
  methods: {
    getCardImage(card) {
      return `/assets/${card}`;
    },
    getCardBackImage() {
      return `/assets/${this.cardBackImage}`;
    }
  },
  created() {
    console.log("GameBoard Component Loaded");
    this.handCards.forEach(card => {
      console.log(`Image path for ${card}:`, this.getCardImage(card));
    });
  }

};
  </script>
  
  <style scoped>
  html, body {
    overflow: hidden;
    height: 100vh;
    width: 100vw;
    margin: 0;
    padding: 0;
    position: fixed;
  }
  
  .game-board {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: space-between;
    height: 100vh;
    width: 100vw;
    font-size: 2rem;
    color: black;
    background-color: #f5f5fa;
    padding: 10px;
    position: fixed;
    top: 0;
    left: 0;
    overflow: hidden;
  }
  
  .top-hand {
    display: flex;
    justify-content: center;
    gap: 5px;
  }
  
  .hand-area {
    display: flex;
    justify-content: center;
    gap: 10px;
    margin-bottom: 10px;
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
  
  .side-right {
    right: 40px;
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
  
  .mindbug {
    color: blue;
  }
  
  .count {
    font-size: 0.8rem;
    color: orange;
  }
  
  .bottom-right {
    position: absolute;
    bottom: 5px;
    right: 5px;
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

  