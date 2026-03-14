<script setup lang="ts">
import {onMounted, ref, Ref} from "vue";
import {createCardSet, getAllCards} from "@/shared/RestService";
import {getCardImage} from "@/shared/CardUtils";
import {useI18n} from "vue-i18n";

const { t } = useI18n();

// Array that will contain all the IDs of the available cards from any default card set
let availableCards: LightCardInterface[]

// Array that will contain all the IDs of the cards chosen for the current set
let leftSideCards: Ref<LightCardInterface[]> = ref([])

// Array that will contain all the IDs of the cards chosen for the current set
let rightSideCards: Ref<LightCardInterface[]> = ref([])

// The card set name
let cardSetName: Ref<string> = ref("")

onMounted(async () => {
  // Get the list of card DTOs from the server
  availableCards = await getAllCards()

  const unevolvedCards = availableCards.filter(card => card.parentId === undefined)
  leftSideCards.value.push(...unevolvedCards)
})

function onCardClick(event: MouseEvent, card: LightCardInterface, cardsList: LightCardInterface[]) {
  event.preventDefault()

  if (card.evolutionId) {
    const evolutionCard = availableCards.find(c => c?.id === card.evolutionId)
    const cardIndex = cardsList.findIndex(c => c?.id === card.id)
    cardsList[cardIndex] = evolutionCard
  } else if (card.parentId) {
    const initialCard = availableCards.find(c => c?.id === card.parentId)
    const cardIndex = cardsList.findIndex(c => c?.id === card.id)
    cardsList[cardIndex] = initialCard
  }
}

function cardDragged(event: DragEvent, card: LightCardInterface) {
  event.dataTransfer.dropEffect = 'move'
  event.dataTransfer.effectAllowed = 'move'
  event.dataTransfer.setData('cardId', String(card.id))
}

function cardDrop(event: DragEvent, sourceCards: LightCardInterface[], targetCards: LightCardInterface[]) {
  const cardId = event.dataTransfer.getData(('cardId'))
  const cardIndex = sourceCards.findIndex(card => card?.id === +cardId)

  if (cardIndex > -1) {
    targetCards.push(sourceCards[cardIndex])
    targetCards.sort((a, b) => a.id - b.id)
    sourceCards.splice(cardIndex, 1)
  }
}

function isCreateButtonDisabled() {
  return !cardSetName.value || !rightSideCards.value || rightSideCards.value.length < 20
}
</script>

<template>
  <nav @contextmenu.prevent>
    <router-link to="/">{{ t('router.home') }}</router-link>
    <router-link to="/sets">{{ t('router.available_sets') }}</router-link>
  </nav>
  <div id="card-set-creator" @contextmenu.prevent>
    <div id="card-set-form">
      <h1>{{ t('create_set.title') }}</h1>
      <input id="card-set-name" type="text" v-model="cardSetName" :placeholder="t('create_set.name_placeholder')"/>
      <input id="card-set-create-button" type="button" :disabled="isCreateButtonDisabled()"
             @click="createCardSet(cardSetName, rightSideCards.map(card => card.id))" :value="t('create_set.button')">
    </div>
    <div id="card-selector">
      <div class="cards-container" @drop="cardDrop($event, rightSideCards, leftSideCards)"
           @dragover.prevent @dragenter.prevent>
        <img v-for="availableCard in leftSideCards" :key="availableCard.id" :src="getCardImage(availableCard.id)"
             :alt="t('misc.card_not_found')"
             @contextmenu="onCardClick($event, availableCard, leftSideCards)"
             draggable="true" @dragstart="cardDragged($event, availableCard)"/>
      </div>
      <div class="cards-container" @drop="cardDrop($event, leftSideCards, rightSideCards)"
           @dragover.prevent @dragenter.prevent>
        <img v-for="chosenCard in rightSideCards" :key="chosenCard.id" :src="getCardImage(chosenCard.id)"
             :alt="t('misc.card_not_found')"
             @contextmenu="onCardClick($event, chosenCard, rightSideCards)"
             draggable="true" @dragstart="cardDragged($event, chosenCard)"/>
      </div>
    </div>
  </div>
</template>

<style scoped>
nav {
  height: 3%;
}

#card-set-creator {
  text-align: center;
  height: 97%;
}

#card-set-form {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  gap: 15px;

  height: 6%;
  margin-bottom: 1%;
}

#card-selector {
  display: flex;
  flex-direction: row;
  gap: 4%;

  height: 91%;
  margin: 0 10px;
}

.cards-container {
  width: 50%;
  height: 99%;
  overflow-y: auto ;

  display: flex;
  flex-wrap: wrap;
  justify-content: space-around;
  align-content: flex-start;

  padding: 20px;

  background-color: #f4f7fb;

  border-radius: 12px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);

  img {
    position: relative;
    width: 250px;
    height: 350px;
    margin: 15px;

    border-radius: 12px;
    box-shadow: 0 6px 12px rgba(0, 0, 0, 0.3);
  }
}
</style>
