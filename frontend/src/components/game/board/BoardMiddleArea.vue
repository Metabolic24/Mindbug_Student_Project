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
    <Card
      v-if="isImageVisible"
      :card="imgSrc"
      context="board"
      :selected="false"
      :attacking="false"
      :clickable="false"
      class="middle-card"
    />

    <span>{{ message }}</span>
  </div>
</template>

<style scoped>
  .middle-area {
    width: 100%;
    height: 40%;

    display: flex;
    align-items: center;
    justify-content: space-around;
  }

  span {
    font-size: 4.5vh;
    font-weight: bolder;
    color: mediumvioletred;

    background-color: rgba(197, 192, 192, 0.8);
    cursor: default;

    padding: 5px 15px;

    border-radius: 10px;
  }

  .middle-card {
    width: 6vw;
    height: 9vw;
    object-fit: cover;
    border-radius: 10px;
    box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.2);
  }

</style>
