<template>
    <div class="cards-container">
      <div v-for="card in cards" :key="card.name" class="card">
        <img :src="`img/First_contact/${card.name}.jpg`" :alt="`Card ${card.name}`" class="card-image" />
      </div>
    </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        cards: [
          { name: 'Axolotl_Healer' },
          { name: 'Bee_Bear' },
          { name: 'Brain_Fly' }
        ]
      };
    },
    methods: {
    }
  }
  </script>
  
  <style scoped>
  .cards-container {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-around;
    padding: 20px;
  }
  
  .card {
  margin: 15px;
  width: 250px; 
  height: 350px; 
  display: flex;
  justify-content: center; 
  align-items: center; 
}

.card-image {
  width: 90%;  
  height: 90%; 
  object-fit: cover; 
  border-radius: 15px; 
  box-shadow: 0px 6px 12px rgba(0, 0, 0, 0.3); 
}
  </style>
  