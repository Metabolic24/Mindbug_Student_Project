<script setup lang="ts">
import {computed, ref, Ref} from "vue";
import {useI18n} from "vue-i18n";
import Card from "@/components/game/Card.vue";

const { t } = useI18n();

// Declare the interface for the data given by the parent component
interface Props {
  choice: ChoiceModalData
}
const props = defineProps<Props>()

// Declare events emitted by this component
const emit = defineEmits(['button-clicked', 'card-preview'])

// Reference for selected cards
const selectedCards: Ref<CardInterface[]> = ref([])

// Computed value for modal title
const title = computed(() => {
  return props.choice?.type === "TARGET" ?
      t('modal.game.choice.target_title', {targets_count: props.choice.count}) :
      t('modal.game.choice.simultaneous_title')
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
    selectedCards.value = []
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

function onCardPreview(card: CardInterface): void {
  emit('card-preview', card)
}

</script>

<template>
  <div class="modal-mask">
    <div class="modal-container">
      <div>
        <h5 class="choice-modal-title">{{ title }}</h5>
        <button type="button" aria-label="Close" v-if="choice?.optional" :title="t('modal.game.choice.leave_tooltip')">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div>
        <div id="cards-container">
          <card
              v-for="card in choice?.cards"
              :key="card.uuid"
              :card="card"
              context="choice-modal"
              :selected="selectedCards.includes(card)"
              :clickable="true"
              @click="onCardSelected"
              @preview="onCardPreview"
          />
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" @click="onButtonClick()" :disabled="isButtonDisabled">
          {{ t('modal.game.choice.save') }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modal-container {
  margin: 10% auto;
  padding: 20px 30px;

  display: flex;
  flex-direction: column;
  gap: 15px;
}

#choice-modal-title {
  text-align: center;
}

#cards-container {
  width: 100%;
  height: 100%;
  min-height: 13vw;
  padding: 10px 20px;

  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  align-items: center;
  gap: 15px;

  background-color: #f4f7fb;
  border-radius: 12px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  max-height: 400px;
  overflow-y: auto; 
}
</style>
