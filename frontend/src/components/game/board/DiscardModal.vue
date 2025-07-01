<script setup lang="ts">
import {getCardAlt, getCardImage} from "@/shared/CardUtils";
import {computed} from "vue";

// Declare the interface for the data given by the parent component
interface Props {
  cards: CardInterface[];
  opponent: boolean;
}
const props = defineProps<Props>()

// Declare events emitted by this component
const emit = defineEmits(['close-modal'])

// Computed value for the modal title
const title = computed(() => {
  return props.opponent ?
      "Défausse de l'adversaire" :
      "Votre défausse";
})
</script>

<template>
  <div class="modal-mask" @click="emit('close-modal')">
    <div class="modal-container">
      <div class="modal-header" @click.stop>
        <h5 class="modal-title">{{ title }}</h5>
        <button type="button" aria-label="Close" @click="emit('close-modal')">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body" @click.stop>
        <div class="cards-container">
          <img v-for="card in cards" :src="getCardImage(card)" :alt="getCardAlt(card)" class="card-image"
               draggable="false" @contextmenu.prevent=""/>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modal-container {
  margin: 150px auto;
  padding: 20px 30px;
}

.modal-header {
  justify-content: space-between;
  padding-left: 20px;
}

.modal-body {
  padding: 10px 0;
}

.cards-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-around;

  padding: 20px;

  background-color: #f4f7fb;

  border-radius: 12px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.card-image {
  position: relative;
  height: 350px;

  margin: 15px;

  border-radius: 12px;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.3);
}
</style>
