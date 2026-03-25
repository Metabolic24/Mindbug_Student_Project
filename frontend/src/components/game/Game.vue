<script setup lang="ts">
import Board from "@/components/game/board/Board.vue";
import Hand from "@/components/game/Hand.vue";
import PlayerDetails from "@/components/game/PlayerDetails.vue";
import CardPreviewModal from "@/components/game/CardPreviewModal.vue";
import {computed, onMounted, onUnmounted, Ref, ref} from "vue";
import {
  declareAttack,
  pickCard,
  playCard,
  resolveAction,
  resolveAttack,
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

const hasDefenseDecision = computed(() => {
  return !!(pickedCard.value || attackingCard.value);
})

const isPlayerActive = computed(() => {
  const game: GameStateInterface = gameState.value;
  if (!game) return false;
  if (game.choice) return game.choice.playerToChoose === game.player.uuid;
  if (hasDefenseDecision.value) return !game.playerTurn;
  return game.playerTurn;
})

const isOpponentActive = computed(() => {
  const game: GameStateInterface = gameState.value;
  if (!game) return false;
  if (game.choice) return game.choice.playerToChoose === game.opponent.uuid;
  if (hasDefenseDecision.value) return game.playerTurn;
  return !game.playerTurn;
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
  if (!game?.winner) {
    surrender(game?.uuid, game?.player.uuid)
  }
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

function onActionButtonClick(buttonEvent: BoardButtonsEvent) {
  const game: GameStateInterface = gameState.value;

  switch (buttonEvent) {
    case "PLAY":
      return pickCard(game.uuid, selectedCard.value.uuid);
    case "MINDBUG":
      return playCard(game.uuid, game.player.uuid);
    case "NO_MINDBUG":
      return playCard(game.uuid, undefined);
    case "ATTACK":
      return declareAttack(game.uuid, selectedCard.value.uuid);
    case "BLOCK":
      return resolveAttack(game.uuid, game.player.uuid, selectedCard.value.uuid);
    case "ACTION":
      return resolveAction(game.uuid, selectedCard.value.uuid);
    case "LOSE_LP":
      return resolveAttack(game.uuid, undefined, undefined);
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
  if (!game?.winner) {
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
  <div v-if="gameState" class="container-fluid game">
    <div class="row top-row">
      <div class="col-2 player-container player-container--top">
        <player-details :name="gameState?.opponent?.name" :life-points="gameState?.opponent?.lifePoints"
                        :draw-pile-count="gameState?.opponent?.drawPileCount"
                        :mindbug-count="gameState?.opponent?.mindbugCount"
                        :is-active="isOpponentActive">
        </player-details>
      </div>
      <div class="col-8">
        <hand
          :cards="gameState?.opponent?.hand"
          :opponent=true
          :selected-card="selectedCard"
          @card-preview="onCardPreview"
        ></hand>
      </div>
      <div class="col-2 top-buttons">
        <button type="button" class="settings-button" @click="onSettingsButtonClick()"
                :title="t('game.settings_tooltip')">
          <svg xmlns="http://www.w3.org/2000/svg" x="0px" y="0px" viewBox="0 0 122.88 122.878" xml:space="preserve"
               fill="currentColor">
            <g>
              <path
                  fill-rule="evenodd"
                  clip-rule="evenodd"
                  d="M101.589,14.7l8.818,8.819c2.321,2.321,2.321,6.118,0,8.439l-7.101,7.101 c1.959,3.658,3.454,7.601,4.405,11.752h9.199c3.283,0,5.969,2.686,5.969,5.968V69.25c0,3.283-2.686,5.969-5.969,5.969h-10.039 c-1.231,4.063-2.992,7.896-5.204,11.418l6.512,6.51c2.321,2.323,2.321,6.12,0,8.44l-8.818,8.819c-2.321,2.32-6.119,2.32-8.439,0 l-7.102-7.102c-3.657,1.96-7.601,3.456-11.753,4.406v9.199c0,3.282-2.685,5.968-5.968,5.968H53.629 c-3.283,0-5.969-2.686-5.969-5.968v-10.039c-4.063-1.232-7.896-2.993-11.417-5.205l-6.511,6.512c-2.323,2.321-6.12,2.321-8.441,0 l-8.818-8.818c-2.321-2.321-2.321-6.118,0-8.439l7.102-7.102c-1.96-3.657-3.456-7.6-4.405-11.751H5.968 C2.686,72.067,0,69.382,0,66.099V53.628c0-3.283,2.686-5.968,5.968-5.968h10.039c1.232-4.063,2.993-7.896,5.204-11.418l-6.511-6.51 c-2.321-2.322-2.321-6.12,0-8.44l8.819-8.819c2.321-2.321,6.118-2.321,8.439,0l7.101,7.101c3.658-1.96,7.601-3.456,11.753-4.406 V5.969C50.812,2.686,53.498,0,56.78,0h12.471c3.282,0,5.968,2.686,5.968,5.969v10.036c4.064,1.231,7.898,2.992,11.422,5.204 l6.507-6.509C95.471,12.379,99.268,12.379,101.589,14.7L101.589,14.7z M61.44,36.92c13.54,0,24.519,10.98,24.519,24.519 c0,13.538-10.979,24.519-24.519,24.519c-13.539,0-24.519-10.98-24.519-24.519C36.921,47.9,47.901,36.92,61.44,36.92L61.44,36.92z"
              />
            </g>
          </svg>
        </button>
      </div>
    </div>

    <div v-if="displaySettingsMenu" class="settings-menu-backdrop" @click="continueGame()">
      <div class="settings-menu" @click.stop>
        <h2>{{ t('modal.game.settings.title') }}</h2>
        <button @click="continueGame()">{{ t('modal.game.settings.continue') }}</button>
        <button class="leave" @click="leaveGame()">{{ t('modal.game.settings.leave') }}</button>
      </div>
    </div>

    <div class="board-wrapper">
      <board :game-state="gameState" :selected-card="selectedCard" :picked-card="pickedCard"
             :attacking-card="attackingCard" @button-clicked="onActionButtonClick($event)"
             @card-selected="onCardSelected($event, 'Board')"
             @card-preview="onCardPreview">
      </board>
    </div>

    <div class="row bottom-row">
      <div class="col-2 player-container player-container--bottom">
        <player-details :name="gameState?.player?.name" :life-points="gameState?.player?.lifePoints"
                        :draw-pile-count="gameState?.player?.drawPileCount"
                        :mindbug-count="gameState?.player?.mindbugCount"
                        :is-active="isPlayerActive">
        </player-details>
      </div>
      <div class="col-8">
        <hand
          :cards="gameState?.player?.hand"
          :opponent=false
          :selected-card="selectedCard"
          @card-selected="onCardSelected($event, 'Hand')"
          @card-preview="onCardPreview"
        ></hand>
      </div>
      <div class="col-2"></div>
    </div>
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

.settings-menu-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
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

.settings-button {
  width: 44px;
  height: 44px;

  display: inline-flex;
  align-items: center;
  justify-content: center;

  background-color: rgba(255, 255, 255, 0.5);

  border: none;
  border-radius: 25px;
  transition: background-color 0.3s, transform 0.2s;

  svg {
    width: 20px;
    height: 20px;
  }
}

.settings-button:hover {
  background-color: rgba(255, 255, 255, 0.9);
  transform: scale(1.05);
}

.settings-button:active {
  background-color: #1e6f93;
  transform: scale(0.98);
}

/* Page d'erreur et de chargement plein écran */
.error-page, .loading-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #1a1a1a; /* Couleur sombre pour rester dans le thème */
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

/* Petit spinner pour le chargement */
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
