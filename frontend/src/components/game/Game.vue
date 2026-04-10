<script setup lang="ts">
import Board from "@/components/game/board/Board.vue";
import Hand from "@/components/game/Hand.vue";
import PlayerDetails from "@/components/game/PlayerDetails.vue";
import CardPreviewModal from "@/components/game/CardPreviewModal.vue";
import {computed, onMounted, onUnmounted, Ref, ref} from "vue";
import DuelPanel from "./DuelPanel.vue";
import TeamPanel from "./TeamPanel.vue";
import {
  declareAttack,
  pickCard,
  resolveAction,
  resolveBoolean,
  resolveMultipleTargetChoice,
  resolveSingleTargetChoice,
  surrender
} from "@/shared/RestService";
import ChoiceModal from "@/components/game/ChoiceModal.vue";
import {Store, useStore} from "vuex";
import {useRouter} from "vue-router";
import {useI18n} from "vue-i18n";

const {t} = useI18n();


// Declare the interface for the data given by the parent component
interface Props {
  gameId: string;
  mode: string;
}

const props = defineProps<Props>()

const router = useRouter()

// VueX store to retrieve player data
const store: Store<AppState> = useStore()

// Reference for game error
const error = ref(false);
// References for the loading page
let loadingTimer: number;
// After 10s of waiting, the user can go back to the main menu
const showRetry = ref(false);

// Reference for settings menu visibility
const displaySettingsMenu: Ref<boolean> = ref(false);

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
const previewCard: Ref<CardInterface> = ref(undefined);

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
        cards: (game.choice as SimultaneousEffectsChoiceInterface).availableEffects,
        optional: false
      }
    }
  }
})

// Computed value for choice modal visibility
const isChoiceModalVisible = computed(() => {
  const game: GameStateInterface = gameState.value;
  return game && !game.winners &&
      (game.choice?.type === "TARGET" || game.choice?.type === "SIMULTANEOUS") &&
      game.choice?.playerToChoose === game.player.uuid
})

const hasDefenseDecision = computed(() => {
  return !!(pickedCard.value || attackingCard.value);
})

const isPlayerActive = computed(() => {
  const game: GameStateInterface = gameState.value;
  if (!game) return false;
  if (game.choice) return game.choice.playerToChoose === game.player.uuid;

  const isCurrentPlayerTurn = game.currentPlayerID === game.player.uuid
  if (hasDefenseDecision.value) return !isCurrentPlayerTurn;
  return isCurrentPlayerTurn;
})

onMounted(async () => {
  // Initializes the game WebSocket to update game state
  try {
    wsConnection = new WebSocket("ws://localhost:8080/ws/game/" + props.gameId + "?playerId=" + store.state.playerData.uuid);
  } catch (e) {
    error.value = true;

    // if error, do not continue to avoid wsConnection errors
    return;
  }

  // Launch the countdown if the server is taking too long to respond
  loadingTimer = window.setTimeout(() => {
    if (!gameState.value) {
      showRetry.value = true;
    }
  }, 10000);

  wsConnection.onmessage = (event: MessageEvent<string>) => {
    try {
      // if a game is found, cancel the timer to avoid any bug
      if (gameState.value) {
        clearTimeout(loadingTimer);
        showRetry.value = false;
      }

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
          pickedCard.value = message.state.choice?.sourceCard;
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
          attackingCard.value = undefined;
          break;
        case "CHOICE": // Received when a choice needs to be solved
          if (message.state.choice?.type === "FRENZY") {
            attackingCard.value = undefined;
            selectedCard.value = undefined;
          }
          if (message.state.choice?.type === "BOOLEAN") {
            pickedCard.value = message.state.card;
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
          attackingCard.value = message.state.choice?.sourceCard;
          break;
      }

      gameState.value = message.state;
      currentPlayer.value = gameState.value.currentPlayerID;
    } catch (e) {
      console.error("Failed to parse game state", e);
      error.value = true;
    }
  }

  wsConnection.onerror = () => {
    error.value = true;
  };
})

onUnmounted(() => {
  clearTimeout(loadingTimer);
  if (wsConnection) {
    wsConnection.close(1000, "client left the game");
  }

  const game: GameStateInterface = gameState.value;
  if (!game?.winners) {
    surrender(game?.uuid, game?.player.uuid)
  }
})

// Triggered when a card is selected on the board or in the hand
function onCardSelected(card: CardInterface, location: CardLocation): void {
  const game: GameStateInterface = gameState.value;

  if (location === "Board" || // No check required as board is able to manage it by itself
      (location === "Hand" && game?.currentPlayerID === game?.player.uuid && !pickedCard.value && !attackingCard.value && !game.choice)) { // Check that we are at the start of the player turn
    if (selectedCard.value?.uuid === card.uuid) {
      selectedCard.value = undefined;
    } else {
      selectedCard.value = card as SelectedCardInterface;
      selectedCard.value.location = location;
    }
  }
}

function onActionButtonClick(buttonEvent: BoardButtonsEvent) {
  const game: GameStateInterface = gameState.value;

  switch (buttonEvent) {
    case "PLAY":
      return pickCard(game.uuid, selectedCard.value.uuid);
    case "MINDBUG":
      return resolveBoolean(game.uuid, true);
    case "NO_MINDBUG":
      return resolveBoolean(game.uuid, false);
    case "ATTACK":
      return declareAttack(game.uuid, selectedCard.value.uuid);
    case "BLOCK":
      return resolveSingleTargetChoice(game.uuid, selectedCard.value.uuid);
    case "ACTION":
      return resolveAction(game.uuid, selectedCard.value.uuid);
    case "LOSE_LP":
      return resolveSingleTargetChoice(game.uuid, undefined);
    case "HUNT":
      return resolveSingleTargetChoice(game.uuid, selectedCard.value.uuid)
    case "CONTINUE":
      return resolveSingleTargetChoice(game.uuid, "")
    case "YES":
      return resolveBoolean(game.uuid, true)
    case "NO":
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

function onSettingsButtonClick() {
  displaySettingsMenu.value = true;
}

function continueGame() {
  displaySettingsMenu.value = false;
}

async function leaveGame() {
  const game = gameState.value
  if (!game?.winners) {
    await surrender(game?.uuid, game?.player.uuid)
  }

  displaySettingsMenu.value = false;
  await router.push({name: t("router.home")})
}

function onCardPreview(card: CardInterface): void {
  previewCard.value = card
}

function closeCardPreview(): void {
  previewCard.value = undefined
}

</script>

<template>
  <div v-if="gameState">
    
    <div v-if="displaySettingsMenu" class="settings-menu-backdrop" @click="continueGame()">
      <div class="settings-menu" @click.stop>
        <h2>{{ t('modal.game.settings.title') }}</h2>
        <button @click="continueGame()">{{ t('modal.game.settings.continue') }}</button>
        <button class="leave" @click="leaveGame()">{{ t('modal.game.settings.leave') }}</button>
      </div>
    </div>

    <DuelPanel v-if="props.mode === 'duel'"
               :game-state="gameState"
               :selected-card="selectedCard"
               :picked-card="pickedCard"
               :attacking-card="attackingCard"
               :is-player-active="isPlayerActive"
               :settings-tooltip="t('game.settings_tooltip')"
               :on-card-selected="onCardSelected"
               :on-action-button-click="onActionButtonClick"
               :on-settings-button-click="onSettingsButtonClick"
               :on-card-preview="onCardPreview"
                />


    <TeamPanel v-if="props.mode === 'team'" 
               :game-state="gameState"
               :selected-card="selectedCard"
               :picked-card="pickedCard"
               :attacking-card="attackingCard"
               :on-card-selected="onCardSelected"
               :on-action-button-click="onActionButtonClick"
               :on-settings-button-click="onSettingsButtonClick"
               :on-card-preview="onCardPreview"
               />
  </div>

  <div v-else-if="error" class="error-page">
    <div class="error-container">
      <div v-html="t('game.error.not_found.label')"></div>
      <button class="btn-back" @click="router.push({name: t('router.home')})">
        {{ t('game.error.not_found.button') }}
      </button>
    </div>
  </div>

  <div v-else class="loading-page">
    <div class="loader"></div>
    <p>{{ t('game.loading') }}</p>

    <div v-if="showRetry" class="retry-section">
      <p class="small">{{ t('game.error.loading_failure.label') }}</p>
      <button class="btn-back" @click="router.push({name: t('router.home')})">
        {{ t('game.error.loading_failure.button') }}
      </button>
    </div>
  </div>
  <choice-modal v-if="isChoiceModalVisible" :choice="choiceModalData"
                @button-clicked="onChoiceModalButtonClick($event)"></choice-modal>
  <card-preview-modal v-if="previewCard" :card="previewCard" @close="closeCardPreview"></card-preview-modal>
</template>

<style scoped>
.settings-menu-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.settings-menu {
  background: rgba(15, 23, 42, 0.95);
  padding: 2rem 3rem;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  align-items: center;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.5);

  h2 {
    margin-bottom: 0.5rem;
  }

  button {
    min-width: 160px;
    padding: 0.5rem 1.5rem;
    margin-top: 0.25rem;
    border-radius: 999px;
    border: none;
    cursor: pointer;
    font-weight: 600;
    background-color: #1e6f93;
    color: white;
    transition: background 0.2s, transform 0.1s;
  }

  button.leave {
    background-color: #b91c1c;
  }

  button:hover {
    background-color: #2980b9;
    transform: translateY(-1px);
  }

  button.leave:hover {
    background-color: #dc2626;
  }

  button:active {
    transform: translateY(0);
  }
}


/* Error's page and of full screan loading */
.error-page, .loading-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #1a1a1a;
  color: white;
  text-align: center;
}

.btn-back {
  margin-top: 2rem;
  padding: 10px 25px;
  background-color: #1e6f93;
  border: none;
  color: white;
  border-radius: 5px;
  cursor: pointer;
  transition: background 0.3s;
}

.btn-back:hover {
  background-color: #2980b9;
}

.loader {
  border: 4px solid rgba(255, 255, 255, 0.1);
  border-left-color: #1e6f93;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>

<style>
.error-container h1 {
  font-size: 4rem;
  color: #ff4757;
  margin-bottom: 1rem;
}
</style>
