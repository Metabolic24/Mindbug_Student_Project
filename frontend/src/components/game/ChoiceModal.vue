<script setup lang="ts">

import {getCardAlt, getCardImage} from "@/shared/CardUtils";
import {computed, ref, Ref} from "vue";

interface Props {
  choice: ChoiceModalData
}

const props = defineProps<Props>()
const emit = defineEmits(['button-clicked'])

const selectedCards: Ref<CardInterface[]> = ref([])

const title = computed(() => {
  return props.choice?.type === "TARGET" ?
      `Sélectionnez ${props.choice.count} cible(s) parmi ces cartes` :
      "Sélectionnez l'effet à déclencher en premier"
})

const isButtonDisabled = computed(() => {
  return (props.choice.type === "SIMULTANEOUS" && selectedCards.value.length !== 1) ||
      (props.choice.type === "TARGET" && !props.choice.optional && selectedCards.value.length != props.choice.count)
})

function onButtonClick() {
  if ((props.choice.type === "SIMULTANEOUS" && selectedCards.value.length === 1) ||
      (props.choice.type === "TARGET" && (props.choice.optional || (!props.choice.optional && selectedCards.value.length == props.choice.count)))) {
    emit('button-clicked', selectedCards.value)
  }
}

function onCardSelected(card: CardInterface): void {
  if (selectedCards.value.includes(card)) {
    selectedCards.value.splice(selectedCards.value.indexOf(card), 1)
  } else if (selectedCards.value.length < props.choice.count) {
    selectedCards.value.push(card);
  }
}

function getCardClasses(card: CardInterface): Record<string, boolean> {
  return ({
    'selected': selectedCards.value.includes(card),
  })
}

</script>

<template>
  <div class="modal-mask">
    <div class="modal-container">
      <div class="modal-header">
        <h5 class="modal-title">{{ title }}</h5>
        <button type="button" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        <div class="cards-container">
          <img v-for="card in choice?.cards" :src="getCardImage(card)" :alt="getCardAlt(card)" class="card-image"
               :class="getCardClasses(card)" @click="onCardSelected(card)"/>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" @click="onButtonClick()" :disabled="isButtonDisabled">
          Save choice
        </button>
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
