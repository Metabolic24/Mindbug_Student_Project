<script setup lang="ts">
import {onMounted, ref, Ref} from "vue";
import {getCardSetDetails} from "@/shared/RestService";
import {getCardImage} from "@/shared/CardUtils";

// Declare the interface for the data given by the parent component
interface Props {
  set: String,
  custom: boolean
}
const props = defineProps<Props>()

// Array that will contain all the IDs of the current set cards
let cards: Ref<string[]> = ref([])

onMounted(async () => {
  // Get the list of card IDs from the server
  cards.value = await getCardSetDetails(props.set)
})

// Format correctly the set name
function formatSetName(setName: String) {
  return setName.replace(/_/g, ' ');
}
</script>

<template>
  <nav @contextmenu.prevent>
    <router-link to="/">Home</router-link>
    <router-link to="/sets">Sets</router-link>
  </nav>
  <div id="cards-set" @contextmenu.prevent>
    <h1>Cards from set: {{ formatSetName(set) }}</h1>
    <div id="cards-container">
      <img v-for="cardId in cards" :key="cardId" :src="getCardImage(+cardId)" alt="Card not found" class="card-image" draggable="false">/>
    </div>
  </div>
</template>

<style scoped>
#cards-set {
  text-align: center;
}

#cards-container {
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
