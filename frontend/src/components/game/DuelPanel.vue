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
    <div class="row top-row">
      <div class="col-2 player-container">
        <player-details :name="props.gameState?.opponent?.name" 
                        :life-points="props.gameState?.opponent?.lifePoints"
                        :draw-pile-count="props.gameState?.opponent?.drawPileCount"
                        :mindbug-count="props.gameState?.opponent?.mindbugCount">
        </player-details>
      </div>
      <div class="col-8">
        <hand :cards="props.gameState?.opponent?.hand" :opponent="true" :selected-card="props.selectedCard"></hand>
      </div>
      <div class="col-2 top-buttons">
        <button type="button" class="leave-button" @click="props.onLeaveButtonClick()">
          <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor" class="bi bi-door-open" viewBox="0 0 16 16">
            <path d="M8.5 10c-.276 0-.5-.448-.5-1s.224-1 .5-1 .5.448.5 1-.224 1-.5 1"></path>
            <path d="M10.828.122A.5.5 0 0 1 11 .5V1h.5A1.5 1.5 0 0 1 13 2.5V15h1.5a.5.5 0 0 1 0 1h-13a.5.5 0 0 1 0-1H3V1.5a.5.5 0 0 1 .43-.495l7-1a.5.5 0 0 1 .398.117M11.5 2H11v13h1V2.5a.5.5 0 0 0-.5-.5M4 1.934V15h6V1.077z"></path>
          </svg>
        </button>
      </div>
    </div>

    <board :game-state="props.gameState" 
           :selected-card="props.selectedCard" 
           :picked-card="props.pickedCard"
           :attacking-card="props.attackingCard" 
           @button-clicked="props.onActionButtonClick($event)"
           @card-selected="props.onCardSelected($event, 'Board')">
    </board>

    <div class="row bottom-row">
      <div class="col-2 player-container">
        <player-details :name="props.gameState?.player?.name" 
                        :life-points="props.gameState?.player?.lifePoints"
                        :draw-pile-count="props.gameState?.player?.drawPileCount"
                        :mindbug-count="props.gameState?.player?.mindbugCount">
        </player-details>
      </div>
      <div class="col-8">
        <hand :cards="props.gameState?.player?.hand" :opponent="false" :selected-card="props.selectedCard"
              @card-selected="props.onCardSelected($event, 'Hand')"></hand>
      </div>
      <div class="col-2"></div>
    </div>
    </div>
</template>

<style scoped>
/* Copier le style CSS depuis Game.vue pour .game, .top-row, .bottom-row, .player-container, .top-buttons, .leave-button */
.game {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  overflow: hidden;
  background-image: url("../../assets/playmats/default.png");
  background-repeat: no-repeat;
  background-size: cover;
}
.top-row {
  width: 100%;
  height: 10vh;
  display: flex;
  flex-wrap: nowrap;
  .player-container { align-items: start; }
}
.bottom-row {
  width: 100%;
  height: 20vh;
  display: flex;
  flex-wrap: nowrap;
  .player-container { align-items: end; }
}
.player-container { display: flex; }
.top-buttons { display: flex; justify-content: flex-end; }
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