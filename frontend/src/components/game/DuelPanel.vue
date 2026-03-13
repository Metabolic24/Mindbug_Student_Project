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
  onCardPreview: ((args: any[]) => void) | undefined; 
}

const props = defineProps<Props>();
</script>

<template>
  <div class="container-fluid game">
    <div class="row top-row">
      <div class="col-2 player-container player-container--top">
        <player-details :name="gameState?.opponents[0]?.name" :life-points="gameState?.opponents[0]?.lifePoints"
                        :draw-pile-count="gameState?.opponents[0]?.drawPileCount"
                        :mindbug-count="gameState?.opponents[0]?.mindbugCount"
                        :is-active="!isPlayerActive">
        </player-details>
      </div>
      <div class="col-8">
        <hand
          :cards="gameState?.opponents[0]?.hand"
          :opponent=true
          :selected-card="selectedCard"
          @card-preview="onCardPreview"
        ></hand>
      </div>
    </div>

    <div class="board-wrapper">
      <board :game-state="props.gameState" 
            :selected-card="props.selectedCard" 
            :picked-card="props.pickedCard"
            :attacking-card="props.attackingCard" 
            @button-clicked="props.onActionButtonClick($event)"
            @card-selected="props.onCardSelected($event, 'Board')"
            @card-preview="onCardPreview">
      </board>
    </div>

    <div class="row bottom-row">
      <div class="col-2 player-container player-container--bottom">
        <player-details :name="props.gameState?.player?.name" 
                        :life-points="props.gameState?.player?.lifePoints"
                        :draw-pile-count="props.gameState?.player?.drawPileCount"
                        :mindbug-count="props.gameState?.player?.mindbugCount"
                        :is-active="isPlayerActive">
        </player-details>
      </div>
      <div class="col-8">
        <hand :cards="props.gameState?.player?.hand" :opponent="false" :selected-card="props.selectedCard"
              @card-selected="props.onCardSelected($event, 'Hand')"
              @card-preview="onCardPreview">
        </hand>
      </div>
      <div class="col-2"></div>
    </div>
    </div>
</template>

<style scoped>
.game {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  overflow: hidden;
  height: 100vh;
  position: relative;

  background-image: url("../../assets/playmats/default.png");
  background-repeat: no-repeat;
  background-size: cover;
  background-position: center;
}

.top-row {
  width: 100%;
  height: 14vh;

  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: start;
  column-gap: 16px;
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  padding: 8px 8px 0;
  pointer-events: none;
  z-index: 5;

  .player-container {
    align-items: start;
  }
}

.bottom-row {
  width: 100%;
  height: max(20vh, 12vw);

  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: end;
  column-gap: 16px;
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 0 8px 8px;
  pointer-events: none;
  z-index: 5;

  .player-container {
    align-items: end;
  }
}

.top-row .player-container,
.top-row .top-buttons,
.top-row .hand,
.bottom-row .player-container,
.bottom-row .hand {
  pointer-events: auto;
}

.top-row > .col-2,
.top-row > .col-8,
.bottom-row > .col-2,
.bottom-row > .col-8 {
  width: auto;
  max-width: none;
}

.top-row > .col-8,
.bottom-row > .col-8 {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  display: flex;
  justify-content: center;
}

.board-wrapper {
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  display: flex;
  padding-top: 14vh;
  padding-bottom: max(20vh, 12vw);
  box-sizing: border-box;
}

.player-container {
  display: flex;
}

.top-buttons {
  position: absolute;
  top: 15px;
  right: -10px;
  display: flex;
  justify-content: flex-end;
  pointer-events: auto;
  z-index: 6;
}

.leave-button {
  width: 2vw;
  height: 4vh;
  background-color: rgba(255, 255, 255, 0.5);
  border: none;
  border-radius: 25px;
  transition: background-color 0.3s, transform 0.2s;
}
.leave-button:hover { background-color: rgba(255, 255, 255, 0.9); transform: scale(1.05); }
.leave-button:active { background-color: #1e6f93; transform: scale(0.98); }
</style>