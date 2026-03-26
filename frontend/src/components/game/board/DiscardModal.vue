<script setup lang="ts">
import Card from "@/components/game/Card.vue";
import {ref, computed} from "vue";
import {Carousel, Slide, Pagination, Navigation} from "vue3-carousel"
import "vue3-carousel/carousel.css"

// Declare the interface for the data given by the parent component
interface Props {
  cards: CardInterface[];
  playerName: string;
  type: "enemy" | "ally" | "self";
}

const props = defineProps<Props>()

// Declare events emitted by this component
const emit = defineEmits(['close-modal'])

const viewMode = ref<'grid' | 'carousel'>('carousel')

//Carousel configuration 
const carouselConfig = {
  height: 400,
  itemsToShow: 3,
  wrapAround: props.cards.length >= 3,
  snapAlign: "center",
  transition: 350,
} as const

// Computed value for the modal title
const title = computed(() => {
  switch (props.type) {
    case "enemy":
      return `Défausse de l'adversaire (${props.playerName})`;
    case "ally":
      return `Défausse de votre allié (${props.playerName})`;
    case "self":
      return `Votre défausse (${props.playerName})`;
    default:
      return `Défausse (${props.playerName})`;
  }
});
</script>

<template>
  <div class="modal-mask" @click="emit('close-modal')">
    <div class="modal-container">

      <!-- HEADER -->
      <div class="modal-header" @click.stop>
        <h5 class="modal-title">{{ title }}</h5>
        <button class="close-btn" type="button" @click="emit('close-modal')">
          &times;
        </button>
      </div>

      <!-- BODY -->
      <div class="modal-body" @click.stop>
        <!-- Switch GRID/CAROUSEL -->
        <div class="view-toggle">
          <button :class="{ active: viewMode === 'carousel'}" @click="viewMode = 'carousel'">Carousel</button>
          <button :class="{ active: viewMode === 'grid' }" @click="viewMode = 'grid'">Grid</button>
        </div>

        <!-- GRID -->
        <div v-if="viewMode === 'grid'" class="cards-container">
          <Card v-for="card in props.cards" :key="card.uuid" :card="card" context="discard-modal" visibility="self" :clickable="false"/>
        </div>

        <!-- CAROUSEL -->
        <div v-else class="carousel-wrapper">
          <Carousel v-bind="carouselConfig">
            <Slide v-for="card in props.cards" :key="card.uuid">
              <div class="carousel-card">
                <Card :card="card" context="discard-modal" visibility="self" :clickable="false"/>
              </div>
            </Slide>

            <template #addons>
              <Navigation/>
              <Pagination/>
            </template>
          </Carousel>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modal-container {
  position: relative;
  margin: 100px auto;
  padding: 10px 20px 20px;
  max-width: 950px;
  max-height: 650px;
  background-color: #302931;
  border-radius: 12px;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.3);

  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .modal-title {
      font-size: 2.5rem;
      font-weight: 600;
      color: #f3eeee;
    }

    .close-btn {
      background: transparent;
      border: none;
      font-size: 2.5rem;
      cursor: pointer;
      color: #fcfcfc;
      transition: color 0.2s ease;
    }

    .close-btn:hover {
      color: #0c0c0c;
    }
  }

  .modal-body {
    display: flex;
    flex-direction: column;
    height: 500px;
  }

  .view-toggle {
    display: flex;
    gap: 10px;
    margin: 0 0 10px;

    button {
      padding: 5px 15px;
      border-radius: 6px;
      border: 1px solid #ccc;
      background: #f4f4f4;
      cursor: pointer;
      transition: all 0.2s ease;
    }

    button.active {
      background: #007bff;
      color: white;
      border-color: #007bff;
    }

    button:hover {
      background: #4ca3eb;
      color: white;
      border-color: #509cee;
    }
  }
}

/* ================= GRID ================= */

.cards-container {
  display: flex;
  flex-wrap: wrap;
  gap: 50px;
  justify-content: center;
  border-radius: 12px;
  padding: 30px 20px 20px;

  flex: 1;
  overflow-y: auto;
}

/* ================= CAROUSEL ================= */

.carousel {
  --vc-nav-background: rgba(0, 0, 0, 0.3);
  --vc-nav-color: white;
  --vc-nav-color-hover: #e5e5e5;
  --vc-nav-border-radius: 50%;
  --vc-nav-width: 40px;
  --vc-nav-height: 40px;

  --vc-pgn-background-color: white;
  --vc-pgn-active-color: red;
  --vc-pgn-height: 5px;
  --vc-pgn-width: 20px;
}

.carousel__viewport {
  perspective: 2000px;
}

.carousel__track {
  transform-style: preserve-3d;
}

.carousel__slide--sliding {
  transition: opacity var(--carousel-transition),
  transform var(--carousel-transition);
}

.carousel.is-dragging .carousel__slide {
  transition: opacity var(--carousel-transition),
  transform var(--carousel-transition);
}

.carousel__slide {
  opacity: var(--carousel-opacity-inactive);
  transform: translateX(10px) rotateY(-12deg) scale(0.9);
}

.carousel__slide--prev {
  opacity: var(--carousel-opacity-near);
  transform: rotateY(-10deg) scale(0.95);
}

.carousel__slide--active {
  opacity: var(--carousel-opacity-active);
  transform: rotateY(0) scale(1);
}

.carousel__slide--next {
  opacity: var(--carousel-opacity-near);
  transform: rotateY(10deg) scale(0.95);
}

.carousel__slide--next ~ .carousel__slide {
  opacity: var(--carousel-opacity-inactive);
  transform: translateX(-10px) rotateY(12deg) scale(0.9);
}
</style>

