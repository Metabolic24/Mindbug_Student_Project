<template>
  <nav>
    <router-link to="/">Home</router-link>
    <router-link to="/sets">Sets</router-link>
  </nav>
  <div id="card-set-details">
    <h1>Cards from set: {{ formatSetName(set) }}</h1>
    <div class="cards-container">
      <img v-for="cardId in cards" :key="cardId" :src="getCardImage(cardId)" alt="Card not found" class="card-image"/>
    </div>
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref, Ref} from "vue";
import {getCardSetDetails} from "@/shared/RestService";

// Declare the interface for the data given by the parent component
interface Props {
  set: String
}
const props = defineProps<Props>()

// Array that will contain all the IDs of the current set cards
let cards: Ref<string[]> = ref([])

onMounted(async () => {
  // Get the list of card IDs from the server
  cards.value = await getCardSetDetails(props.set)
})

// Get the card image from its ID
function getCardImage(cardId: string) {
  const url = new URL("@/assets/cards/", import.meta.url)
  return `${url}/${props.set}/${cardId}.jpg`
}

// Format correctly the set name
function formatSetName(setName: String) {
  return setName.replace(/_/g, ' ');
}
</script>

<style scoped>
#card-set-details {
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

.card-image {
  position: relative;
  width: 250px;
  height: 350px;
  margin: 15px;

  border-radius: 12px;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.3);
}
</style>
