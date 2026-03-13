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
}

const props = defineProps<Props>();
</script>

<template>
    <div class="container-fluid game">
        TEAM
    </div>
</template>

<style scoped>
.game {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  overflow: hidden;
  background-image: url("../../assets/playmats/default.png");
  background-repeat: no-repeat;
  background-size: cover;
  width: 100%;
  height: 100vh;
}
.leave-button {
  width: 2vw;
  height: 4vh;
  background-color: rgba(255, 255, 255, 0.5);
  border: none;
  border-radius: 25px;
  transition: background-color 0.3s, transform 0.2s;
}
</style>