<template>
  <nav>
    <router-link to="/">Home</router-link>
  </nav>
  <div class="container" style="width: 100%;height:100%">
    <div class="row" style="background-color: red;width: 100%;height:10%">
      <div class="col-2" style="background-color: orange">
        <player-details :name="gameState?.opponent?.name" :life-points="gameState?.opponent?.lifePoints"
                        :draw-pile-count="gameState?.opponent?.drawPileCount" :mindbug-count="gameState?.opponent?.mindbugCount">
        </player-details>
      </div>
      <div class="col-8" style="background-color: yellow">
        <hand :cards="gameState?.opponent?.hand" owner="Opponent" :selected-card="selectedCard"
              :player-turn="gameState?.playerTurn"></hand>
      </div>
      <div class="col-2" style="background-color: black"></div>
    </div>

    <board :player-board="gameState?.player?.board" :player-discard="gameState?.player?.discard"
           :opponent-board="gameState?.opponent?.board" :opponent-discard="gameState?.opponent?.discard"
           :selected-card="selectedCard" :picked-card="pickedCard" :attacking-card="attackingCard"
           :player-turn="gameState?.playerTurn"
           @button-clicked="onActionButtonClick($event)" @card-selected="onCardSelected($event, 'Board')">
    </board>

    <div class="row" style="background-color: red;width: 100%;height:10%">
      <div class="col-2" style="background-color: orange;height: 100%">
        <player-details :name="gameState?.player?.name" :life-points="gameState?.player?.lifePoints"
                        :draw-pile-count="gameState?.player?.drawPileCount"
                        :mindbug-count="gameState?.player?.mindbugCount">
        </player-details>
      </div>
      <div class="col-8" style="background-color: yellow;height: 100%">
        <hand :cards="gameState?.player?.hand" owner="Player" :selected-card="selectedCard"
              :player-turn="gameState?.playerTurn"
              @card-selected="onCardSelected($event, 'Hand')"></hand>
      </div>
      <div class="col-2" style="background-color: black;height: 100%"></div>
    </div>
  </div>
</template>


<script setup lang="ts">
import Board from "@/components/game/Board.vue";
import Hand from "@/components/game/Hand.vue";
import PlayerDetails from "@/components/game/PlayerDetails.vue";
import {onMounted, Ref, ref} from "vue";
import {declareAttack, pickCard, playCard, resolveAttack, startGame} from "@/shared/RestService";

let gameState: Ref<GameStateInterface> = ref({
  uuid: undefined,
  player: {
    uuid: undefined,
    name: undefined,
    lifePoints: undefined,
    mindbugCount: undefined,

    drawPileCount: undefined,
    hand: [],
    board: [],
    discard: [],
  },
  opponent: {
    uuid: undefined,
    name: undefined,
    lifePoints: undefined,
    mindbugCount: undefined,

    drawPileCount: undefined,
    hand: [],
    board: [],
    discard: [],
  },
  playerTurn: false,
  finished: true,
  card: undefined,
  choice: undefined
});

const currentPlayer: Ref<string> = ref(undefined);
const selectedCard: Ref<SelectedCardInterface> = ref(undefined);
const pickedCard: Ref<CardInterface> = ref(undefined);
const attackingCard: Ref<CardInterface> = ref(undefined);

onMounted(async () => {
  gameState.value = await startGame()
  currentPlayer.value = gameState.value.player.uuid
  gameState.value.playerTurn = true

  const connection = new WebSocket("ws://localhost:8080/ws/game/" + gameState.value.uuid + "?playerId=" + gameState.value.player.uuid);
  connection.onmessage = (event: MessageEvent<string>) => {
    const message: WsMessage = JSON.parse(event.data)

    switch (message.type) {
      case "ATTACK_DECLARED":
        selectedCard.value = undefined;
        attackingCard.value = message.state.card;
        break;
      case "CARD_PICKED":
        selectedCard.value = undefined;
        pickedCard.value = message.state.card;
        break;
      case "CARD_PLAYED":
        pickedCard.value = undefined;
        break;
      case "NEW_TURN":
        selectedCard.value = undefined;
        pickedCard.value = undefined;
        attackingCard.value = undefined;
        break;
      case "FINISHED":
        alert("Game finished!")
        break;
        //TODO Implement remaining cases
      case "LP_DOWN":
        break;
      case "CARD_DESTROYED":
        break;
      case "EFFECT_RESOLVED":
        break;
      case "CHOICE":
        //TODO Trouver un moyen d'afficher le choix (éventuellement directement dans l'interface, sinon dans une popup)
        break;
    }

    gameState.value = message.state;
    currentPlayer.value = gameState.value.playerTurn ? gameState.value.player.uuid : gameState.value.opponent.uuid;
  }

  //TODO Enlever les modifications inutiles quand le back sera raccord

  // Update opponent hand so hidden is true
  gameState.value?.opponent?.hand?.forEach(card => {
    card.id = undefined;
    card.name = undefined;
  });
})

function onCardSelected(card: CardInterface, location: CardLocation): void {
  const game: GameStateInterface = gameState.value;

  if (location === "Board" || (location === "Hand" && game?.playerTurn && attackingCard.value === undefined)) {
    if (selectedCard.value?.uuid === card.uuid) {
      selectedCard.value = undefined;
    } else {
      selectedCard.value = card as SelectedCardInterface;
      selectedCard.value.location = location;
    }
  }
}

function onActionButtonClick(actionLabel: string) {
  const game: GameStateInterface = gameState.value;

  //TODO Gérer les cas d'erreur
  switch (actionLabel) {
    case "Play":
      return pickCard(game.uuid, selectedCard.value.uuid);
    case "Use Mindbug":
      return playCard(game.uuid, game.player.uuid);
    case "No Mindbug":
      return playCard(game.uuid, undefined);
    case "Attack":
      return declareAttack(game.uuid, selectedCard.value.uuid);
    case "Block":
      return resolveAttack(game.uuid, game.player.uuid, selectedCard.value.uuid);
    default:
      // Unexpected value
  }
}
</script>


<style scoped>

</style>
