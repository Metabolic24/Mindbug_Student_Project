<script setup lang="ts">
// Declare the interface for the data given by the parent component
import {computed} from "vue";
import {getCardImage} from "@/shared/CardUtils";
import Card from "../Card.vue";

// Declare the interface for the data given by the parent component
interface Props {
  gameState: GameStateInterface

  pickedCard: CardInterface
  attackingCard: CardInterface
}
const props = defineProps<Props>()

// Computed value for the message
const message = computed(() => {
  if (props.gameState.winner) {
    const gameOver = "Game Over : "
    if (props.gameState.winner === props.gameState.player.uuid) {
      return gameOver + "You WIN !"
    } else {
      return gameOver + "You LOSE !"
    }
  } else if (props.gameState?.choice) {
    if (props.gameState.choice.playerToChoose === props.gameState.player.uuid) {
      if (props.gameState.choice.type === "FRENZY") {
        return "Do you want to attack again ?"
      } else if (props.gameState.choice.type === "BOOLEAN") {
        return props.gameState.choice.message
      } else if (props.gameState.choice.type === "HUNTER") {
        return "Select a target to hunt or continue attacking"
      }
    } else {
      return "Waiting for opponent choice..."
    }
  } else if (props.gameState?.playerTurn) {
    if (props.pickedCard || props.attackingCard) {
      return "Waiting for opponent..."
    } else {
      return "Play OR Attack"
    }
  } else {
    if (props.pickedCard) {
      return "Do you want to use a MindBug ?"
    } else if (props.attackingCard) {
      return "Block OR Lose LP"
    } else {
      return "Waiting for opponent..."
    }
  }
})

const isPlayerTurn = computed(() => props.gameState?.playerTurn)

// Computed value that controls image visibility
const isImageVisible = computed(() => {
  return !props.gameState?.winner &&
      (props.pickedCard !== undefined ||
      props.gameState?.choice?.type === "BOOLEAN" ||
      props.gameState?.choice?.type === "FRENZY" ||
      props.gameState?.choice?.type === "HUNTER")
})

// Computed value for the image source URL
const imgSrc = computed(() => {
  if (props.gameState?.choice) {
    return props.gameState.choice.sourceCard
  } else if (props.pickedCard) {
    return props.pickedCard
  }
})

</script>

<template>
  <div class="middle-area">
    <div class="turn-indicator" :class="{ 'opponent-turn': !isPlayerTurn }" aria-live="polite">
      <div class="turn-segment turn-segment--chevron" aria-hidden="true">
        <svg class="turn-chevron" viewBox="0 0 20 20" focusable="false">
          <path d="M4 7 L10 13 L16 7" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" />
        </svg>
      </div>
      <div class="turn-segment turn-segment--label">
        <span class="turn-label">{{ message }}</span>
      </div>
      <div class="turn-segment turn-segment--chevron" aria-hidden="true">
        <svg class="turn-chevron" viewBox="0 0 20 20" focusable="false">
          <path d="M4 7 L10 13 L16 7" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" />
        </svg>
      </div>
    </div>
    <div class="turn-segment--card-slot" aria-hidden="true">
     <Card
       v-if="isImageVisible"
       :card="imgSrc"
       context="board"
       :selected="false"
       :attacking="false"
       :clickable="false"
       class="middle-card"
      />
    </div>
  </div>
</template>

<style scoped>
  .middle-area {
    width: 100vw;
    height: 30%;

    display: flex;
    align-items: center;
    justify-content: center;
    gap: 16px;
    position: relative;
    left: 50%;
    transform: translateX(-50%);
  }

  .turn-indicator {
    display: flex;
    align-items: center;
    justify-content: stretch;

    max-width: 100%;
    height: clamp(36px, 6vh, 56px);
    box-sizing: border-box;

    background: #ffffff;
    border: 2px solid #1d1d1d;
    box-shadow: none !important;
    outline: 0 !important;
    filter: none !important;
    overflow: visible;
    cursor: default;
    padding: 0 8px;

    margin: 0 auto;
  }

  .turn-segment {
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .turn-segment--empty {
    flex: 1 1 0;
    min-width: clamp(24px, 4vw, 80px);
    background: #ffffff;
  }

  .turn-segment--chevron {
    flex: 0 0 auto;
    width: clamp(32px, 5vw, 55px);
    background: transparent;
  }

  .turn-segment--label {
    flex: 0 1 auto;
    max-width: 60vw;
    padding: 0 75px;
  }

  .turn-segment--card-slot {
    position: absolute;
    right: 16px;
    top: 50%;
    transform: translateY(-50%);
    overflow: visible;
    min-width: calc(8.8vw + 16px);
  }

  .turn-segment--card-slot .middle-card {
    width: calc(8vw * 1.25);
    height: calc(12vw * 1.25);
  }

  .turn-label {
    font-size: clamp(24px, 3.6vh, 40px);
    font-weight: 700;
    color: #1b1b1b;
    letter-spacing: 0.02em;
    line-height: 1;
    white-space: nowrap;
    text-align: center;
    flex: 1 1 auto;
  }

  .turn-chevron {
    width: 34px;
    height: 34px;
    color: #1b1b1b;
    display: block;
    flex: 0 0 auto;
  }

  .middle-card {
    object-fit: cover;
    border-radius: 10px;
    box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.2);
    margin-right: 5vw;
  }

</style>
