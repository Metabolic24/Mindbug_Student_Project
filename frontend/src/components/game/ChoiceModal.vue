<script setup lang="ts">
import {getCardAlt, getCardImage} from "@/shared/CardUtils";
import {computed, ref, Ref} from "vue";

// Declare the interface for the data given by the parent component
interface Props {
  choice: ChoiceModalData
}
const props = defineProps<Props>()

// Declare events emitted by this component
const emit = defineEmits(['button-clicked'])

// Reference for selected cards
const selectedCards: Ref<CardInterface[]> = ref([])

// Computed value for modal title
const title = computed(() => {
  return props.choice?.type === "TARGET" ?
      `Sélectionnez ${props.choice.count} cible(s) parmi ces cartes` :
      "Sélectionnez l'effet à déclencher en premier"
})

// Computed value to disable validation button
const isButtonDisabled = computed(() => {
  return (props.choice?.type === "SIMULTANEOUS" && selectedCards.value.length !== 1) ||
      (props.choice?.type === "TARGET" && !props.choice?.optional && selectedCards.value.length != props.choice?.count)
})

// Triggered when button is clicked
function onButtonClick() {
  if ((props.choice?.type === "SIMULTANEOUS" && selectedCards.value.length === 1) ||
      (props.choice?.type === "TARGET" && (props.choice?.optional || (!props.choice?.optional && selectedCards.value.length == props.choice?.count)))) {
    emit('button-clicked', selectedCards.value)
  }
}

// Triggered when a card is selected
function onCardSelected(card: CardInterface): void {
  if (selectedCards.value.includes(card)) {
    selectedCards.value.splice(selectedCards.value.indexOf(card), 1)
  } else if (selectedCards.value.length < props.choice.count) {
    selectedCards.value.push(card);
  }
}

// Function for card CSS classes
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
        <button type="button" aria-label="Close" v-if="choice?.optional">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        <div class="cards-container">
          <img v-for="card in choice?.cards" :src="getCardImage(card)" :alt="getCardAlt(card)" class="card-image"
               :class="getCardClasses(card)" @click="onCardSelected(card)" draggable="false" @contextmenu.prevent=""/>
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
  margin: 15px;
  height: 350px;

  border-radius: 12px;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.3);
}

.card-image.selected {
  outline: 4px solid red;
}
</style>
