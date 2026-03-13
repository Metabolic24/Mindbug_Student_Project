<script setup lang="ts">
import {computed, onMounted, ref, watch} from "vue";
import Card from "@/components/game/Card.vue";
import {getCardTimings, getEvolutionTargetId, getEvolutionTiming, getKeywordDescriptions} from "@/shared/CardTextUtils";
import {getCardSetCards} from "@/shared/RestService";

interface Props {
  card: CardInterface
}

const props = defineProps<Props>()
const emit = defineEmits(['close'])

const setCards = ref<CardDetails[] | null>(null)
const cardDetails = ref<CardDetails | null>(null)
const loadingDetails = ref(false)

const keywordSource = computed(() => cardDetails.value?.keywords ?? props.card?.keywords ?? [])
const keywordDescriptions = computed(() => getKeywordDescriptions(keywordSource.value))

const cardEffects = computed(() => getCardTimings(cardDetails.value?.effects))

const evolutionTargetId = computed(() => getEvolutionTargetId(cardDetails.value?.effects))
const evolutionTiming = computed(() => getEvolutionTiming(cardDetails.value?.effects))
const evolutionInfo = computed(() => {
  if (evolutionTargetId.value == null || !setCards.value) return null
  const target = setCards.value.find((c) => c.id === evolutionTargetId.value)
  return {
    targetName: target?.name ?? `Card #${evolutionTargetId.value}`
  }
})

const evolutionFrom = computed(() => {
  if (!setCards.value) return null
  for (const candidate of setCards.value) {
    const targetId = getEvolutionTargetId(candidate.effects)
    if (targetId === props.card.id) {
      return candidate.name ?? `Card #${candidate.id}`
    }
  }
  return null
})

async function loadCardDetails() {
  if (!props.card?.setName) {
    cardDetails.value = null
    return
  }

  loadingDetails.value = true
  try {
    if (!setCards.value) {
      setCards.value = await getCardSetCards(props.card.setName)
    }
    cardDetails.value = setCards.value?.find((c) => c.id === props.card.id) ?? null
  } finally {
    loadingDetails.value = false
  }
}

onMounted(loadCardDetails)
watch(() => `${props.card?.setName}-${props.card?.id}`, loadCardDetails)

</script>

<template>
  <div class="modal-mask" @click="emit('close')">
    <div class="preview-container" @click="emit('close')">
      <div class="preview-content" @click.stop>
        <button type="button" class="close-button" aria-label="Close" @click="emit('close')">
          <span aria-hidden="true">&times;</span>
        </button>
        <Card
          :card="card"
          context="board"
          :selected="false"
          :attacking="false"
          :clickable="false"
          class="preview-card"
        />

        <div class="preview-text">
          <h3 class="card-title">{{ card.name ?? "Unknown card" }}</h3>
          <div v-if="evolutionInfo || evolutionFrom" class="evolution">
            <div class="section-title">Evolution</div>
            <div v-if="evolutionFrom" class="muted">Evolves from {{ evolutionFrom }}.</div>
            <div v-if="evolutionInfo" class="muted">
              Evolves into {{ evolutionInfo.targetName }} when its EVOLVE effect resolves at:
              {{ evolutionTiming ?? "its timing" }}.
            </div>
          </div>

          <div class="section">
            <div class="section-title">Keywords</div>
            <div v-if="keywordDescriptions.length === 0" class="muted">No keywords.</div>
            <ul v-else class="list">
              <li v-for="item in keywordDescriptions" :key="item.keyword">
                <strong>{{ item.keyword }}</strong> — {{ item.description }}
              </li>
            </ul>
          </div>

          <div class="section">
            <div class="section-title">Timing</div>
            <div v-if="cardEffects.length === 0" class="muted">No timing listed.</div>
            <ul v-else class="list">
              <li v-for="(effect, effectIndex) in cardEffects" :key="effect.timing + '-' + effectIndex">
                <strong v-if="effect.name">{{ effect.name }}</strong>
                <span v-if="effect.name"> — </span>
                {{ effect.timing }}
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.preview-container {
  position: relative;
  margin: 0;
  width: min(92vw, 980px);

  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-content {
  position: relative;
  display: grid;
  grid-template-columns: minmax(240px, 420px) minmax(260px, 1fr);
  gap: 24px;
  align-items: start;
  padding: 12px;
  background: rgba(18, 18, 18, 0.9);
  border-radius: 16px;
}

:global(.preview-card.card-wrapper) {
  width: min(38vw, 420px);
  height: calc(min(38vw, 420px) * 1.5);
  font-size: clamp(18px, 2vw, 24px);
  border-radius: 14px;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.45);
}

:global(.preview-card .card-image) {
  height: 100%;
}

:global(.preview-card .power-overlay span) {
  font-size: 3em;
}

:global(.preview-card .description-text) {
  font-size: 0.9em;
  line-height: 1.2;
}

.preview-text {
  max-height: min(80vh, 720px);
  overflow: auto;
  padding-right: 6px;
  color: #f1f1f1;
}

.card-title {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 12px;
}

.section {
  margin-top: 16px;
}

.section-title {
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  font-size: 12px;
  color: #d0d0d0;
  margin-bottom: 8px;
}

.list {
  margin: 0;
  padding-left: 16px;
}

.muted {
  color: #b0b0b0;
  font-size: 14px;
}

.effect-blocks {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.effect-timing {
  font-size: 12px;
  color: #c5c5c5;
  margin-bottom: 2px;
}

.effect-title,
.effect-description {
  display: none;
}

.close-button {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: none;
  background: rgba(0, 0, 0, 0.8);
  color: #ffffff;
  font-size: 22px;
  line-height: 1;
  cursor: pointer;
}

:global(.modal-mask) {
  display: flex;
  align-items: center;
  justify-content: center;
}

@media (max-width: 900px) {
  .preview-content {
    grid-template-columns: 1fr;
  }
}
</style>
