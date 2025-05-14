<script setup lang="ts">
import {getCardAlt, getCardImage} from "@/shared/CardUtils";

interface Props {
  cards: CardInterface[]
  owner: Owner
  selectedCard?: CardInterface
  playerTurn: boolean
}

const props = defineProps<Props>()

const emit = defineEmits(['card-selected'])

function getCardClasses(card: CardInterface): Record<string, boolean> {
  return ({
    'top-card': props.owner === "Opponent",
    'bottom-card': props.owner === "Player",
    'selected': card.uuid === props.selectedCard?.uuid,
  })
}

function onCardSelected(card: CardInterface): void {
  if (props.playerTurn) {
    emit('card-selected', card)
  }
}
</script>

<template>
  <div class="hand">
    <img v-for="card in cards" :src="getCardImage(card)" :alt="getCardAlt(card)" class="card-image"
         :class="getCardClasses(card)" @click="onCardSelected(card)" />
  </div>
</template>

<style scoped>
.hand {
  height: 100%;
  width: 100%;

  display: flex;
  justify-content: center;
  gap: 5px;

  background-color: green;
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
