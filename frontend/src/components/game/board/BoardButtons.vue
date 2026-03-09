<script setup lang="ts">
import {computed, ComputedRef} from "vue";
import {useI18n} from "vue-i18n";

const {t} = useI18n();

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

const mainButtonData: ComputedRef<ButtonData> = computed(() => {
  const result: ButtonData = {
    label: "",
    visible: !props.gameState?.winner, // should not be visible if game is already finished
    disabled: false,
    event: undefined
  }

  if (props.gameState?.choice) {
    if (props.gameState.choice.type === "BOOLEAN" || props.gameState.choice.type === "FRENZY") { // Boolean/Frenzy case
      result.label = 'misc.yes'
      result.visible = props.gameState.choice.playerToChoose === props.gameState.player.uuid
      result.event = "YES"
    } else if (props.gameState.choice.type === "HUNTER") { // Hunter case
      result.label = 'game.buttons.hunt'
      result.disabled = !props.selectedCard
      result.visible = props.gameState.choice.playerToChoose === props.gameState.player.uuid
      result.event = "HUNT"
    } else {
      result.visible = false
    }
  } else if (props.gameState?.playerTurn) {
    if (props.selectedCard) {
      if (props.selectedCard?.location === "Hand") {
        result.label = 'game.buttons.play'
        result.disabled = props.gameState?.forcedAttack
        result.event = "PLAY"
      } else {
        result.label = 'game.buttons.attack'
        result.disabled = !props.selectedCard.ableToAttack
        result.event = "ATTACK"
      }

      result.visible = !props.attackingCard
    } else {
      result.visible = false
    }
  } else {
    if (props.pickedCard) { // Mindbug case
      result.label = 'game.buttons.use_mindbug'
      result.event = "MINDBUG"
    } else if (props.attackingCard) { // Block case
      result.label = 'game.buttons.block'
      result.disabled = !(props.selectedCard && props.attackingCard.ownerId !== props.gameState?.player.uuid)
      result.visible = props.attackingCard.ownerId !== props.gameState?.player.uuid;
      result.event = "BLOCK"
    } else {
      result.visible = false
    }
  }

  return result
})

const secondButtonData: ComputedRef<ButtonData> = computed(() => {
  const result: ButtonData = {
    label: "",
    visible: !props.gameState.winner, // should not be visible if game is already finished
    disabled: false,
    event: undefined
  }

  if (props.gameState?.choice) {
    if (props.gameState.choice.type === "BOOLEAN" || props.gameState.choice.type === "FRENZY") { // Boolean/Frenzy case
      result.label = 'misc.no'
      result.visible = props.gameState.choice.playerToChoose === props.gameState.player.uuid
      result.event = "NO"
    } else if (props.gameState.choice.type === "HUNTER") { // Hunter case
      result.label = 'game.buttons.continue'
      result.visible = props.gameState.choice.playerToChoose === props.gameState.player.uuid
      result.event = "CONTINUE"
    } else {
      result.visible = false
    }
  } else if (props.gameState?.playerTurn) {
    if (props.selectedCard?.location === "Board" && props.selectedCard?.hasAction) {
      result.label = "game.buttons.action"
      result.disabled = props.gameState.forcedAttack
      result.event = "ACTION"
    } else {
      result.visible = false
    }
  } else if (props.pickedCard) { // Mindbug case
    result.label = "game.buttons.no_mindbug"
    result.event = "NO_MINDBUG"
  } else if (props.attackingCard) { // Block case
    result.label = "game.buttons.lose_lp"
    result.event = "LOSE_LP"
  } else {
    result.visible = false
  }

  return result
})
</script>

<template>
  <div class="buttons">
    <button v-if="mainButtonData.visible" :disabled="mainButtonData.disabled" @click="emit('button-clicked', mainButtonData.event)">
      {{ t(mainButtonData.label) }}
    </button>
    <button v-if="secondButtonData.visible" :disabled="secondButtonData.disabled" @click="emit('button-clicked', secondButtonData.event)">
      {{ t(secondButtonData.label) }}
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
