<template>
  <nav>
    <router-link to="/">Home</router-link>
  </nav>
  <div class="container" style="width: 100%;height:100%">
    <div class="row" style="background-color: red;width: 100%;height:10%">
      <div class="col-2" style="background-color: orange">
        <player-details :name="gameState?.opponent?.name" :life-points="gameState?.opponent?.lifePoints"
                        :draw-pile-count="gameState?.opponent?.drawPileCount"
                        :mindbug-count="gameState?.opponent?.mindbugCount">
        </player-details>
      </div>
      <div class="col-8" style="background-color: yellow">
        <hand :cards="gameState?.opponent?.hand" owner="Opponent" :selected-card="selectedCard"
              :player-turn="gameState?.playerTurn"></hand>
      </div>
      <div class="col-2" style="background-color: black"></div>
    </div>

    <board :game-state="gameState" :selected-card="selectedCard" :picked-card="pickedCard"
           :attacking-card="attackingCard" @button-clicked="onActionButtonClick($event)"
           @card-selected="onCardSelected($event, 'Board')">
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
  <choice-modal v-if="isModalVisible" :choice="modalData"
                @button-clicked="onChoiceModalButtonClick($event)"></choice-modal>
</template>


<script setup lang="ts">
import Board from "@/components/game/Board.vue";
import Hand from "@/components/game/Hand.vue";
import PlayerDetails from "@/components/game/PlayerDetails.vue";
import {computed, onMounted, Ref, ref} from "vue";
import {
  declareAttack,
  pickCard,
  playCard,
  resolveAttack,
  resolveBoolean,
  resolveMultipleTargetChoice,
  resolveSingleTargetChoice,
  startGame
} from "@/shared/RestService";
import ChoiceModal from "@/components/game/ChoiceModal.vue";

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

const modalData = computed((): ChoiceModalData => {
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
    }
  } else if (game?.choice.type === "SIMULTANEOUS") {
    return {
      type: "SIMULTANEOUS",
      count: 1,
      cards: (game.choice as SimultaneousChoiceInterface).availableEffects,
      optional: false
    }
  }
})

const isModalVisible = computed(() => {
  return gameState.value?.choice?.type === "TARGET" || gameState.value?.choice?.type === "SIMULTANEOUS"
})

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
      case "CHOICE":
        if (message.state.choice?.type === "FRENZY") {
          attackingCard.value = undefined;
        } else if (message.state.choice?.type === "TARGET" || message.state.choice?.type === "SIMULTANEOUS") {
        }
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
      case "CARD_DESTROYED":
      case "EFFECT_RESOLVED":
      case "WAITING_ATTACK_RESOLUTION":
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

  if (location === "Board" || // No check required as board is able to manage it by itself
      (location === "Hand" && game?.playerTurn && !attackingCard.value && !game.choice)) { // Check that we are at the start of a turn
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


<style scoped>

</style>
