<script setup lang="ts">
import DiscardPile from "@/components/game/board/DiscardPile.vue";
import {getCardAlt, getCardImage} from "@/shared/CardUtils";
import {ref, Ref} from "vue";
import DiscardModal from "@/components/game/board/DiscardModal.vue";
import BoardButtons from "@/components/game/board/BoardButtons.vue";
import BoardMiddleArea from "@/components/game/board/BoardMiddleArea.vue";

// Declare the interface for the data given by the parent component
interface Props {
  gameState: GameStateInterface

  selectedCard: SelectedCardInterface
  pickedCard: CardInterface
  attackingCard: CardInterface
}

const props = defineProps<Props>()

// Declare events emitted by this component
const emit = defineEmits(['button-clicked', 'card-selected'])

function onCardSelected(card: CardInterface): void {
  if ((props.gameState?.playerTurn && !props.attackingCard && card.ableToAttack) || // Attack case
      (!props.gameState?.playerTurn && props.attackingCard && card.ableToBlock && // Block case
          (props.attackingCard.keywords.includes("SNEAKY") && card.keywords.includes("SNEAKY") ||
              !props.attackingCard.keywords.includes("SNEAKY")))) {
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

const discardModalData: Ref<CardInterface[]> = ref([]);
const isDiscardModalVisible: Ref<boolean> = ref(false);
const isOpponentDiscard: Ref<boolean> = ref(false);

function displayDiscardModal(opponent: boolean) {
  isDiscardModalVisible.value = true;
  isOpponentDiscard.value = opponent;
  discardModalData.value = opponent ?
      props.gameState?.opponent.discard :
      props.gameState?.player.discard;
}

// Triggered when the player wants to close the modal
function closeModal() {
  isDiscardModalVisible.value = false;
}
</script>

<template>
  <div class="row board">
    <div class="col-2 discards">
      <discard-pile :cards="gameState?.opponent.discard" @clicked="displayDiscardModal(true)"></discard-pile>
      <discard-pile :cards="gameState?.player.discard" @clicked="displayDiscardModal(false)"></discard-pile>
    </div>
    <div class="col-8">
      <div class="cards">
        <img v-for="card in gameState?.opponent.board" :src="getCardImage(card)" :alt="getCardAlt(card)"
             class="card-image" :class="getCardClasses(card)" @click="onOpponentCardSelected(card)" draggable="false"
             @contextmenu.prevent=""/>
      </div>
      <board-middle-area :game-state="gameState" :picked-card="pickedCard" :attacking-card="attackingCard"></board-middle-area>
      <div class="cards">
        <img v-for="card in gameState?.player.board" :src="getCardImage(card)" :alt="getCardAlt(card)"
             class="card-image" :class="getCardClasses(card)" @click="onCardSelected(card)" draggable="false"
             @contextmenu.prevent=""/>
      </div>
    </div>
    <div class="col-2">
      <board-buttons :game-state="gameState" :picked-card="pickedCard" :attacking-card="attackingCard"
                     :selected-card="selectedCard" @button-clicked="emit('button-clicked', $event)"></board-buttons>
    </div>
  </div>
  <discard-modal v-if="isDiscardModalVisible" :cards="discardModalData" :opponent="isOpponentDiscard"
                 @closeModal="closeModal()"></discard-modal>
</template>

<style scoped>
.board {
  width: 100%;
  height: 70vh;
}

.col-2 {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.discards {
  justify-content: space-around;
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
</style>
