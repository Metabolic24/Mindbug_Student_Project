<script setup lang="ts">
  import {computed }  from "vue";
  import { getCardAlt, getCardImage } from "@/shared/CardUtils";

  // Declare the interface for the data given by the parent component
  interface Props {
    card: CardInterface

    context: 'player-hand' | 'opponent-hand' | 'board'

    selected?: boolean
    attacking?: boolean
    clickable?: boolean
  }

  // Define props and emits
  const props = defineProps<Props>()
  const emit = defineEmits(['click'])

  // To know if the power is modified
  const isPowerModified = computed(() => {
    return props.card.power !== props.card.basePower
  })

  // Determine the CSS classes for the card based on its context and state
  const cardClasses = computed(() => ({
    'bottom-card': props.context === 'player-hand',
    'opponent-hand': props.context === 'opponent-hand',
    'selected': props.selected,
    'attacking': props.attacking,
    'clickable': props.clickable,
    'TOUGH': props.context === 'board' && props.card.keywords?.includes('TOUGH') && props.card.stillTough
  }))

  // Determine if the power overlay should be shown on the opponent's hand
  const showOverlay = computed(() => props.context !== 'opponent-hand');
</script>

<template>
  <div class="card-wrapper" :class="cardClasses" @click="clickable && emit('click', card)">
    <!-- Card image -->
    <img
      :src="getCardImage(card)"
      :alt="getCardAlt(card)"
      class="card-image"
      draggable="false"
      @contextmenu.prevent=""
    />

    <!-- Overlay power -->
    <div v-if="showOverlay" class="power-overlay" :class="{ 'modified-power': isPowerModified }">
      <Transition name="power-slide" mode="out-in">
        <span :key="card.power">{{ card.power }}</span>
      </Transition>
    </div>
  </div>
</template>


<style scoped>

  /* #############################################  General card  ############################################# */

  /* General card styling */
  .card-wrapper {
    position: relative;
    width: 6vw;
    height: 9vw;
    transition: transform 0.25s ease, box-shadow 0.25s ease;
  }

  .card-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 10px;
    box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.2);
  }

  /* Opponent hand styling */
  .card-wrapper.opponent-hand {
    transform: translateY(-45%);
    opacity: 0.95;
  }

  .card-wrapper.opponent-hand:hover {
    transform: translateY(-45%) scale(1.1);
    z-index: 5;
  }

  /* Hide power overlay for opponent's hand */
  .card-wrapper.opponent-hand.power-overlay{
    display: none;
  }

  /* Selected and attacking card styling */
  .card-wrapper.selected {
    outline: 4px solid red;
  }

  .card-wrapper.attacking {
    outline: 4px solid orange;
  }

  .card-wrapper.clickable {
    cursor: pointer;
  }

  .card-wrapper.TOUGH {
    position: relative;
    overflow: hidden;
  }

  /* #############################################  POWER card  ############################################# */

  /* Modified power styling */
  .power-overlay.modified-power {
    color: rgb(255, 217, 0);
    text-shadow: 0 0 6px rgba(255, 215, 0, 0.8);
  }

  /* Power overlay */
  .power-overlay {
    position: absolute;
    top: 5%;
    left: 5%;

    min-width: 19%;
    height: 12%;

    background: #512134;
    color: #fefcfe;
    font-size: 100%;

    display: flex;
    align-items: center;
    justify-content: center;

    border-radius: 50%;
    border: 2px solid #ac3f69;
    overflow: hidden;
  }
  .power-overlay span {
    font-weight: bold;
  }

  /* Power change animation */
  .power-number {
    display: inline-block;
    width: 100%;
    text-align: center;
  }

  .power-slide-leave-active,
  .power-slide-enter-active {
    transition: all 0.25s ease-out;
  }
  .power-slide-leave-to {
    transform: translateY(100%);
    opacity: 0;
  }
  .power-slide-enter-from {
    transform: translateY(-100%);
    opacity: 0;
  }
  
</style>