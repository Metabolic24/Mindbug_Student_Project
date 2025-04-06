<template>
  <div id="cards-list">
    <h1>Cards from set: {{ formatSetName(this.set) }}</h1>
    <div class="cards-container">
      <div v-for="card in cards" :key="card.name" class="card">
        <img :src="require(`@/assets/Sets/${getImage(card.name)}`)" 
             :alt="`Card ${card.name}`" class="card-image" />
      </div>
    </div>
  </div>

</template>

<script>
import axios from 'axios';
export default {
  props: {
    set: {  
      type: String,
      required: true
    }
  },
  data() {
    return {
      cards: []
    };
  },
  computed: {
    filteredCards() {
      return this.cards.filter(card => card.set === this.set);
    }
  },
  methods: {
    getImage(name) {
        return `${this.set}/${name}.jpg`
    },
    formatSetName(setName) {
      return setName.replace(/_/g, ' ');
    },
    async fetchCards() {
      try {
        const response = await axios.get(`/api/cards/${this.set}`);
        this.cards = response.data;
      } catch (error) {
        console.error("Error fetching cards:", error);
      }
    }
  },
  mounted() {
    this.fetchCards();
  }

}
</script>

<style scoped>
  #cards-list{
    text-align: center;
  }
  .cards-container {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-around;
    padding: 20px;
    background-color: #f4f7fb;
    border-radius: 12px; 
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }

  .card {
    position: relative;
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
    border-radius: 12px;
    box-shadow: 0px 6px 12px rgba(0, 0, 0, 0.3);
  }

  .card-copies {
    position: absolute;
    top: 2px;
    right: -10px;
    color: white;
    padding: 5px 10px;
    font-size: 24px;
    font-weight: bold;
    -webkit-text-stroke: 0.7px black;
  }
</style>
