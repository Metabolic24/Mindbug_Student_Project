<script setup lang="ts">
import Card from "@/components/game/Card.vue";
import DiscardPile from "@/components/game/board/DiscardPile.vue";
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
const emit = defineEmits(['button-clicked', 'card-selected', 'card-preview'])

function onCardSelected(card: CardInterface): void {
  const isCurrentPlayerTurn = props.gameState?.currentPlayerID === props.gameState?.player.uuid

  if ((isCurrentPlayerTurn && !props.attackingCard && card.ableToAttack) || // Attack case
      (!isCurrentPlayerTurn && props.attackingCard && card.ableToBlock && // Block case
          (props.attackingCard.keywords.includes("SNEAKY") && card.keywords.includes("SNEAKY") ||
              !props.attackingCard.keywords.includes("SNEAKY")))) {
    emit('card-selected', card)
  }
}

function onOpponentCardSelected(card: CardInterface): void {
  if (props.gameState?.currentPlayerID === props.gameState?.player.uuid && props.gameState?.choice?.type === "HUNTER") { // Hunter case
    emit('card-selected', card)
  }
}

function onCardPreview(card: CardInterface): void {
  emit('card-preview', card)
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
      props.gameState?.opponents[0].discard :
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
      <discard-pile :cards="gameState?.opponents[0].discard" @clicked="displayDiscardModal(true)"></discard-pile>
      <discard-pile :cards="gameState?.player.discard" @clicked="displayDiscardModal(false)"></discard-pile>
    </div>
    <div class="col-8">
      <div class="cards">
        <card
          v-for="card in gameState?.opponents[0].board"
          :key="card.uuid"
          :card="card"
          context="opponent-board"
          :selected="card.uuid === selectedCard?.uuid"
          :attacking="card.uuid === attackingCard?.uuid"
          :clickable="true"
          @click="onOpponentCardSelected"
          @preview="onCardPreview"
        />
      </div>
      <board-middle-area
        :game-state="gameState"
        :picked-card="pickedCard"
        :attacking-card="attackingCard"
        @card-preview="onCardPreview"
      ></board-middle-area>
      <div class="cards">
        <card
          v-for="card in gameState?.player.board"
          :key="card.uuid"
          :card="card"
          context="player-board"
          :selected="card.uuid === selectedCard?.uuid"
          :attacking="card.uuid === attackingCard?.uuid"
          :clickable="true"
          @click="onCardSelected"
          @preview="onCardPreview"
        />
      </div>
    </div>
    <div class="col-2 board-buttons-col">
      <board-buttons :game-state="gameState" :picked-card="pickedCard" :attacking-card="attackingCard"
                     :selected-card="selectedCard" @button-clicked="emit('button-clicked', $event)"></board-buttons>
    </div>
  </div>
  <discard-modal
    v-if="isDiscardModalVisible"
    :cards="discardModalData"
    :opponent="isOpponentDiscard"
    @closeModal="closeModal()"
    @card-preview="emit('card-preview', $event)"
  ></discard-modal>
</template>

<style scoped>
.board {
  width: 100%;
  height: 100%;
  min-height: 0;
  position: relative;
}

.col-2 {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.discards {
  position: absolute;
  left: 16px;
  top: 54%;
  transform: translateY(-50%);
  z-index: 3;
  justify-content: center;
  gap: 15vh;
  padding: 0;
}

.board-buttons-col {
  position: absolute;
  right: 20px;
  bottom: -210px;
  z-index: 3;
  align-items: flex-end;
  justify-content: flex-end;
  padding-right: 0;
  padding-bottom: 0;
}

.cards {
  width: 100%;
  height: 35%;

  display: flex;
  column-gap: 5px;
  align-items: center;
  justify-content: center;
}

.board .col-8 {
  padding-left: 0;
  padding-right: 0;
  width: 100%;
  position: absolute;
  left: 50%;
  top: 52%;
  transform: translate(-50%, -50%);
  height: 80%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
}

</style>
