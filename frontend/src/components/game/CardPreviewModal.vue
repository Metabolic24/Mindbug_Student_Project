<script setup lang="ts">
import {computed, onMounted, ref, watch} from "vue";
import {getCardAlt, getCardImage} from "@/shared/CardUtils";
import {
  getActivationLabel,
  getCardActivations,
  getEvolutionActivation,
  getEvolutionTargetId,
  getKeywordDescriptions
} from "@/shared/CardTextUtils";
import {getCardSetCards} from "@/shared/RestService";

interface Props {
  card: CardInterface
}

const props = defineProps<Props>()
const emit = defineEmits(['close'])

const setCards = ref<CardDetails[] | null>(null)
const cardDetails = ref<CardDetails | null>(null)
const loadingDetails = ref(false)

const keywordSource = computed(() => props.card?.keywords ?? cardDetails.value?.keywords ?? [])
const keywordDescriptions = computed(() => getKeywordDescriptions(keywordSource.value))
const keywordIcons: Record<string, string> = {
  FRENZY: new URL("@/assets/cards/KeywordIcons/FRENZY.png", import.meta.url).href,
  HUNTER: new URL("@/assets/cards/KeywordIcons/HUNTER.png", import.meta.url).href,
  POISONOUS: new URL("@/assets/cards/KeywordIcons/POISONOUS.png", import.meta.url).href,
  SNEAKY: new URL("@/assets/cards/KeywordIcons/SNEAKY.png", import.meta.url).href,
  TOUGH: new URL("@/assets/cards/KeywordIcons/TOUGH.png", import.meta.url).href,
}
const keywordItems = computed(() =>
  keywordDescriptions.value.map((item) => ({
    ...item,
    icon: keywordIcons[item.keyword],
    inactive: item.keyword === "TOUGH" && props.card?.stillTough === false
  }))
)

const descriptionSource = computed(() => props.card?.description ?? cardDetails.value?.description ?? "")
const descriptionText = computed(() => {
  const raw = descriptionSource.value ?? ""
  return raw
    .replace(/<br\s*\/?>/gi, " ")
    .replace(/<[^>]*>/g, "")
    .replace(/&nbsp;/gi, " ")
    .replace(/\s+/g, " ")
    .trim()
})
const powerSource = computed(() => props.card?.power ?? cardDetails.value?.power ?? 0)
const basePowerSource = computed(() => props.card?.basePower ?? cardDetails.value?.basePower ?? powerSource.value)

const cardEffects = computed(() => getCardActivations(cardDetails.value?.effects))

const activationFromDescription = computed(() => {
  const description = descriptionText.value
  if (!description) return null
  const match = /^(Play|Attack|Passive|Action|Defeated|Life Lost)\s*(?:[:\-–—])?\s*(.+)$/i.exec(description)
  if (!match) return null
  const label = match[1].toUpperCase().replace(" ", "_")
  return {
    name: label,
    activation: getActivationLabel(label),
    description: match[2].trim(),
  }
})

const activationData = computed(() => {
  const items = cardEffects.value.map((effect) => ({
    name: effect.name,
    activation: effect.activation,
    description: ""
  }))
  let descriptionAttached = false
  if (activationFromDescription.value) {
    const existingIndex = items.findIndex((item) => item.name === activationFromDescription.value?.name)
    if (existingIndex !== -1) {
      items[existingIndex].description = activationFromDescription.value.description
    } else {
      items.push(activationFromDescription.value)
    }
    descriptionAttached = true
  } else if (descriptionText.value && items.length > 0) {
    const passiveIndex = items.findIndex((item) => item.name === "PASSIVE")
    const targetIndex = passiveIndex !== -1 ? passiveIndex : (items.length === 1 ? 0 : -1)
    if (targetIndex !== -1) {
      items[targetIndex].description = descriptionText.value
      descriptionAttached = true
    }
  }
  return { items, descriptionAttached }
})

const evolutionTargetId = computed(() => getEvolutionTargetId(cardDetails.value?.effects))
const evolutionActivation = computed(() => getEvolutionActivation(cardDetails.value?.effects))
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
        <img
          :src="getCardImage(card)"
          :alt="getCardAlt(card)"
          class="preview-card"
          draggable="false"
        />

        <div class="preview-text">
          <h3 class="card-title">{{ card.name ?? "Unknown card" }}</h3>
          <div v-if="!activationData.descriptionAttached && descriptionSource" class="card-description" v-html="descriptionSource"></div>
          <div class="section">
            <div class="section-title">Power</div>
            <div class="muted">
              {{ powerSource }}
              <span v-if="basePowerSource !== powerSource"> (Base {{ basePowerSource }})</span>
            </div>
          </div>
          <div v-if="evolutionInfo || evolutionFrom" class="evolution">
            <div class="section-title">Evolution</div>
            <div v-if="evolutionFrom" class="muted">Evolves from {{ evolutionFrom }}.</div>
            <div v-if="evolutionInfo" class="muted">
              Evolves into {{ evolutionInfo.targetName }} when its EVOLVE effect resolves at:
              {{ evolutionActivation ?? "its activation" }}.
            </div>
          </div>

          <div class="section">
            <div class="section-title">Keywords</div>
            <div v-if="keywordDescriptions.length === 0" class="muted">No keywords.</div>
            <ul v-else class="list">
              <li v-for="item in keywordItems" :key="item.keyword">
                <span class="keyword-row">
                  <img v-if="item.icon" :src="item.icon" :alt="item.keyword" class="keyword-icon" />
                  <strong :class="{ inactive: item.inactive }">{{ item.keyword }}</strong>
                </span>
                <span class="keyword-text" :class="{ inactive: item.inactive }">— {{ item.description }}</span>
                <span v-if="item.inactive" class="keyword-status">(TOUGH effect already used.)</span>
              </li>
            </ul>
          </div>

          <div class="section">
            <div class="section-title">Activation</div>
            <div v-if="activationData.items.length === 0" class="muted">No activation listed.</div>
            <ul v-else class="list">
              <li v-for="(effect, effectIndex) in activationData.items" :key="effect.activation + '-' + effectIndex">
                <strong v-if="effect.name">{{ effect.name }}</strong>
                <span v-if="effect.name"> — </span>
                {{ effect.activation }}
                <template v-if="effect.description">
                  <br />
                  <span class="activation-description">• {{ effect.description }}</span>
                </template>
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


  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-content {
  position: relative;
  width: min(92vw, 980px);
  display: grid;
  grid-template-columns: minmax(240px, 420px) minmax(260px, 1fr);
  gap: 24px;
  align-items: start;
  padding: 12px;
  background: rgba(18, 18, 18, 0.9);
  border-radius: 16px;
}

:global(.preview-card) {
  width: min(90vw, 420px);
  height: calc(min(38vw, 420px) * 1.5);
  border-radius: 14px;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.45);
  object-fit: cover;
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

.card-description {
  color: #d8d8d8;
  font-size: 14px;
  line-height: 1.5;
  margin: -6px 0 12px;
}

.section {
  margin-top: 16px;
}

.evolution {
  margin-top: 20px;
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

.list li {
  display: block;
  margin-bottom: 8px;
}

.keyword-row {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  white-space: nowrap;
  margin-right: 6px;
}

.keyword-icon {
  width: 18px;
  height: 18px;
  object-fit: contain;
  flex: 0 0 auto;
}

.keyword-text {
  white-space: normal;
}

.inactive {
  opacity: 0.55;
}

.keyword-status {
  display: inline-block;
  margin-left: 8px;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  color: #a9a9a9;
}

.muted {
  color: #b0b0b0;
  font-size: 14px;
}

.activation-description {
  color: #d8d8d8;
  font-size: 14px;
  line-height: 1.5;
}

.effect-blocks {
  display: flex;
  flex-direction: column;
  gap: 12px;
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
