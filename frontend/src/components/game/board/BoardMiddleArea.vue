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

    <span>{{ message ? t(message) : "" }}</span>
  </div>
</template>

<style scoped>
  .middle-area {
    width: 100%;
    height: 30%;

    display: flex;
    align-items: center;
    justify-content: space-around;
  }

  span {
    font-size: 4.5vh;
    font-weight: bolder;
    color: mediumvioletred;

    background-color: rgba(197, 192, 192, 0.8);
    cursor: default;

    padding: 5px 15px;

    border-radius: 10px;
  }

  .middle-card {
    width: 6vw;
    height: 9vw;
    object-fit: cover;
    border-radius: 10px;
    box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.2);
  }

</style>
