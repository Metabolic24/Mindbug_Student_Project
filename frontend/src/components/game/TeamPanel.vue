<script setup lang="ts">
import Board from "@/components/game/board/Board.vue";
import Hand from "@/components/game/Hand.vue";
import PlayerDetails from "@/components/game/PlayerDetails.vue";
import {Ref} from "vue";
//import {CardInterface, SelectedCardInterface, GameStateInterface} from "@/components/game/Game.vue"; // ajuste selon tes types
import {declareAttack, pickCard, playCard, resolveAction, resolveAttack, resolveBoolean, resolveMultipleTargetChoice, resolveSingleTargetChoice} from "@/shared/RestService";

interface Props {
  gameState: GameStateInterface;
  selectedCard: SelectedCardInterface;
  pickedCard: CardInterface;
  attackingCard: CardInterface;
  onCardSelected: (card: CardInterface, location: "Board" | "Hand") => void;
  onActionButtonClick: (actionLabel: string) => void;
  onLeaveButtonClick: () => void;
}

const props = defineProps<Props>();
</script>

<template>
    <div class="container-fluid game">
        <!-- TOP ROW -->
        <div class="top-row">
            <div class="discard top-left-discard">discard</div>
            <div class="hand top-hand-left">hand</div>

            <div class="team-details top-team">
                Teamdétail
            </div>
            <div class="hand top-hand-right">hand</div>
            <div class="discard top-right-discard">discard</div>
        </div>

        <!-- BOARD (MILIEU) -->
        <div class="board-area">
            board
            <button type="button" class="leave-button" @click="props.onLeaveButtonClick()">
            <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor" class="bi bi-door-open" viewBox="0 0 16 16">
                <path d="M8.5 10c-.276 0-.5-.448-.5-1s.224-1 .5-1 .5.448.5 1-.224 1-.5 1"></path>
                <path d="M10.828.122A.5.5 0 0 1 11 .5V1h.5A1.5 1.5 0 0 1 13 2.5V15h1.5a.5.5 0 0 1 0 1h-13a.5.5 0 0 1 0-1H3V1.5a.5.5 0 0 1 .43-.495l7-1a.5.5 0 0 1 .398.117M11.5 2H11v13h1V2.5a.5.5 0 0 0-.5-.5M4 1.934V15h6V1.077z"></path>
            </svg>
            </button>
        </div>

        <!-- BOTTOM ROW -->
        <div class="bottom-row">

            <div class="discard bottom-left-discard">discard</div>
            <div class="hand bottom-hand-left">hand</div>

            <div class="team-details bottom-team">
                Teamdétail
            </div>

            <div class="hand bottom-hand-right">hand</div>
            <div class="discard bottom-right-discard">discard</div>

        </div>
    </div>
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
.leave-button {
  width: 2vw;
  height: 4vh;
  background-color: rgba(255, 255, 255, 0.5);
  border: none;
  border-radius: 25px;
  transition: background-color 0.3s, transform 0.2s;
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

  background: rgba(255,255,0,0.25);
  border: 2px solid yellow;

  z-index: 3;
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