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
  return gameState.value?.choice?.type === "TARGET" || gameState.value?.choice?.type === "SIMULTANEOUS"
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
        }
        break;
      case "FINISHED": // Received when the game is finished
        alert("Game finished!")
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
  wsConnection.close(1001, "client left the game")
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
  <nav>
    <router-link to="/">Home</router-link>
  </nav>
  <div v-if="gameState" class="container" style="width: 100%;height:100%">
    <div class="row" style="width: 100%;height:10%">
      <div class="col-2">
        <player-details :name="gameState?.opponent?.name" :life-points="gameState?.opponent?.lifePoints"
                        :draw-pile-count="gameState?.opponent?.drawPileCount"
                        :mindbug-count="gameState?.opponent?.mindbugCount">
        </player-details>
      </div>
      <div class="col-8">
        <hand :cards="gameState?.opponent?.hand" :opponent=true :selected-card="selectedCard"></hand>
      </div>
      <div class="col-2"></div>
    </div>

    <board :game-state="gameState" :selected-card="selectedCard" :picked-card="pickedCard"
           :attacking-card="attackingCard" @button-clicked="onActionButtonClick($event)"
           @card-selected="onCardSelected($event, 'Board')">
    </board>

    <div class="row" style="width: 100%;height:10%">
      <div class="col-2" style="height: 100%">
        <player-details :name="gameState?.player?.name" :life-points="gameState?.player?.lifePoints"
                        :draw-pile-count="gameState?.player?.drawPileCount"
                        :mindbug-count="gameState?.player?.mindbugCount">
        </player-details>
      </div>
      <div class="col-8" style="height: 100%">
        <hand :cards="gameState?.player?.hand" :opponent=false :selected-card="selectedCard"
              @card-selected="onCardSelected($event, 'Hand')"></hand>
      </div>
      <div class="col-2" style="height: 100%"></div>
    </div>
  </div>
  <choice-modal v-if="isChoiceModalVisible" :choice="choiceModalData"
                @button-clicked="onChoiceModalButtonClick($event)"></choice-modal>
  <h2 v-if="!gameState">Loading...</h2>
</template>

<style scoped>

</style>
