<script setup lang="ts">
import Card from "@/components/game/Card.vue";

// Declare the interface for the data given by the parent component
interface Props {
  cards: CardInterface[]
  opponent: boolean
  selectedCard?: CardInterface
}
defineProps<Props>()

// Declare events emitted by this component
const emit = defineEmits(['card-selected', 'card-preview'])

</script>

<template>
  <div class="hand" :class="{ 'hand--top': opponent, 'hand--bottom': !opponent }">
    <card
      v-for="card in cards"
      :key="card.uuid"
      :card="card"
      :context="opponent ? 'opponent-hand' : 'player-hand'"
      :selected="card.uuid === selectedCard?.uuid"
      :clickable="!opponent"
      @click="emit('card-selected', card)"
      @preview="emit('card-preview', card)"
    />
  </div>
</template>

<style scoped>
.hand {
  position: relative;
  width: max-content;
  max-width: 100%;
  margin: 0 auto;

  display: flex;
  justify-content: center;
  gap: 5px;
  pointer-events: none;
}

.hand > * {
  pointer-events: auto;
}

.hand--top {
  transform: translateY(6px);
}

.hand--bottom {
  transform: translateY(-6px);
}

.bottom-card {
  transform: translateY(+30%);
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}

.bottom-card:hover {
  transform: translateY(-110px) scale(1.7);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  z-index: 5;
}

.bottom-card.selected {
  transform: translateY(-130px) scale(1.8);
  box-shadow: 0 14px 28px rgba(0, 0, 0, 0.35);
  z-index: 10;
}
</style>
