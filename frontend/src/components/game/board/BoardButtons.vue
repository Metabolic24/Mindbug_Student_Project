<script setup lang="ts">
import {computed} from "vue";

// Declare the interface for the data given by the parent component
interface Props {
  gameState: GameStateInterface

  selectedCard: SelectedCardInterface
  pickedCard: CardInterface
  attackingCard: CardInterface
}

const props = defineProps<Props>()

// Declare events emitted by this component
const emit = defineEmits(['button-clicked'])

// Computed value for the first button label
const buttonLabel = computed(() => {
  if (props.gameState?.choice) {
    if (props.gameState?.choice.type === "BOOLEAN" || props.gameState?.choice.type === "FRENZY") { // Boolean/Frenzy case
      return "Yes"
    } else if (props.gameState?.choice.type === "HUNTER") { // Hunter case
      return "Hunt target"
    }
  } else if (props.gameState?.playerTurn) {
    return props.selectedCard?.location === "Hand" ? "Play" : "Attack" // Play/attack case
  } else {
    if (props.pickedCard) { // Mindbug case
      return "Use Mindbug"
    } else { // Block case
      return "Block"
    }
  }
})

// Computed value for the first button visibility
const isFirstButtonVisible = computed(() => {
  if (props.gameState.winner) {
    return false
  } else if (props.gameState?.choice) {
    return props.gameState?.choice.playerToChoose === props.gameState?.player.uuid &&
        (props.gameState?.choice.type === "BOOLEAN" || props.gameState?.choice.type === "FRENZY" || props.gameState?.choice.type === "HUNTER");// Choice case
  } else if (props.gameState?.playerTurn) {
    return props.selectedCard && !props.attackingCard // Play/attack case
  } else {
    return props.pickedCard || props.attackingCard // Mindbug or Block case
  }
})

// Computed value to manage 'disable' state of the first button
const isFirstButtonDisabled = computed(() => {
  const label = buttonLabel.value
  switch (label) {
    case "Use Mindbug":  // Mindbug
    case "Yes": // Frenzy case
      return false
    case "Block":  // Block case
      return !(props.selectedCard && props.attackingCard && props.attackingCard.ownerId !== props.gameState?.player.uuid)
    case "Hunt target":   // Hunter case
      return !props.selectedCard
    case "Play":    // Play case
    case "Attack":   // Attack case
      return props.gameState?.forcedAttack

    default:
      return false
  }
})

// Computed value for the second button label
const secondButtonLabel = computed(() => {
  if (props.gameState?.choice && props.gameState?.choice.playerToChoose === props.gameState?.player.uuid) {
    if (props.gameState?.choice.type === "BOOLEAN" || props.gameState?.choice.type === "FRENZY") { // Boolean/Frenzy choice case
      return "No"
    } else if (props.gameState?.choice.type === "HUNTER") { // Hunter choice case
      return "Continue"
    }
  } else if (props.gameState?.playerTurn) {
    if (props.selectedCard?.location === "Board" && props.selectedCard?.hasAction) {
      return "Action"
    }
  } else if (props.pickedCard) { // Mindbug case
    return "No Mindbug"
  } else if (props.attackingCard) { // Block case
    return "Lose LP"
  }
})

// Computed value for the second button visibility
const isSecondButtonVisible = computed(() => {
  if (props.gameState.winner) {
    return false
  } else if (props.gameState?.choice) {
    return props.gameState?.choice.playerToChoose === props.gameState?.player.uuid &&
        (props.gameState?.choice.type === "BOOLEAN" || props.gameState?.choice.type === "FRENZY" || props.gameState?.choice.type === "HUNTER");// Choice case
  } else if (props.gameState?.playerTurn) {
    return props.selectedCard?.location === "Board" && props.selectedCard?.hasAction
  } else {
    return (props.pickedCard || props.attackingCard) // Mindbug or Block case
  }
})

// Computed value to manage 'disable' state of the first button
const isSecondButtonDisabled = computed(() => {
  return !props.gameState?.choice && props.selectedCard?.location === "Board" && props.gameState?.playerTurn && props.gameState?.forcedAttack // Using the card action is forbidden due to forcedAttack
})

</script>

<template>
  <div class="buttons">
    <button v-if="isFirstButtonVisible" :disabled="isFirstButtonDisabled" @click="emit('button-clicked', buttonLabel)">
      {{ buttonLabel }}
    </button>
    <button v-if="isSecondButtonVisible" :disabled="isSecondButtonDisabled" @click="emit('button-clicked', secondButtonLabel)">
      {{ secondButtonLabel }}
    </button>
  </div>
</template>

<style scoped>
.buttons {
  height: 100%;
  width: 100%;

  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1vh;

  button {
    width: 15vw;
    padding: 1vw;

    font-size: 5vh;
    background-color: rgba(250, 250, 250, 0.8);

    border-radius: 10px;
    box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.2);
  }

  button:hover {
    background-color: rgba(255, 255, 255, 0.9);
    transform: scale(1.05);
  }

  button:active {
    background-color: #1e6f93;
    transform: scale(0.98);
  }
}
</style>
