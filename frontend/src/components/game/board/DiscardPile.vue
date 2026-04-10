<script setup lang="ts">
import {computed} from "vue";
import Card from "@/components/game/Card.vue";
import {useI18n} from "vue-i18n";

const { t } = useI18n();

// Declare the interface for the data given by the parent component
interface Props {
  cards: CardInterface[];
  position?: "top" | "bottom";
  isTeam?: boolean
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
  <div class="discard-wrapper" :class="{ isTeam: props.isTeam }">
    <!-- Title above pile -->
    <div class="discard-title" :class="titleClass">{{ t('game.discard_pile') }}</div>

    <div class="discard-container" :class="{ empty: !lastCard }" @click="onClick">
      <!-- Last card displayed -->
      <card v-if="lastCard" :card="lastCard" context="discard-pile" visibility="self" :clickable="false"/>
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

  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 6px;
}

.discard-wrapper.title-bottom {
  flex-direction: column-reverse;
}
.discard-wrapper.isTeam {
  width: 100%;
  height: 100%;
  position: relative;
  justify-content: center;
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
  top: -80px;
}

.discard-title.bottom {
  bottom: -80px;
}

.discard-wrapper.isTeam .discard-title {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  font-weight: 700;
  font-size: 1.2rem;
  color: #1d1c1c;
  white-space: nowrap;
}

.discard-wrapper.isTeam .discard-title.top {
  top: -25px;
}

.discard-wrapper.isTeam .discard-title.bottom {
  bottom: -25px;
}

/* Pile container */
.discard-container {
  position: relative;
  width: 8vw;
  height: 12vw;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 10px;
  border: 2px solid rgb(0, 0, 0);
  background: rgba(0, 0, 0, 0.247);
}

.discard-wrapper.isTeam .discard-container {
  width: 100%;
  height: 100%;
}

/* Empty state style */
.discard-container.empty {
  border: 2px dashed rgb(0, 0, 0);
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
