<script setup lang="ts">
import Board from "@/components/game/board/Board.vue";
import Hand from "@/components/game/Hand.vue";
import PlayerDetails from "@/components/game/PlayerDetails.vue";
import {computed, onMounted, onUnmounted, Ref, ref} from "vue";
import {
  declareAttack,
  pickCard,
  playCard,
  resolveAttack,
  resolveBoolean,
  resolveMultipleTargetChoice,
  resolveSingleTargetChoice
} from "@/shared/RestService";
import ChoiceModal from "@/components/game/ChoiceModal.vue";
import {Store, useStore} from "vuex";

// Declare the interface for the data given by the parent component
interface Props {
  gameId: string;
}

const props = defineProps<Props>()

// VueX store to retrieve player data
const store: Store<AppState> = useStore()

// Reference for game state
const gameState: Ref<GameStateInterface> = ref(undefined);
// Reference that holds the current player ID
const currentPlayer: Ref<string> = ref(undefined);
// Reference for the currently selected card
const selectedCard: Ref<SelectedCardInterface> = ref(undefined);
// Reference for the currently picked card
const pickedCard: Ref<CardInterface> = ref(undefined);
// Reference for the currently attacking card
const attackingCard: Ref<CardInterface> = ref(undefined);

// Stores the WebSocket connection so it can be easily closed if necessary
let wsConnection: WebSocket;

// Computed value for choice modal data
const choiceModalData = computed((): ChoiceModalData => {
  const game = gameState.value

  if (game?.choice && game?.choice.playerToChoose === game?.player.uuid) {
    if (game?.choice.type === "TARGET") {
      const targetChoice = game.choice as TargetChoiceInterface
      return {
        type: "TARGET",
        count: targetChoice.targetsCount,
        cards: targetChoice.availableTargets,
        optional: targetChoice.optional
      }
    } else if (game?.choice.type === "SIMULTANEOUS") {
      return {
        type: "SIMULTANEOUS",
        count: 1,
        cards: (game.choice as SimultaneousChoiceInterface).availableEffects,
        optional: false
      }
    }
  }
})

// Computed value for choice modal visibility
const isChoiceModalVisible = computed(() => {
  const game: GameStateInterface = gameState.value;
  return game && !game.winner &&
      (game.choice?.type === "TARGET" || game.choice?.type === "SIMULTANEOUS") &&
      game.choice?.playerToChoose === game.player.uuid
})

onMounted(async () => {
  // Initializes the game WebSocket to update game state
  wsConnection = new WebSocket("ws://localhost:8080/ws/game/" + props.gameId + "?playerId=" + store.state.playerData.uuid);
  wsConnection.onmessage = (event: MessageEvent<string>) => {
    // Parse incoming data into a WsMessage
    const message: WsMessage = JSON.parse(event.data)

    // Apply a specific process depending on the event type
    switch (message.type) {
      case "NEW_TURN": // Received when a new turn starts
        selectedCard.value = undefined;
        pickedCard.value = undefined;
        attackingCard.value = undefined;
        break;
      case "CARD_PICKED": // Received when a player has picked a card
        selectedCard.value = undefined;
        pickedCard.value = message.state.card;
        attackingCard.value = undefined;
        break;
      case "CARD_PLAYED": // Received when a player has played a card
        selectedCard.value = undefined;
        pickedCard.value = undefined;
        attackingCard.value = undefined;
        break;
      case "ATTACK_DECLARED": // Received when a player declared an attack
        selectedCard.value = undefined;
        pickedCard.value = undefined;
        attackingCard.value = message.state.card;
        break;
      case "CHOICE": // Received when a choice needs to be solved
        if (message.state.choice?.type === "FRENZY") {
          attackingCard.value = undefined;
          selectedCard.value = undefined;
        }
        break;
      case "FINISHED": // Received when the game is finished
        wsConnection.close()
        break;
        //TODO Implement remaining cases
      case "STATE": // Received after joining the WebSocket
      case "LP_DOWN": // Received when a player has lost Life Points
      case "CARD_DESTROYED": // Received when a card is destroyed
      case "EFFECT_RESOLVED": // Received when an effect is successfully resolved
      case "WAITING_ATTACK_RESOLUTION": // Received when waiting for attack resolution
        break;
    }

    gameState.value = message.state;
    currentPlayer.value = gameState.value.playerTurn ? gameState.value.player.uuid : gameState.value.opponent.uuid;
  }
})

onUnmounted(() => {
  wsConnection.close(1000, "client left the game")
})

// Triggered when a card is selected on the board or in the hand
function onCardSelected(card: CardInterface, location: CardLocation): void {
  const game: GameStateInterface = gameState.value;

  if (location === "Board" || // No check required as board is able to manage it by itself
      (location === "Hand" && game?.playerTurn && !pickedCard.value && !attackingCard.value && !game.choice)) { // Check that we are at the start of the player turn
    if (selectedCard.value?.uuid === card.uuid) {
      selectedCard.value = undefined;
    } else {
      selectedCard.value = card as SelectedCardInterface;
      selectedCard.value.location = location;
    }
  }
}

// TODO Gérer différemment les différentes actions (c'est nul de se baser sur le label du bouton)
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
    case "Lose LP":
      return resolveAttack(game.uuid, undefined, undefined);
    case "Hunt target":
      return resolveSingleTargetChoice(game.uuid, selectedCard.value.uuid)
    case "Continue":
      return resolveSingleTargetChoice(game.uuid, "")
    case "Yes":
      return resolveBoolean(game.uuid, true)
    case "No":
      return resolveBoolean(game.uuid, false)
    default:
      // Unexpected value
  }
}

// Triggered when choice modal raises button-click event
async function onChoiceModalButtonClick(cards: CardInterface[]) {
  const game = gameState.value

  if (game.choice.type === "SIMULTANEOUS") {
    await resolveSingleTargetChoice(game.uuid, cards[0].uuid)
  } else if (game.choice.type === "TARGET") {
    await resolveMultipleTargetChoice(game.uuid, cards.map(card => card.uuid))
  }
}
</script>

<template>
  <div v-if="gameState" class="container-fluid game">
    <div class="row top-row">
      <div class="col-2 player-container">
        <player-details :name="gameState?.opponent?.name" :life-points="gameState?.opponent?.lifePoints"
                        :draw-pile-count="gameState?.opponent?.drawPileCount"
                        :mindbug-count="gameState?.opponent?.mindbugCount">
        </player-details>
      </div>
      <div class="col-8">
        <hand :cards="gameState?.opponent?.hand" :opponent=true :selected-card="selectedCard"></hand>
      </div>
      <div class="col-2 top-buttons">
        <button type="button" class="leave-button" @click="$router.push({name: 'Home'})">
          <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor" class="bi bi-door-open" viewBox="0 0 16 16">
            <path d="M8.5 10c-.276 0-.5-.448-.5-1s.224-1 .5-1 .5.448.5 1-.224 1-.5 1"></path>
            <path d="M10.828.122A.5.5 0 0 1 11 .5V1h.5A1.5 1.5 0 0 1 13 2.5V15h1.5a.5.5 0 0 1 0 1h-13a.5.5 0 0 1 0-1H3V1.5a.5.5 0 0 1 .43-.495l7-1a.5.5 0 0 1 .398.117M11.5 2H11v13h1V2.5a.5.5 0 0 0-.5-.5M4 1.934V15h6V1.077z"></path>
          </svg>
        </button>
      </div>
    </div>

    <board :game-state="gameState" :selected-card="selectedCard" :picked-card="pickedCard"
           :attacking-card="attackingCard" @button-clicked="onActionButtonClick($event)"
           @card-selected="onCardSelected($event, 'Board')">
    </board>

    <div class="row bottom-row">
      <div class="col-2 player-container">
        <player-details :name="gameState?.player?.name" :life-points="gameState?.player?.lifePoints"
                        :draw-pile-count="gameState?.player?.drawPileCount"
                        :mindbug-count="gameState?.player?.mindbugCount">
        </player-details>
      </div>
      <div class="col-8">
        <hand :cards="gameState?.player?.hand" :opponent=false :selected-card="selectedCard"
              @card-selected="onCardSelected($event, 'Hand')"></hand>
      </div>
      <div class="col-2"></div>
    </div>
  </div>
  <choice-modal v-if="isChoiceModalVisible" :choice="choiceModalData"
                @button-clicked="onChoiceModalButtonClick($event)"></choice-modal>
  <h2 v-if="!gameState">Loading...</h2>
</template>

<style scoped>
.game {
  display: flex;
  flex-direction: column;
  justify-content: space-between;

  background-image: url("../../assets/playmats/default.png");
  background-repeat: no-repeat;
  background-size: cover;
}

.top-row {
  width: 100%;
  height: 10vh;

  display: flex;
  flex-wrap: nowrap;

  .player-container {
    align-items: start;
  }
}

.bottom-row {
  width: 100%;
  height: 20vh;

  display: flex;
  flex-wrap: nowrap;

  .player-container {
    align-items: end;
  }
}

.player-container {
  display: flex;
}

.top-buttons {
  display: flex;
  justify-content: flex-end;
}

.leave-button {
  width: 2vw;
  height: 4vh;

  background-color: rgba(255, 255, 255, 0.5);

  border: none;
  border-radius: 25px;
  transition: background-color 0.3s, transform 0.2s;

  svg {
    min-width: 16px;
    min-height: 16px;
  }
}

.leave-button:hover {
  background-color: rgba(255, 255, 255, 0.9);
  transform: scale(1.05);
}

.leave-button:active {
  background-color: #1e6f93;
  transform: scale(0.98);
}
</style>
