<template>
    <div class="game-board">
      <div class="top-hand">
        <div v-for="(_, index) in 5" :key="index" class="card">Mindbug</div>
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
        <div class="card center first-card"></div>
        <div class="row">
          <div v-for="(_, index) in 3" :key="index" class="card"></div>
        </div>
      </div>
      
      <div class="side-right">
        <div class="card mindbug">Mindbug <span class="count bottom-right">4</span></div>
        <div class="card mindbug">Mindbug <span class="count bottom-right">4</span></div>
      </div>
      
      <div class="hand-area">
        <div class="card">Dragon Infernal</div>
        <div class="card">Golem de Pierre</div>
        <div class="card">Fée Magique</div>
        <div class="card">Fée Magique</div>
        <div class="card">Fée Magique</div>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: "GameBoard",
    created() {
      console.log("GameBoard Component Loaded");
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
  </style>
  