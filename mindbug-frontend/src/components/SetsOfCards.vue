<template>
  <div id="sets-of-cards">
    <h1>Select a set of cards</h1>
    <div class="sets-container">
      <div v-for="set in sets" :key="set.name" class="set-item">
        <router-link :to="`/cardlist/${set}`">
          <div class="set-card">
            <img :src="`/img/sets/${set}.png`" :alt="set" />
          </div>
        </router-link>
      </div>
    </div>
  </div>
  </template>
  
  <script>
  import axios from 'axios';
  
  export default {
    data() {
      return {
        sets: [] 
      };
    },
    methods: {
      async fetchSets() {
        try {
          const response = await axios.get('/api/cards/sets'); 
          this.sets = response.data;
        } catch (error) {
          console.error("Erreur lors de la récupération des sets :", error);
        }
      }
    },
    mounted() {
      this.fetchSets(); 
    }
  }
  </script>
  
<style scoped>

  #sets-of-cards {
    text-align: center;
  }

  .sets-container {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
    padding: 20px;
    background-color: #f4f7fb;
    border-radius: 12px; 
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); 
  }

  h2 {
    font-size: 24px;
    color: #2c3e50; 
    margin-bottom: 20px;
    text-align: center;
  }

  .set-item {
    cursor: pointer;
    transition: transform 0.3s ease, box-shadow 0.3s ease; 
  }

  .set-item:hover {
    transform: scale(1.05); 
    box-shadow: 0 6px 12px rgba(0, 0, 0, 0.2); 
  }

  .set-card {
    width: 180px;
    height: 180px;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: white; 
    border-radius: 12px; 
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    overflow: hidden;
    transition: box-shadow 0.3s ease;
  }

  .set-card img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 12px; 
  }
</style>
  