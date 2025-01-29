<template>
  <div id="cards-list">
    <h1>Cards from set: {{ formatSetName(set) }}</h1>
    <div class="cards-container">
      <div v-for="card in filteredCards" :key="card.name" class="card">
        <img :src="`/img/${set}/${card.name}.jpg`" :alt="`Card ${set} ${this.set} ${card.name}`" class="card-image" />
        <div class="card-copies">X{{ card.copies }}</div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    set: {  
      type: String,
      required: true
    }
  },
  data() {
    return {
      cards: [
        { name: 'Axolotl_Healer', copies: 2, set: 'First_Contact' },
        { name: 'Bee_Bear', copies: 1, set: 'First_Contact' },
        { name: 'Brain_Fly', copies: 1, set: 'First_Contact' },
        { name: 'Chameleon_Sniper', copies: 1, set: 'First_Contact' },
        { name: 'Compost_Dragon', copies: 2, set: 'First_Contact' },
        { name: 'Deathweaver', copies: 1, set: 'First_Contact' },
        { name: 'Elephantopus', copies: 1, set: 'First_Contact' },
        { name: 'Explosive_Toad', copies: 2, set: 'First_Contact' },
        { name: 'Ferret_Bomber', copies: 2, set: 'First_Contact' },
        { name: 'Giraffodile', copies: 1, set: 'First_Contact' },
        { name: 'Goblin_Werewolf', copies: 2, set: 'First_Contact' },
        { name: 'Gorillion', copies: 1, set: 'First_Contact' },
        { name: 'Grave_Robber', copies: 2, set: 'First_Contact' },
        { name: 'Harpy_Mother', copies: 1, set: 'First_Contact' },
        { name: 'Kangasaurus_Rex', copies: 2, set: 'First_Contact' },
        { name: 'Killer_Bee', copies: 2, set: 'First_Contact' },
        { name: 'Lone_Yeti', copies: 1, set: 'First_Contact' },
        { name: 'Luchataur', copies: 2, set: 'First_Contact' },
        { name: 'Mysterious_Mermaid', copies: 1, set: 'First_Contact' },
        { name: 'Plated_Scorpion', copies: 2, set: 'First_Contact' },
        { name: 'Rhino_Turtle', copies: 2, set: 'First_Contact' },
        { name: 'Shark_Dog', copies: 1, set: 'First_Contact' },
        { name: 'Sharky_Crab-Dog-Mummypus', copies: 1, set: 'First_Contact' },
        { name: 'Shield_Bugs', copies: 2, set: 'First_Contact' },
        { name: 'Snail_Hydra', copies: 2, set: 'First_Contact' },
        { name: 'Snail_Thrower', copies: 1, set: 'First_Contact' },
        { name: 'Spider_Owl', copies: 2, set: 'First_Contact' },
        { name: 'Strange_Barrel', copies: 1, set: 'First_Contact' },
        { name: 'Tiger_Squirrel', copies: 2, set: 'First_Contact' },
        { name: 'Turbo_Bug', copies: 1, set: 'First_Contact' },
        { name: 'Tusked_Extorter', copies: 2, set: 'First_Contact' },
        { name: 'Urchin_Hurler', copies: 1, set: 'First_Contact' },
      ]
    };
  },
  computed: {
    filteredCards() {
      return this.cards.filter(card => card.set === this.set);
    }
  },
  methods: {
    formatSetName(setName) {
      return setName.replace(/_/g, ' ');
    }
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
