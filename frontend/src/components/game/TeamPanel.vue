<script setup lang="ts">
import BoardTeam from "@/components/game/board/BoardTeam.vue";
import Hand from "@/components/game/Hand.vue";
import PlayerDetails from "@/components/game/PlayerDetails.vue";
import TeamDetails from "@/components/game/TeamDetails.vue";
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
}

const props = defineProps<Props>();
</script>

<template>
    <div class="container-fluid game">
        <!-- TOP ROW -->
        <div class="top-row">
            <div class="discard top-left-discard">discard</div>
            <div class="hand top-hand-left">
              <hand :cards="props.gameState?.opponents[0].hand" visibility="enemy" :selected-card="props.selectedCard"></hand>
            </div>

            <TeamDetails
              class="team-details top-team"
              :teamLife="props.gameState.opponents[0].lifePoints"
              :ally="props.gameState.opponents[0]"
              :player="props.gameState.opponents[1]"
            />

            <div class="hand top-hand-right">
              <hand :cards="props.gameState?.opponents[1].hand" visibility="enemy" :selected-card="props.selectedCard"></hand>
            </div>
            <div class="discard top-right-discard">discard</div>
        </div>

        <!-- BOARD (MILIEU) -->
        <div class="board-area">
            <BoardTeam/>
        </div>

        <!-- BOTTOM ROW -->
        <div class="bottom-row">

            <div class="discard bottom-left-discard">discard</div>
            <div class="hand bottom-hand-left">
              <hand :cards="props.gameState.ally.hand" visibility="ally" :selected-card="props.selectedCard"></hand>
            </div>

            <TeamDetails
              class="team-details bottom-team"
              :teamLife="props.gameState.player.lifePoints"
              :ally="props.gameState.ally"
              :player="props.gameState.player"
            />

            <div class="hand bottom-hand-right">
              <hand :cards="props.gameState?.player?.hand" visibility="self" :selected-card="props.selectedCard"
              @card-selected="props.onCardSelected($event, 'Hand')"></hand>
            </div>
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