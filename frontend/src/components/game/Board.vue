<script setup lang="ts">

import DiscardPile from "@/components/game/DiscardPile.vue";
import {getCardAlt, getCardImage} from "@/shared/CardUtils";
import {computed} from "vue";

interface Props {
  gameState: GameStateInterface

  selectedCard?: SelectedCardInterface
  attackingCard: CardInterface
  pickedCard: CardInterface
}

const props = defineProps<Props>()
const emit = defineEmits(['button-clicked', 'card-selected'])

const message = computed(() => {
  if (props.gameState?.choice) {
    if (props.gameState.choice.playerToChoose === props.gameState.player.uuid) {
      if (props.gameState.choice.type === "FRENZY") {
        return "Do you want to attack again ?"
      } else if (props.gameState.choice.type === "BOOLEAN") {
        return "Do you want to revive this card?" // TODO A modifier avec la valeur appropriée (voir comment on la récupère d'ailleurs)
      } else if (props.gameState.choice.type === "HUNTER") {
        return "Select a target to hunt or continue attacking"
      }
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

const buttonLabel = computed(() => {
  if (props.gameState?.choice) {
    if (props.gameState?.choice.type === "BOOLEAN" || props.gameState?.choice.type === "FRENZY") {
      return "Yes"
    } else if (props.gameState?.choice.type === "HUNTER") {
      return "Hunt target"
    }
  } else if (props.gameState?.playerTurn) {
    return props.selectedCard?.location === "Hand" ? "Play" : "Attack"
  } else {
    if (props.pickedCard) {
      return "Use Mindbug"
    } else {
      return "Block"
    }
  }
})

const secondButtonLabel = computed(() => {
  if (props.gameState?.choice && props.gameState?.choice.playerToChoose === props.gameState?.player.uuid) {
    if (props.gameState?.choice.type === "BOOLEAN" || props.gameState?.choice.type === "FRENZY") {
      return "No"
    } else if (props.gameState?.choice.type === "HUNTER") {
      return "Continue"
    }
  } else if (props.pickedCard && !props.gameState?.playerTurn) {
    return "No Mindbug"
  } else if (props.attackingCard && !props.gameState?.playerTurn) {
    return "Lose LP"
  }
})

const isButtonVisible = computed(() => {
  if (props.gameState?.choice) {
    return props.gameState?.choice.playerToChoose === props.gameState?.player.uuid &&
        (props.gameState?.choice.type === "BOOLEAN" || props.gameState?.choice.type === "FRENZY" || props.gameState?.choice.type === "HUNTER");// Choice case
  } else if (props.gameState?.playerTurn) {
    return props.selectedCard && !props.attackingCard // Play/attack Case
  } else {
    return props.pickedCard || // Mindbug case
        props.attackingCard // Block case
  }
})

const isButtonDisabled = computed(() => {
  return !props.selectedCard && (
      props.gameState?.choice?.type === "HUNTER" || // Hunter case
      (!props.gameState?.choice && props.attackingCard && !props.gameState?.playerTurn && !props.selectedCard) // Block case
  )
})

const isSecondButtonVisible = computed(() => {
  if (props.gameState?.choice) {
    return props.gameState?.choice.playerToChoose === props.gameState?.player.uuid &&
        (props.gameState?.choice.type === "BOOLEAN" || props.gameState?.choice.type === "FRENZY" || props.gameState?.choice.type === "HUNTER");// Choice case
  } else {
    return !props.gameState?.playerTurn && (props.pickedCard || // Mindbug case
        props.attackingCard) // Block case
  }
})

function onButtonClick(label: string) {
  emit('button-clicked', label)
}

function onCardSelected(card: CardInterface): void {
  if ((props.gameState?.playerTurn && !props.attackingCard && card.ableToAttack) || // Attack case
      (!props.gameState?.playerTurn && props.attackingCard && card.ableToBlock)) { // Block case
    emit('card-selected', card)
  }
}

function onOpponentCardSelected(card: CardInterface): void {
  if (props.gameState?.playerTurn && props.gameState?.choice?.type === "HUNTER") { // Hunter case
    emit('card-selected', card)
  }
}

function getCardClasses(card: CardInterface): Record<string, boolean> {
  return ({
    'selected': card.uuid === props.selectedCard?.uuid,
    'attacking': card.uuid === props.attackingCard?.uuid,
  })
}

const isImageVisible = computed(() => {
  return props.pickedCard !== undefined || (
      props.gameState?.choice?.type === "BOOLEAN" ||
      props.gameState?.choice?.type === "FRENZY" ||
      props.gameState?.choice?.type === "HUNTER")
})

const imgSrc = computed(() => {
  if (props.gameState?.choice) {
    return getCardImage(props.gameState.choice.sourceCard)
  } else if (props.pickedCard) {
    return getCardImage(props.pickedCard)
  }
})

const imgAlt = computed(() => {
  if (props.gameState?.choice) {
    return props.gameState.choice.sourceCard?.name
  } else if (props.pickedCard) {
    return props.pickedCard.name
  }
})

</script>

<template>
  <div class="row board">
    <div class="col-2 discards" style="background-color: green">
      <discard-pile :cards="gameState?.opponent.discard"></discard-pile>
      <discard-pile :cards="gameState?.player.discard"></discard-pile>
    </div>
    <div class="col-8" style="background-color: yellow">
      <div class="cards">
        <img v-for="card in gameState?.opponent.board" :src="getCardImage(card)" :alt="getCardAlt(card)"
             class="card-image" :class="getCardClasses(card)" @click="onOpponentCardSelected(card)"/>
      </div>
      <div class="middle-area">
        <img v-if="isImageVisible" :src="imgSrc" :alt="imgAlt" class="card-image"/>
        <span>{{ message }}</span>
      </div>
      <div class="cards">
        <img v-for="card in gameState?.player.board" :src="getCardImage(card)" :alt="getCardAlt(card)"
             class="card-image"
             :class="getCardClasses(card)" @click="onCardSelected(card)"/>
      </div>
    </div>
    <div class="col-2" style="background-color: green">
      <div class="buttons">
        <button v-if="isButtonVisible" :disabled="isButtonDisabled" @click="onButtonClick(buttonLabel)">
          {{ buttonLabel }}
        </button>
        <button v-if="isSecondButtonVisible" @click="onButtonClick(secondButtonLabel)">{{ secondButtonLabel }}</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.board {
  width: 100%;
  height: 80%;
}

.col-2 {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.discards {
  row-gap: 5px;
}

.middle-area {
  width: 100%;
  height: 40%;

  display: flex;
  align-items: center;
  justify-content: space-around;

  span {
    font-size: x-large;
    font-weight: bolder;
  }
}

.cards {
  width: 100%;
  height: 30%;

  display: flex;
  column-gap: 5px;
  align-items: center;
  justify-content: center;
}

.card-image {
  width: 6vw;
  height: 9vw;
  object-fit: cover;
  border-radius: 10px;
  box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.2);
}

.card-image.selected {
  outline: 4px solid red;
}

.card-image.attacking {
  outline: 4px solid orange;
}

.buttons {
  height: 100%;
  width: 100%;

  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  button {
    height: 30px;
  }
}
</style>
