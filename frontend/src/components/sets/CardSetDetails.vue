<script setup lang="ts">
import {onMounted, ref, Ref} from "vue";
import {getCardSetDetails} from "@/shared/RestService";
import {useI18n} from "vue-i18n";
import Card from "@/components/game/Card.vue";
import CardPreviewModal from "@/components/game/CardPreviewModal.vue";

const { t } = useI18n();

// Declare the interface for the data given by the parent component
interface Props {
  set: String,
  custom: boolean
}
const props = defineProps<Props>()

// Array that will contain all the IDs of the current set cards
const cards: Ref<LightCardInterface[]> = ref([])

// The card that should be displayed in the preview modal
const previewCard: Ref<LightCardInterface> = ref(undefined);

onMounted(async () => {
  // Get the list of card IDs from the server
  cards.value = await getCardSetDetails(props.set)
})

// Format correctly the set name
function getSetName() {
  return props.custom ? props.set : t("card_sets." + props.set)
}

function onCardPreview(card: LightCardInterface): void {
  previewCard.value = card
}

function closeCardPreview(): void {
  previewCard.value = undefined
}
</script>

<template>
  <nav @contextmenu.prevent>
    <router-link to="/">{{ t("router.home") }}</router-link>
    <router-link to="/sets">{{ t("router.available_sets") }}</router-link>
  </nav>
  <div id="cards-set" @contextmenu.prevent>
    <h1>{{ t("available_sets.title")}} <b>{{ getSetName() }}</b></h1>
    <div id="cards-container">
      <card
          v-for="card in cards"
          :key="card.id"
          :card="card"
          context="sets-details"
          :clickable="false"
          @preview="onCardPreview"
      />
    </div>
  </div>
  <card-preview-modal v-if="previewCard" :card="previewCard" @close="closeCardPreview"></card-preview-modal>
</template>

<style scoped>
nav {
  position: absolute;
}

#cards-set {
  padding-top: 3%;

  text-align: center;

  h1 {
    margin-bottom: 3%;
    font-size: xxx-large;
  }
}

#cards-container {
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
  width: 250px;
  height: 350px;
  margin: 15px;

  border-radius: 12px;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.3);
}
</style>
