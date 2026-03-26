<script setup lang="ts">
import Card from "@/components/game/Card.vue";
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
const discardType = ref<"enemy" | "ally" | "self">("self");
const discardPlayerName = ref<string>("");

function displayDiscardModal(cards: CardInterface[], type: "enemy" | "ally" | "self", playerName: string) {
  discardModalData.value = cards;
  discardType.value = type;
  discardPlayerName.value = playerName;
  isDiscardModalVisible.value = true;
}

// Triggered when the player wants to close the modal
function closeModal() {
  isDiscardModalVisible.value = false;
}
</script>

<template>
  <div class="row board">
    <div class="col-2 discards">
      <discard-pile :cards="gameState?.opponents[0].discard" @clicked="displayDiscardModal(gameState?.opponents[0].discard, 'enemy' ,gameState.opponents[0].name)" position="bottom"></discard-pile>
      <discard-pile :cards="gameState?.player.discard" @clicked="displayDiscardModal(gameState.player.discard, 'self' ,gameState.player.name)" position="top"></discard-pile>
    </div>
    <div class="col-8">
      <div class="cards">
        <Card
          v-for="card in gameState?.opponents[0].board"
          :key="card.uuid"
          :card="card"
          context="opponent-board"
          visibility="enemy"
          :selected="card.uuid === selectedCard?.uuid"
          :attacking="card.uuid === attackingCard?.uuid"
          :clickable="true"
          @click="onOpponentCardSelected"
        />
      </div>
      <board-middle-area :game-state="gameState" :picked-card="pickedCard" :attacking-card="attackingCard"></board-middle-area>
      <div class="cards">
        <Card
          v-for="card in gameState?.player.board"
          :key="card.uuid"
          :card="card"
          context="player-board"
          visibility="enemy"
          :selected="card.uuid === selectedCard?.uuid"
          :attacking="card.uuid === attackingCard?.uuid"
          :clickable="true"
          @click="onCardSelected"
        />
      </div>
    </div>
    <div class="col-2">
      <board-buttons :game-state="gameState" :picked-card="pickedCard" :attacking-card="attackingCard"
                     :selected-card="selectedCard" @button-clicked="emit('button-clicked', $event)"></board-buttons>
    </div>
  </div>
  <discard-modal v-if="isDiscardModalVisible" :cards="discardModalData" :type="discardType" :playerName="discardPlayerName"
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
  height: 40%;

  display: flex;
  column-gap: 20px;
  align-items: center;
  justify-content: center;
}

</style>
