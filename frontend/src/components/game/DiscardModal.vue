<script setup lang="ts">

import {getCardAlt, getCardImage} from "@/shared/CardUtils";
import {computed, ref, Ref} from "vue";

interface Props {
  cards: CardInterface[];
  opponent: boolean;
}

const props = defineProps<Props>()
const emit = defineEmits(['close-modal'])

const title = computed(() => {
  return props.opponent ?
      "Défausse de l'adversaire" :
      "Votre défausse";
})

</script>

<template>
  <div class="modal-mask">
    <div class="modal-container">
      <div class="modal-header">
        <h5 class="modal-title">{{ title }}</h5>
        <button type="button" aria-label="Close" @click="emit('close-modal')">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        <div class="cards-container">
          <img v-for="card in cards" :src="getCardImage(card)" :alt="getCardAlt(card)" class="card-image"/>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

.modal-mask {
  position: fixed;
  z-index: 9998;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
}

.modal-container {
  margin: 150px auto;
  padding: 20px 30px;
  background-color: #fff;
  border-radius: 2px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.33);
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
  margin: 15px;
  height: 350px;

  border-radius: 12px;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.3);
}

.card-image.selected {
  outline: 4px solid red;
}

</style>
