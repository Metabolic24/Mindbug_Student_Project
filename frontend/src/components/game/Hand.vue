<script setup lang="ts">
import {getCardAlt, getCardImage} from "@/shared/CardUtils";

// Declare the interface for the data given by the parent component
interface Props {
  cards: CardInterface[]
  opponent: boolean
  selectedCard?: CardInterface
}
const props = defineProps<Props>()

// Declare events emitted by this component
const emit = defineEmits(['card-selected'])

// Get the CSS classes for the given card
function getCardClasses(card: CardInterface): Record<string, boolean> {
  return ({
    'top-card': props.opponent,
    'bottom-card': !props.opponent,
    'selected': card.uuid === props.selectedCard?.uuid,
  })
}
</script>

<template>
  <div class="hand">
    <img v-for="card in cards" :src="getCardImage(card)" :alt="getCardAlt(card)" class="card-image"
         :class="getCardClasses(card)" @click="emit('card-selected', card)" />
  </div>
</template>

<style scoped>
.hand {
  height: 100%;
  width: 100%;

  display: flex;
  justify-content: center;
  gap: 5px;
}

.card-image {
  width: 3vw;
  height: auto;
  object-fit: cover;

  border-radius: 10px;
  box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.2);
}

.top-card:hover {
  transform: translateY(10px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.top-card.selected {
  outline: 4px solid red;
  transform: translateY(10px);
}

.bottom-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.bottom-card.selected {
  outline: 4px solid red;
  transform: translateY(-10px);
}
</style>
