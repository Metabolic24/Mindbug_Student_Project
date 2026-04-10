<script setup lang="ts">
import Card from "@/components/game/Card.vue";
import TurnWheel from "./TurnWheel.vue";
import { computed, ref } from "vue";

const wheelRef = ref();
const activeKey = computed(() => wheelRef.value?.activeInfo.key);
const isChoice  = computed(() => wheelRef.value?.activeInfo.isChoice);

interface Props {
  gameState: GameStateInterface
  selectedCard: SelectedCardInterface
  attackingCard: CardInterface
  onCardSelected: (card: CardInterface) => void
}

const emit = defineEmits(['button-clicked', 'card-selected', 'card-preview'])

function onCardSelected(card: CardInterface): void {
  if ((props.gameState?.currentPlayerID === props.gameState?.player.uuid && !props.attackingCard && card.ableToAttack) || // Attack case
      (!(props.gameState?.currentPlayerID === props.gameState?.player.uuid) && card.ableToBlock && // Block case
          (props.attackingCard.keywords?.includes("SNEAKY") && card.keywords?.includes("SNEAKY") ||
              !props.attackingCard.keywords?.includes("SNEAKY")))) {
    emit('card-selected', card)
  }
}

function onOpponentCardSelected(card: CardInterface): void {
  if (props.gameState?.currentPlayerID === props.gameState?.player.uuid && props.gameState?.choice?.type === "HUNTER" && card.ownerId!=props.gameState?.ally.uuid) { // Hunter case
    emit('card-selected', card)
  }
}

const props = defineProps<Props>()
</script>

<template>
    <div class="board-team">

  <!-- PLAYER'S ZONE -->

  <div class="player-zone player-1"
     :class="{ 'active-turn': activeKey === 'opp1' && !isChoice,
               'active-choice': activeKey === 'opp1' && isChoice }">
    <Card
        v-for="card in props.gameState.opponents[0].board"
        :key="card.uuid"
        :card="card"
        context="opponent-board"
        visibility="enemy"
        :selected="card.uuid === props.selectedCard?.uuid"
        :attacking="card.uuid === props.attackingCard?.uuid"
        :clickable="true"
        @click="onOpponentCardSelected"
        @preview="emit('card-preview', $event)"
      />
  </div>

  <div class="player-zone player-2"
     :class="{ 'active-turn': activeKey === 'opp2' && !isChoice,
               'active-choice': activeKey === 'opp2' && isChoice }">
    <Card
        v-for="card in props.gameState.opponents[1].board"
        :key="card.uuid"
        :card="card"
        context="opponent-board"
        visibility="enemy"
        :selected="card.uuid === props.selectedCard?.uuid"
        :attacking="card.uuid === props.attackingCard?.uuid"
        :clickable="true"
        @click="onOpponentCardSelected"
        @preview="emit('card-preview', $event)"
      />
  </div>

  <div class="player-zone player-3"
     :class="{ 'active-turn': activeKey === 'ally' && !isChoice,
               'active-choice': activeKey === 'ally' && isChoice }">
    <Card
        v-for="card in props.gameState.ally.board"
        :key="card.uuid"
        :card="card"
        context="player-board"
        visibility="ally"
        :selected="card.uuid === props.selectedCard?.uuid"
        :attacking="card.uuid === props.attackingCard?.uuid"
        :clickable="true"
        @click="onOpponentCardSelected"
        @preview="emit('card-preview', $event)"
      />
  </div>

  <div class="player-zone player-4"
     :class="{ 'active-turn': activeKey === 'player' && !isChoice,
               'active-choice': activeKey === 'player' && isChoice }">
    <Card
        v-for="card in props.gameState.player.board"
        :key="card.uuid"
        :card="card"
        context="player-board"
        visibility="self"
        :selected="card.uuid === props.selectedCard?.uuid"
        :attacking="card.uuid === props.attackingCard?.uuid"
        :clickable="true"
        @click="onCardSelected"
        @preview="emit('card-preview', $event)"
      />
  </div>

  <!-- WHEEL TURN -->

  <div class="turn-wheel">
    <TurnWheel ref="wheelRef" :game-state="props.gameState" />
  </div>

</div>
</template>

<style scoped>

.board-team {

  position: relative;

  width: 100%;
  height: 100%;

  display: grid;

  grid-template-columns: 1fr 1fr;
  grid-template-rows: 1fr 1fr;

}

/* PLAYER'S ZONE */

.player-zone.active-turn {
  box-shadow: inset 0 0 40px rgba(255, 200, 50, 0.15),
              0 0 0 2px rgba(255, 200, 50, 0.6);
  border-color: rgba(255, 200, 50, 0.7) !important;
}
.player-zone.active-choice {
  box-shadow: inset 0 0 40px rgba(120, 180, 255, 0.15),
              0 0 0 2px rgba(120, 180, 255, 0.7);
  border-color: rgba(120, 180, 255, 0.7) !important;
}

.player-zone {

  display: flex;
  justify-content: center;
  align-items: center;

  border: 2px solid rgba(255,255,255,0.2);
  transition: box-shadow 0.4s ease, border-color 0.4s ease;
}

.player-1 { grid-area: 1 / 1 }
.player-2 { grid-area: 1 / 2 }
.player-3 { grid-area: 2 / 1 }
.player-4 { grid-area: 2 / 2 }

/* WHEEL TURN */

.turn-wheel {

  position: absolute;

  top: 50%;
  left: 50%;

  transform: translate(-50%, -50%);

  z-index: 10;

  pointer-events: none;

}

.wheel {

  width: 120px;
  height: 120px;

  border-radius: 50%;

  background: rgba(0,0,0,0.7);

  display: flex;
  justify-content: center;
  align-items: center;

  color: white;
  font-size: 30px;
  font-weight: bold;
  
  pointer-events: auto;
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
