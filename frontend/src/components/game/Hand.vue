<script setup lang="ts">
import Card from "@/components/game/Card.vue";
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

</script>

<template>
  <div class="hand">
    <Card
      v-for="card in cards"
      :key="card.uuid"
      :card="card"
      :context="opponent ? 'opponent-hand' : 'player-hand'"
      :selected="card.uuid === selectedCard?.uuid"
      :clickable="!opponent"
      @click="emit('card-selected', card)"
    />

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

.bottom-card {
  transform: translateY(+30%);
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}

.bottom-card:hover {
  transform: translateY(-120px) scale(2);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  z-index: 5;
}

.bottom-card.selected {
  transform: translateY(-140px) scale(2.1);
  box-shadow: 0 14px 28px rgba(0, 0, 0, 0.35);
  z-index: 10;
}

</style>
