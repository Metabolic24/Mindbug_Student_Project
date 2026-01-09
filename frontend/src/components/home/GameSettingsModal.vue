<script setup lang="ts">
import {computed, onMounted, ref, Ref} from "vue";
import {getAvailableSets} from "@/shared/RestService";

// Declare a list that will contain all the names of the available sets
let sets: Ref<string[]> = ref([])
let selectedSets: Ref<string[]> = ref([])

// Retrieve the image corresponding to the given set
function getSetImage(set: string) {
  const url = new URL("@/assets/sets/", import.meta.url)
  return `${url}/${set}.png`
}

// Retrieve the image corresponding to the given set
function updateSelection(set: string) {
  const index = selectedSets.value.indexOf(set)

  if (index == -1) {
    selectedSets.value.push(set)
  } else {
    selectedSets.value.splice(index, 1)
  }
}

function getSetClasses(set: string): Record<string, boolean> {
  return ({
    'set-card': true,
    'selected': selectedSets.value.includes(set),
  })
}

onMounted(async () => {
  // Get the list of available sets from the server
  sets.value = await getAvailableSets()
})

// Declare events emitted by this component
const emit = defineEmits(['button-clicked'])

// Disable login button if nickname is too short
const isButtonDisabled = computed(() => {
  return selectedSets.value.length == 0
})

</script>

<template>
  <div class="modal-mask">
    <div class="modal-container">
      <div class="modal-header">
        <h5 class="modal-title">Choose one or more sets you want to play</h5>
      </div>
      <div class="modal-body">
        <div class="sets-container">
          <div :class="getSetClasses(set)" v-for="set in sets" :key="set">
            <img :src="getSetImage(set)" :alt="set" @click="updateSelection(set)"/>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" @click="emit('button-clicked', selectedSets)" :disabled="isButtonDisabled">
          Search
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modal-container {
  width: 80%;

  margin: 150px auto;
  padding: 20px 30px;
}

.modal-header {
  justify-content: center;
}

.modal-body {
  display: flex;
  justify-content: center;

  padding: 10px 0;
}

.modal-footer {
  display: flex;
  justify-content: center;
}

.sets-container {
  display: flex;
  flex-wrap: wrap;

  padding: 20px;
  gap: 20px;

  background-color: #f4f7fb;

  border-radius: 12px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.set-card {
  width: 180px;
  height: 225px;

  display: flex;
  flex-direction: column;
  align-items: center;
  overflow: hidden;

  background-color: white;
  border-radius: 12px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.set-card:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.2);
}

.set-card.selected {
  border: 4px solid red;
}

.set-card img {
  width: 100%;
  height: 100%;
}
</style>
