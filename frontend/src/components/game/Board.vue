<script setup lang="ts">

import DiscardPile from "@/components/game/DiscardPile.vue";
import {getCardAlt, getCardImage} from "@/shared/CardUtils";
import {computed} from "vue";

interface Props {
  playerBoard: CardInterface[]
  playerDiscard: CardInterface[]
  opponentBoard: CardInterface[]
  opponentDiscard: CardInterface[]
  selectedCard?: SelectedCardInterface
  attackingCard: CardInterface
  playerTurn: boolean
}

const props = defineProps<Props>()
const emit = defineEmits(['button-clicked', 'card-selected'])

const buttonLabel = computed(() => {
  if (props.selectedCard) {
    if (props.attackingCard && !props.playerTurn && props.selectedCard.location === "Board") {
      return "Block"
    } else if (!props.attackingCard && props.playerTurn) {
      return props.selectedCard.location === "Hand" ? "Play" : "Attack"
    }
  }
})

const isButtonVisible = computed(() => {
  return props.selectedCard && ((props.attackingCard && props.selectedCard.location === "Board") || (!props.attackingCard && props.playerTurn))
})

function onButtonClick() {
  emit('button-clicked', buttonLabel.value)
}

function onCardSelected(card: CardInterface): void {
  if ((props.playerTurn && card.ableToAttack) || (!props.playerTurn && props.attackingCard && card.ableToBlock)) {
    emit('card-selected', card)
  }
}

function getPlayerCardClasses(card: CardInterface): Record<string, boolean> {
  return ({
    'selected': card.uuid === props.selectedCard?.uuid,
    'attacking': card.uuid === props.attackingCard?.uuid,
  })
}

function getOpponentCardClasses(card: CardInterface): Record<string, boolean> {
  return ({
    'attacking': card.uuid === props.attackingCard?.uuid,
  })
}

</script>

<template>
  <div class="row" style="width: 100%;height: 40%">
    <div class="col-2" style="background-color: green"></div>
    <div class="col-8" style="background-color: yellow">
      <img v-for="card in opponentBoard" :src="getCardImage(card)" :alt="getCardAlt(card)" class="card-image"
           :class="getOpponentCardClasses(card)" />
    </div>
    <div class="col-2" style="background-color: green">
      <discard-pile :cards="opponentDiscard"></discard-pile>
    </div>
  </div>
  <div class="row" style="width: 100%;height: 40%">
    <div class="col-2" style="background-color: green">
      <discard-pile :cards="playerDiscard"></discard-pile>
    </div>
    <div class="col-8" style="background-color: yellow">
      <img v-for="card in playerBoard" :src="getCardImage(card)" :alt="getCardAlt(card)" class="card-image"
           :class="getPlayerCardClasses(card)" @click="onCardSelected(card)"/>
    </div>
    <div class="col-2" style="background-color: green">
      <button v-if="isButtonVisible" @click="onButtonClick()">{{ buttonLabel }}</button>
    </div>
  </div>
</template>

<style scoped>
.col-8 {
  display: flex;
}

.card-image {
  width: 6vw;
  height: 9vw;
  object-fit: cover;
  border: 2px solid black;
  border-radius: 10px;
  box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.2);
}

.card-image.selected {
  outline: 4px solid red;
}

.card-image.attacking {
  outline: 4px solid orange;
}

</style>
