<script setup lang="ts">
import {computed, onMounted, ref, watch} from "vue";
import {getCardAlt, getCardImage} from "@/shared/CardUtils";
import {getAllCards} from "@/shared/RestService";
import {useI18n} from "vue-i18n";

interface Props {
  card: CardInterface | LightCardInterface;
}

const props = defineProps<Props>()
const emit = defineEmits(['close'])

const { t } = useI18n()

const setCards = ref<LightCardInterface[] | null>(null)
const cardDetails = computed(() => setCards.value?.find((c) => c.id === props.card?.id) ?? null)

const keywordSource = computed(() => props.card?.keywords ?? cardDetails.value?.keywords ?? [])
const keywordIcons: Record<string, string> = {
  FRENZY: new URL("@/assets/cards/KeywordIcons/FRENZY.png", import.meta.url).href,
  HUNTER: new URL("@/assets/cards/KeywordIcons/HUNTER.png", import.meta.url).href,
  POISONOUS: new URL("@/assets/cards/KeywordIcons/POISONOUS.png", import.meta.url).href,
  SNEAKY: new URL("@/assets/cards/KeywordIcons/SNEAKY.png", import.meta.url).href,
  TOUGH: new URL("@/assets/cards/KeywordIcons/TOUGH.png", import.meta.url).href,
}

const keywordItems = computed(() =>
  keywordSource.value.map((keyword) => {
    const labelKey = `cards.keywords.${keyword}`
    const label = t(labelKey)
    const descriptionKey = `card_preview.keyword_descriptions.${keyword}`
    const description = t(descriptionKey)
    const stillTough = (props.card as CardInterface)?.stillTough

    return {
      keyword,
      label: label === labelKey ? keyword : label,
      description: description === descriptionKey ? t("card_preview.keyword_descriptions.default") : description,
      icon: keywordIcons[keyword],
      inactive: keyword === "TOUGH" && !stillTough
    }
  })
)

const descriptionSource = computed(() => {
  const key = `cards.${props.card?.id}.description`
  const translated = t(key)
  if (translated !== key) {
    return translated
  }
  return ""
})

const cardName = computed(() => {
  const key = `cards.${props.card?.id}.name`
  const translated = t(key)

  return translated !== key ? translated : t("card_preview.unknown_card")
})

const descriptionText = computed(() => {
  const raw = descriptionSource.value ?? ""
  return raw
    .replace(/<br\s*\/?>/gi, " ")
    .replace(/<[^>]*>/g, "")
    .replace(/&nbsp;/gi, " ")
    .replace(/\s+/g, " ")
    .trim()
})
const isFlavorText = computed(() => /<\s*q\b/i.test(descriptionSource.value))

const powerSource = computed(() => props.card?.power ?? cardDetails.value?.power ?? 0)
const basePowerSource = computed(() => ((props.card as CardInterface)?.basePower) ?? powerSource.value)

function escapeRegExp(value: string): string {
  return value.replace(/[.*+?^${}()|[\]\\]/g, "\\$&")
}

function escapeHtml(value: string): string {
  return value
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#39;")
}

function getActivationLabel(activation: string): string {
  const labelKey = `card_preview.activation_labels.${activation}`
  const label = t(labelKey)
  return label === labelKey ? activation : label
}

function getActivationName(activation: string): string {
  const nameKey = `card_preview.activation_names.${activation}`
  const name = t(nameKey)
  return name === nameKey ? activation : name
}

const activationTokens = computed(() => ({
  PLAY: [getActivationName("PLAY")],
  ATTACK: [getActivationName("ATTACK")],
  PASSIVE: [getActivationName("PASSIVE")],
  ACTION: [getActivationName("ACTION")],
  DEFEATED: [getActivationName("DEFEATED")],
  LIFE_LOST: [getActivationName("LIFE_LOST")]
}))

const keywordLabels = computed(() => {
  const labels = new Set<string>()
  for (const keyword of Object.keys(keywordIcons)) {
    labels.add(keyword)
    const translated = t(`cards.keywords.${keyword}`)
    if (translated && translated !== `cards.keywords.${keyword}`) {
      labels.add(translated)
    }
  }
  return Array.from(labels).filter(Boolean)
})

const keywordLabelToKey = computed(() => {
  const map: Record<string, string> = {}
  for (const keyword of Object.keys(keywordIcons)) {
    map[keyword] = keyword
    const translated = t(`cards.keywords.${keyword}`)
    if (translated && translated !== `cards.keywords.${keyword}`) {
      map[translated] = keyword
    }
  }
  return map
})

function highlightKeywords(text: string): string {
  if (!text) return ""
  const labels = keywordLabels.value.slice().sort((a, b) => b.length - a.length)
  if (labels.length === 0) {
    return escapeHtml(text)
  }
  const pattern = new RegExp(`\\b(${labels.map(escapeRegExp).join("|")})\\b`, "g")
  let result = ""
  let lastIndex = 0
  for (let match = pattern.exec(text); match; match = pattern.exec(text)) {
    const start = match.index
    const end = start + match[0].length
    result += escapeHtml(text.slice(lastIndex, start))
    const label = match[0]
    const keywordKey = keywordLabelToKey.value[label]
    const dataAttr = keywordKey ? ` data-key="${keywordKey}"` : ""
    result += `<strong class="keyword-highlight"${dataAttr}>${escapeHtml(label)}</strong>`
    lastIndex = end
  }
  result += escapeHtml(text.slice(lastIndex))
  return result
}

const activationFromDescription = computed(() => {
  const description = descriptionText.value
  if (!description) return null

  for (const [activation, tokens] of Object.entries(activationTokens.value)) {
    for (const token of tokens) {
      if (!token) continue
      // Match a leading activation token (case-insensitive), then capture the remaining effect text.
      const match = new RegExp(`^${escapeRegExp(token)}\s*[:\-–—]?\s*(.+)$`, "i").exec(description)
      if (match) {
        return {
          name: activation,
          activation: getActivationLabel(activation),
          description: match[1].trim(),
        }
      }
    }
  }
  return null
})

const activationData = computed(() => {
  const items: { name?: string; activation: string; descriptionHtml: string }[] = []
  const stripLeadingPunctuation = (value: string): string =>
    value.replace(/^[\s:–—-]+/, "").trim()
  if (activationFromDescription.value) {
    items.push({
      name: getActivationName(activationFromDescription.value.name),
      activation: activationFromDescription.value.activation,
      descriptionHtml: highlightKeywords(stripLeadingPunctuation(activationFromDescription.value.description))
    })
  } else if (descriptionText.value && !isFlavorText.value) {
    items.push({
      name: getActivationName("PASSIVE"),
      activation: getActivationLabel("PASSIVE"),
      descriptionHtml: highlightKeywords(stripLeadingPunctuation(descriptionText.value))
    })
  }
  return { items, descriptionAttached: items.length > 0 }
})

function resolveCardName(cardId: number): string {
  const key = `cards.${cardId}.name`
  const translated = t(key)
  if (translated !== key) {
    return translated
  }
  return t("card_preview.card_number", {id: cardId})
}

const evolutionInfo = computed(() => {
  const targetId = cardDetails.value?.evolutionId
  if (targetId == null || !setCards.value) return null
  const target = setCards.value.find((c) => c.id === targetId)
  return {
    targetName: target ? resolveCardName(target.id) : resolveCardName(targetId)
  }
})

const evolutionFrom = computed(() => {
  const parentId = cardDetails.value?.parentId
  if (parentId == null || !setCards.value) return null
  const parent = setCards.value.find((c) => c.id === parentId)
  return parent ? resolveCardName(parent.id) : resolveCardName(parentId)
})

async function loadCardDetails() {
  if (!props.card?.id) {
    return
  }

  try {
    if (!setCards.value) {
      setCards.value = await getAllCards()
    }
  } finally {
  }
}

onMounted(loadCardDetails)
watch(() => props.card?.id, loadCardDetails)

</script>

<template>
  <div class="modal-mask" @click="emit('close')">
    <div class="preview-container" @click="emit('close')">
      <div class="preview-content" @click.stop>
        <button type="button" class="close-button" aria-label="Close" @click="emit('close')">
          <span aria-hidden="true">&times;</span>
        </button>
        <img
          :src="getCardImage(card.id)"
          :alt="t(getCardAlt(card))"
          class="preview-card"
          draggable="false"
        />

        <div class="preview-text">
          <h3 class="card-title">{{ cardName }}</h3>
          <div v-if="!activationData.descriptionAttached && descriptionSource" class="card-description" v-html="descriptionSource"></div>
          <div class="section">
            <div class="section-title">{{ t("card_preview.sections.power") }}</div>
            <div class="muted">
              {{ powerSource }}
              <span v-if="basePowerSource !== powerSource"> ({{ t("card_preview.power.base") }} {{ basePowerSource }})</span>
            </div>
          </div>
          <div v-if="evolutionInfo || evolutionFrom" class="evolution">
            <div class="section-title">{{ t("card_preview.sections.evolution") }}</div>
            <div v-if="evolutionFrom" class="muted">{{ t("card_preview.evolution.from", {name: evolutionFrom}) }}</div>
            <div v-if="evolutionInfo" class="muted">
              {{ t("card_preview.evolution.into", {name: evolutionInfo.targetName}) }}
            </div>
          </div>

          <div class="section">
            <div class="section-title">{{ t("card_preview.sections.keywords") }}</div>
            <div v-if="keywordItems.length === 0" class="muted">{{ t("card_preview.keywords.none") }}</div>
            <ul v-else class="list">
              <li v-for="item in keywordItems" :key="item.keyword">
                <span class="keyword-row">
                  <img v-if="item.icon" :src="item.icon" :alt="item.label" class="keyword-icon" />
                  <strong :class="{ inactive: item.inactive }">{{ item.label }}</strong>
                </span>
                <span class="keyword-text" :class="{ inactive: item.inactive }">— {{ item.description }}</span>
                <span v-if="item.inactive" class="keyword-status">{{ t("card_preview.keywords.tough_used") }}</span>
              </li>
            </ul>
          </div>

          <div class="section">
            <div class="section-title">{{ t("card_preview.sections.activation") }}</div>
            <div v-if="activationData.items.length === 0" class="muted">{{ t("card_preview.activation.none") }}</div>
            <ul v-else class="list">
              <li v-for="(effect, effectIndex) in activationData.items" :key="effect.activation + '-' + effectIndex">
                <strong v-if="effect.name">{{ effect.name }}</strong>
                <span v-if="effect.name"> — </span>
                {{ effect.activation }}
                <template v-if="effect.descriptionHtml">
                  <br />
                  <span class="activation-description" v-html="`• ${effect.descriptionHtml}`"></span>
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
  font-size: 30px;
  font-weight: 700;
  text-transform: uppercase;
  margin-bottom: 12px;
}

.card-description {
  color: #d8d8d8;
  font-size: 14px;
  line-height: 1.5;
  font-style: italic;
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

.activation-description :deep(.keyword-highlight) {
  position: relative;
  font-weight: 700;
  padding-left: 22px;
}

.activation-description :deep(.keyword-highlight)::before {
  content: "";
  position: absolute;
  left: 0;
  top: 50%;
  width: 16px;
  height: 16px;
  transform: translateY(-50%);
  background-size: contain;
  background-repeat: no-repeat;
  background-position: center;
  opacity: 0.95;
}

.activation-description :deep(.keyword-highlight[data-key="FRENZY"])::before {
  background-image: url("@/assets/cards/KeywordIcons/FRENZY.png");
}

.activation-description :deep(.keyword-highlight[data-key="HUNTER"])::before {
  background-image: url("@/assets/cards/KeywordIcons/HUNTER.png");
}

.activation-description :deep(.keyword-highlight[data-key="POISONOUS"])::before {
  background-image: url("@/assets/cards/KeywordIcons/POISONOUS.png");
}

.activation-description :deep(.keyword-highlight[data-key="SNEAKY"])::before {
  background-image: url("@/assets/cards/KeywordIcons/SNEAKY.png");
}

.activation-description :deep(.keyword-highlight[data-key="TOUGH"])::before {
  background-image: url("@/assets/cards/KeywordIcons/TOUGH.png");
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
