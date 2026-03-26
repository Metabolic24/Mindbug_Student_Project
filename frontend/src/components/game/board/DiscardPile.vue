<script setup lang="ts">
import {computed} from "vue";
import Card from "@/components/game/Card.vue";

// Declare the interface for the data given by the parent component
interface Props {
  cards: CardInterface[];
  position?: "top" | "bottom"
}

const props = defineProps<Props>()

// Declare events emitted by this component
const emit = defineEmits(['clicked'])

// Triggered when the player clicks on the component
function onClick() {
  if (props.cards.length > 0) {
    emit('clicked')
  }
}

const lastCard = computed(() => {
  return props.cards.length > 0
      ? props.cards[props.cards.length - 1]
      : undefined
})

const titleClass = computed(() => ({
  top: props.position === "top",
  bottom: props.position === "bottom",
}));
</script>

<template>
  <div class="discard-wrapper">
    <!-- Title above pile -->
    <div class="discard-title" :class="titleClass">Discard Pile</div>

    <div class="discard-container" :class="{ empty: !lastCard }" @click="onClick">
      <!-- Last card displayed -->
      <Card v-if="lastCard" :card="lastCard" context="discard-pile" visibility="self" :clickable="false"/>
      <!-- Counter badge -->
      <div v-if="props.cards.length > 0" class="counter-badge">
        {{ props.cards.length }}
      </div>
    </div>
  </div>
</template>

<style scoped>
.discard-wrapper {
  width: 100%;
  height: 100%;

  display: flex;
  justify-content: center;
  align-items: center;
}

/* Title above the pile */
.discard-title {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  font-weight: 700;
  font-size: 1.2rem;
  color: #1d1c1c;
  white-space: nowrap;
}

.discard-title.top {
  top: -25px;
}

.discard-title.bottom {
  bottom: -25px;
}

/* Pile container */
.discard-container {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 10px;
  border: 2px solid rgb(0, 0, 0);
  background: rgba(0, 0, 0, 0.247);
}

/* Empty state style */
.discard-container.empty {
  border: 2px dashed rgb(0, 0, 0);
  background: rgba(0, 0, 0, 0.247);
}

/* Counter badge */
.counter-badge {
  position: absolute;
  top: -8px;
  right: -8px;
  background: #007bff;
  color: white;
  font-size: 1rem;
  font-weight: bold;
  padding: 4px 12px;
  border-radius: 50%;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
}
</style>
