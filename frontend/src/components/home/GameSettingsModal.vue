<script setup lang="ts">
import {computed, onMounted, ref, Ref} from "vue";
import {getAvailableAiLevels, getAvailableSets} from "@/shared/RestService";
import {getSetImage} from "@/shared/CardUtils";
import {useI18n} from "vue-i18n";

const { t } = useI18n();

// Declare a list that will contain all the names of the available sets
let sets: Ref<string[]> = ref([])
let selectedSets: Ref<string[]> = ref([])

// Declare the variable that will retrieve offline checkbox value
let offline: Ref<boolean> = ref(false)
let levels: Ref<string[]> = ref([])
let selectedLevel: Ref<string> = ref("")

let mode: Ref<string | null> = ref(null)
let step: Ref<number> = ref(1)

// Retrieve the image corresponding to the given set
function updateSelection(set: string) {
  const index = selectedSets.value.indexOf(set)

  if (index == -1) {
    selectedSets.value.push(set)
  } else {
    selectedSets.value.splice(index, 1)
  }
}

function chooseMode(selectedMode: string) {
  mode.value = selectedMode
  // empêcher offline si 2v2
  if (mode.value === "team") {
    offline.value = false
  }
  step.value = 2
}

function goBack() {
  step.value = 1
  selectedSets.value = []
  mode.value=""
}

function getSetClasses(set: string): Record<string, boolean> {
  return ({
    'cards-set': true,
    'selected': selectedSets.value.includes(set),
  })
}

onMounted(async () => {
  // Get the list of available sets from the server
  sets.value = await getAvailableSets()
  levels.value = await getAvailableAiLevels()
})

// Declare events emitted by this component
const emit = defineEmits(['button-clicked'])

// Disable login button if nickname is too short
const isButtonDisabled = computed(() => {
  if (step.value === 1) return mode.value === null
      return selectedSets.value.length == 0 || (offline.value && !selectedLevel.value)
})

function onButtonClicked() {
  emit('button-clicked', {
    sets: selectedSets.value,
    offline: offline.value,
    level: selectedLevel.value,
    mode: mode.value
  } as GameSettingsInterface)
}

</script>

<template>
  <div class="modal-mask">
    <div class="modal-container">
      <div class="modal-header">
        <h5 class="modal-title">{{ t("modal.game_settings.title")}}<p v-if="mode"> {{ t("modal.game_settings.selected")}} <strong>{{ mode === "duel" ? "1v1 Duel" : "2v2 Team" }}</strong></p></h5>
      </div>
      <div class="modal-body">
        <!-- STEP 1 : MODE CHOICE -->
        <div v-if="step === 1" class="mode-container">
          <button class="mode-button" @click="chooseMode('duel')">
            1v1 Duel
          </button>

          <button class="mode-button" @click="chooseMode('team')">
            2v2 Team
          </button>
        </div>

        <!-- STEP 2 : SETS CHOICE -->
        <div v-if="step === 2" id="cards-sets-container">
          <div :class="getSetClasses(set)" v-for="(set, index) in sets" :key="set" @click="updateSelection(set)">
            <img v-if="index < 2" :src="getSetImage(set)" :alt="t('card_sets.' + set)"/>
            <h2 v-if="index >= 2">{{ set }}</h2>
          </div>
        </div>

      </div>
      <div class="modal-footer">
        <button v-if="step === 2" class="btn btn-secondary" @click="goBack()">
          {{ t("modal.game_settings.back") }}
        </button>
        <button v-if="step === 2" type="button" class="btn btn-primary" @click="onButtonClicked()" :disabled="isButtonDisabled">
          {{ t("modal.game_settings.button") }}
        </button>
        <div v-if="mode === 'duel'" id="offline_div">
          <input type="checkbox" v-model="offline" id="offline_checkbox" />
          <label id="offline_label" for="offline_checkbox"> {{ t("modal.game_settings.play_offline") }}</label>
        </div>
        <select v-if="offline" id="ai_level_selector" v-model="selectedLevel">
          <option disabled value="">{{ t("modal.game_settings.level.default") }}</option>
          <option v-for="level in levels" :key="level" :value="level">{{ t("modal.game_settings.level." + level) }}</option>
        </select>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modal-container {
  width: 80%;
  height: 60%;

  display: flex;
  flex-direction: column;
  gap: 2%;

  margin: 10% auto;
  padding: 1% 2%;

  .modal-header {
    justify-content: center;
  }

  .modal-body {
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .modal-footer {
    width: 100%;
    height: 10%;

    display: flex;
    justify-content: center;

    button {
      width: 15%;
      height: 100%;

      font-size: x-large;
    }
  }
}

#cards-sets-container {
  height: 80%;
  width: 100%;
  padding: 1%;

  display: flex;
  flex-wrap: wrap;
  gap: 15px;

  background-color: #f4f7fb;
  border-radius: 12px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);

  .cards-set {
    width: 16%;
    height: 100%;

    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    overflow: hidden;

    background-color: white;
    border-radius: 12px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease, box-shadow 0.3s ease;

    img {
      width: 100%;
      height: 100%;
    }

    h2 {
      width: 100%;
      overflow-wrap: break-word;
      color: hsla(160, 100%, 37%, 1);
    }
  }

  .cards-set:hover {
    transform: scale(1.05);
    box-shadow: 0 6px 12px rgba(0, 0, 0, 0.2);
  }

  .cards-set.selected {
    border: 4px solid red;
  }
}


#offline_div {
  height: 100%;
  width: 10%;

  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 2%;

  input {
    width: 20%;
    height: 40%;

    margin-left: 5%;
  }
}

#offline_label {
  width: 70%;
  font-size: x-large;
  margin-bottom: 2px;
}

#ai_level_selector {
  margin-left: 2%;
  width: 10%;
  height: 50%;

  font-size: large;
}
</style>
