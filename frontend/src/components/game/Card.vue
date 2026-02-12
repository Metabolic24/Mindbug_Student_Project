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

  
  // to get the keyword Icons
  const keywordIcons: Record<string, string> = {
    FRENZY: new URL('@/assets/cards/KeywordIcons/FRENZY.png', import.meta.url).href,
    HUNTER: new URL('@/assets/cards/KeywordIcons/HUNTER.png', import.meta.url).href,
    POISONOUS: new URL('@/assets/cards/KeywordIcons/POISONOUS.png', import.meta.url).href,
    SNEAKY: new URL('@/assets/cards/KeywordIcons/SNEAKY.png', import.meta.url).href,
    REVERSED: new URL('@/assets/cards/KeywordIcons/REVERSED.png', import.meta.url).href,
    TOUGH: new URL('@/assets/cards/KeywordIcons/TOUGHT.png', import.meta.url).href,
  }

  const displayKeywords = computed(() => {
    if (!props.card.keywords) return []
    return props.card.keywords.map(k => ({
      key: k,
      icon: keywordIcons[k]
    }))
})

  const formattedKeywords = computed(() => {
    if (!props.card.keywords || props.card.keywords.length === 0) return ''
    else return props.card.keywords.join(' * ')
  })


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
  <div class="card-wrapper" :class="cardClasses" @click="clickable && emit('click', props.card)">
    <!-- Card image -->
    <img
      :src="getCardImage(card)"
      :alt="getCardAlt(card)"
      class="card-image"
      draggable="false"
      @contextmenu.prevent=""
    />

    <div v-if="showOverlay" class="title-banner">
      <div class="title-text">{{ props.card.name }}</div>
    </div>
    <div 
      v-if="showOverlay && displayKeywords.length"
      class="keywords-row"
    >
      <div
        v-for="kw in displayKeywords"
        :key="kw.key"
        class="keyword-badge"
      >
        <img 
          :src="kw.icon"
          :alt="kw.key"
          class="keyword-icon"
        />
        <div class="keyword-tooltip">
          {{ kw.key }}
        </div>
      </div>
    </div>

    <!-- Description Box -->
    <div v-if="showOverlay" class="description-box">
      <div class="description-text" v-html="props.card.description"></div>
        <!-- Keywords -->
        <div 
          v-if="formattedKeywords"
          class="keywords-text"
        >
          {{ formattedKeywords }}
      </div>
    </div>

    <!-- Overlay power -->
    <div v-if="showOverlay" class="power-overlay" :class="{ 'modified-power': isPowerModified }">
      <Transition name="power-slide" mode="out-in">
        <span :key="props.card.power">{{ props.card.power }}</span>
      </Transition>
    </div>
  </div>
</template>


<style scoped>

  /* #############################################  General card  ############################################# */

  /* General card styling */
  .card-wrapper {
    position: relative;
    width: 7vw;
    height: 10vw;

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
    top: 4%;
    left: 6%;

    width: 19%;
    height: 13%;

    background: #512134;
    color: #fefcfe;
    font-size: clamp(0.5rem, 1vw, 1rem);

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

  /* #############################################  Title/description card  ############################################# */

  .title-banner {
  position: absolute;
  top: 4.5%;
  left: 7%;
  width: 88%;
  height: 11%;

  background: #2c2f3a;
  border: 1px solid #f0a23a;
  border-radius: 2vw;

  display: flex;
  align-items: center;
  justify-content: center;
  padding-left: 15%;

  box-shadow: 0 2px 5px rgba(0,0,0,0.4);
}


.title-text {
  font-family: "ZalamanderCaps";
  font-weight: bold;
  font-size: 0.7em;
  text-align: center;
  letter-spacing: 0.5px;
  color: white;
  line-height: 0.8;
}

/* Area of card description */
.description-box {
  position: absolute;
  bottom: 5%;
  left: 5%;
  width: 90%;
  height: 28%;

  background: rgba(25, 30, 40, 0.95);
  border: 1px solid #f0a23a;
  border-radius: 20px;

  padding: 5%;
  box-sizing: border-box;

  display: flex;
  flex-direction: column;
  justify-content: center;
  overflow: hidden;

  color: white;
}

.description-text {
  font-size: 0.50em;
  line-height: 1.1;
  text-align: center;
}

.keywords-text {
  font-size: 0.55em;
  font-weight: bold;
  color: #f0a23a;
  text-align: center;
  letter-spacing: 1px;
}


</style>