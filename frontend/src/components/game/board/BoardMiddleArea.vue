<script setup lang="ts">
import {computed} from "vue";
import Card from "../Card.vue";
import {useI18n} from "vue-i18n";

const { t } = useI18n();

// Declare the interface for the data given by the parent component
interface Props {
  gameState: GameStateInterface

  pickedCard: CardInterface
  attackingCard: CardInterface
}
const props = defineProps<Props>()
const emit = defineEmits(['card-preview'])

// Computed value for the message
const message = computed(() => {
  if (props.gameState.winners) {
    if (props.gameState.winners.includes(props.gameState.player.uuid)) {
      return 'game.middle_area.win'
    } else {
      return 'game.middle_area.lose'
    }
  } else if (props.gameState?.choice) {
    if (props.gameState.choice.playerToChoose === props.gameState.player.uuid) {
      if (props.gameState.choice.type === "FRENZY" || props.gameState.choice.type === "HUNTER") {
        return 'game.middle_area.choice.' + props.gameState.choice.type
      } else if (props.gameState.choice.type === "BOOLEAN") {
        return 'game.middle_area.choice.' + props.gameState.choice.sourceCard.id
      }
    } else {
      return 'game.middle_area.choice.waiting'
    }
  } else if (props.gameState?.currentPlayerID === props.gameState?.player.uuid) {
    if (props.pickedCard || props.attackingCard) {
      return 'game.middle_area.waiting'
    } else {
      return 'game.middle_area.player_turn'
    }
  } else {
    if (props.pickedCard) {
      return 'game.middle_area.choice.mindbug'
    } else if (props.attackingCard) {
      return 'game.middle_area.choice.attacked'
    } else {
      return 'game.middle_area.waiting'
    }
  }
})

const isPlayerTurn = computed(() => props.gameState?.currentPlayerID === props.gameState?.player.uuid)

// Computed value that controls image visibility
const isImageVisible = computed(() => {
  return !props.gameState?.winners &&
      (props.pickedCard !== undefined ||
      props.gameState?.choice?.type === "BOOLEAN" ||
      props.gameState?.choice?.type === "FRENZY" ||
      props.gameState?.choice?.type === "HUNTER")
})

// Computed value for the image source URL
const imgSrc = computed(() => {
  if (props.gameState?.choice) {
    return props.gameState.choice.targetCard ? props.gameState.choice.targetCard : props.gameState.choice.sourceCard
  } else if (props.pickedCard) {
    return props.pickedCard
  }
})

</script>

<template>
  <div class="middle-area">
    <span class="sr-only">{{ message ? t(message) : "" }}</span>
    <div class="turn-indicator" :class="{ 'opponent-turn': !isPlayerTurn }" aria-live="polite">
      <div class="turn-segment turn-segment--chevron" aria-hidden="true">
        <svg class="turn-chevron" viewBox="0 0 20 20" focusable="false">
          <path d="M4 7 L10 13 L16 7" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" />
        </svg>
      </div>
      <div class="turn-segment turn-segment--label">
        <span class="turn-label">{{ message ? t(message) : "" }}</span>
      </div>
      <div class="turn-segment turn-segment--chevron" aria-hidden="true">
        <svg class="turn-chevron" viewBox="0 0 20 20" focusable="false">
          <path d="M4 7 L10 13 L16 7" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" />
        </svg>
      </div>
    </div>
    <div class="turn-segment--card-slot" aria-hidden="true">
     <card
       v-if="isImageVisible"
       :card="imgSrc"
       context="board"
       :selected="false"
       :attacking="false"
       :clickable="false"
       class="middle-card"
       @preview="emit('card-preview', $event)"
      />
    </div>
  </div>
</template>

<style scoped>
  .middle-area {
    width: 100%;
    height: 30%;

    display: flex;
    align-items: center;
    justify-content: center;
    gap: 16px;
    position: relative;
  }

  .turn-indicator {
    display: flex;
    align-items: center;
    justify-content: stretch;

    width: clamp(320px, 70vw, 950px);
    height: clamp(36px, 6vh, 56px);
    box-sizing: border-box;

    background: #ffffff;
    border: 2px solid #1d1d1d;
    box-shadow: none !important;
    outline: 0 !important;
    filter: none !important;
    overflow: visible;
    cursor: default;
    padding: 0 8px;

  }

  .turn-segment {
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .turn-segment--chevron {
    flex: 0 0 auto;
    width: clamp(32px, 5vw, 55px);
    background: transparent;
  }

  .turn-segment--label {
    flex: 1 1 auto;
    max-width: none;
    padding: 0 24px;
  }

  .turn-segment--card-slot {
    position: absolute;
    right: -34px;
    top: 48%;
    transform: translateY(-50%);
    overflow: visible;
    min-width: calc(8.8vw + 16px);
  }

  .turn-segment--card-slot .middle-card {
    width: calc(8vw * 1.52);
    height: calc(12vw * 1.52);
  }

  .turn-label {
    font-size: clamp(18px, 2.6vw, 32px);
    font-weight: 700;
    color: #1b1b1b;
    letter-spacing: 0.02em;
    line-height: 1;
    white-space: nowrap;
    text-align: center;
    flex: 1 1 auto;
  }

  .turn-chevron {
    width: 34px;
    height: 34px;
    color: #1b1b1b;
    display: block;
    flex: 0 0 auto;
  }

  .middle-card {
    object-fit: cover;
    border-radius: 10px;
    box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.2);
    margin-right: 5vw;
  }

  .sr-only {
    position: absolute;
    width: 1px;
    height: 1px;
    padding: 0;
    margin: -1px;
    overflow: hidden;
    clip: rect(0, 0, 0, 0);
    white-space: nowrap;
    border: 0;
  }

</style>
