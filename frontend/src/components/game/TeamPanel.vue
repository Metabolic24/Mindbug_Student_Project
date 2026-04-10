<script setup lang="ts">
import BoardTeam from "@/components/game/board/BoardTeam.vue";
import Hand from "@/components/game/Hand.vue";
import PlayerDetails from "@/components/game/PlayerDetails.vue";
import TeamDetails from "@/components/game/TeamDetails.vue";
import { ref, Ref } from "vue";
import DiscardModal from "@/components/game/board/DiscardModal.vue";
import DiscardPile from "@/components/game/board/DiscardPile.vue";

interface Props {
  gameState: GameStateInterface;
  selectedCard: SelectedCardInterface;
  pickedCard: CardInterface;
  attackingCard: CardInterface;
  onCardSelected: (card: CardInterface, location: "Board" | "Hand") => void;
  onActionButtonClick: (actionLabel: string) => void;
}

const props = defineProps<Props>();
const emit = defineEmits(['button-clicked']);

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

function closeModal() {
  isDiscardModalVisible.value = false;
}
</script>

<template>
    <div class="container-fluid game">
        <!-- TOP ROW -->
        <div class="top-row">
            <div class="discard top-left-discard">
              <discard-pile :cards="gameState?.opponents[0].discard" @clicked="displayDiscardModal(gameState?.opponents[0].discard, 'enemy',gameState.opponents[0].name)" position="bottom" isTeam/>
            </div>
            <div class="hand top-hand-left">
              <hand :cards="props.gameState?.opponents[0].hand" visibility="enemy" :selected-card="props.selectedCard"></hand>
            </div>

            <TeamDetails
              class="team-details top-team"
              :teamLife="props.gameState.opponents[0].lifePoints"
              :ally="props.gameState.opponents[0]"
              :player="props.gameState.opponents[1]"
              :isEnemy=true

              :game-state="gameState"
              :picked-card="pickedCard"
              :attacking-card="attackingCard"
              :selected-card="selectedCard" 
              @button-clicked="props.onActionButtonClick($event)"
            />

            <div class="hand top-hand-right">
              <hand :cards="props.gameState?.opponents[1].hand" visibility="enemy" :selected-card="props.selectedCard"></hand>
            </div>
            <div class="discard top-right-discard">
              <discard-pile :cards="gameState?.opponents[1].discard" @clicked="displayDiscardModal(gameState?.opponents[1].discard, 'enemy',gameState.opponents[1].name)" position="bottom" isTeam/>
            </div>
        </div>

        <!-- BOARD (MILIEU) -->
        <div class="board-area">
            <BoardTeam
              :game-state="props.gameState"
              :selected-card="props.selectedCard"
              :attacking-card="props.attackingCard"
              @card-selected="props.onCardSelected($event, 'Board')"
            />
        </div>

        <!-- BOTTOM ROW -->
        <div class="bottom-row">

            <div class="discard bottom-left-discard">
              <discard-pile :cards="gameState?.ally.discard" @clicked="displayDiscardModal(gameState?.ally.discard, 'ally', gameState.ally.name)" position="top" isTeam/>
            </div>
            <div class="hand bottom-hand-left">
              <hand :cards="props.gameState.ally.hand" visibility="ally" :selected-card="props.selectedCard"></hand>
            </div>

            <TeamDetails
              class="team-details bottom-team"
              :teamLife="props.gameState.player.lifePoints"
              :ally="props.gameState.ally"
              :player="props.gameState.player"
              :isEnemy=false

              :game-state="gameState"
              :picked-card="pickedCard"
              :attacking-card="attackingCard"
              :selected-card="selectedCard"
              @button-clicked="props.onActionButtonClick($event)"
            />

            <div class="hand bottom-hand-right">
              <hand :cards="props.gameState?.player?.hand" visibility="self" :selected-card="props.selectedCard"
              @card-selected="props.onCardSelected($event, 'Hand')"></hand>
            </div>
            <div class="discard bottom-right-discard">
              <discard-pile :cards="gameState?.player.discard" @clicked="displayDiscardModal(gameState?.player.discard, 'self', gameState.player.name)" position="top" isTeam/>
            </div>

        </div>
    </div>
    <discard-modal
      v-if="isDiscardModalVisible"
      :cards="discardModalData"
      :type="discardType"
      :playerName="discardPlayerName"
      @closeModal="closeModal()"
    />
</template>

<style scoped>
.game {
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100%;
  overflow: hidden;

  background-image: url("../../assets/playmats/default.png");
  background-size: cover;
}

/* ROWS */

.top-row,
.bottom-row {
  position: relative;
  height: 18vh;
  width: 100%;

  display: flex;
  align-items: center;
  justify-content: space-between;
  
}

/* BOARD */

.board-area {
  flex: 1;

  display: flex;
  justify-content: center;
  align-items: center;
}

/* HANDS */

.hand {
  flex: 1;
  height: 100%;

  margin: 0 10px;

  border: 2px solid grey;

  display: flex;
  justify-content: center;
  align-items: center;

  z-index: 1;
}
.bottom-hand-right,
.bottom-hand-left{
  transform: translateY(-40%);
}

.bottom-hand-right :deep(.card-wrapper.bottom-card:hover) {
  transform: translateY(-55%) scale(1.6);
  z-index: 5;
}

.bottom-hand-right :deep(.card-wrapper.bottom-card.selected) {
  transform: translateY(-57%) scale(1.7);
  z-index: 10;
}

.bottom-hand-left :deep(.card-wrapper.bottom-card:hover),
.bottom-hand-left :deep(.card-wrapper.bottom-card.selected) {
  transform: translateY(30%) scale(1.1);
  box-shadow: none;
  z-index: 1;
}

.top-hand-left,
.top-hand-right {
  transform: translateY(65%);
}

/* TEAM DETAILS */

.team-details {
  position: absolute;

  width: 100%;
  height: 13vh;

  display: flex;
  justify-content: center;
  align-items: center;
  border: 2px solid blue;

  z-index: 2;

  pointer-events: none;
}
.team-details * {
  pointer-events: auto;
}
.top-team{
    top: 0%;
}
.bottom-team{
    bottom: 0%;
}

/* DISCARD */

.discard {
  position: absolute;

  width: 8vw;
  height: 11vw;
  z-index: 3;
  

  display: flex;
  align-items: center;
  justify-content: center;
}

.top-left-discard {
  top: 50%;
  transform: translateY(-40%);
}

.top-right-discard {
  right: 0px;
  top: 50%;
  transform: translateY(-40%);
}

.bottom-left-discard {
  top: 50%;
  transform: translateY(-60%);
}

.bottom-right-discard {
  right: 0px;
  top: 50%;
  transform: translateY(-60%);
}
</style>
