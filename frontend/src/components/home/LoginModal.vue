<script setup lang="ts">
import {computed, ref, Ref} from "vue";

// Declare events emitted by this component
const emit = defineEmits(['button-clicked'])

const playerName: Ref<string> = ref(undefined)

// Disable login button if nickname is too short
const isButtonDisabled = computed(() => {
  return !(playerName.value?.length >= 3)
})
</script>

<template>
  <div class="modal-mask">
    <div class="modal-container">
      <div class="modal-header">
        <h5 class="modal-title">Choose a nickname</h5>
      </div>
      <div class="modal-body">
        <input v-model="playerName" placeholder="nickname"/>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" @click="emit('button-clicked', playerName)" :disabled="isButtonDisabled">
          Start
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modal-container {
  width: 250px;

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
</style>
