<script setup lang="ts">

import {computed, ref, Ref} from "vue";

const emit = defineEmits(['button-clicked'])

const playerName: Ref<string> = ref(undefined)

const isButtonDisabled = computed(() => {
  return !(playerName.value?.length >= 3)
})

function onButtonClick() {
  emit('button-clicked', playerName.value)
}

</script>

<template>
  <div class="modal-mask">
    <div class="modal-container">
      <div class="modal-header">
        <h5 class="modal-title">S'authentifier</h5>
      </div>
      <div class="modal-body">
        <input v-model="playerName" placeholder="pseudo"/>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" @click="onButtonClick()" :disabled="isButtonDisabled">
          Démarrer
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>

.modal-mask {
  position: fixed;
  z-index: 9998;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
}

.modal-container {
  width: 250px;
  margin: 150px auto;
  padding: 20px 30px;
  background-color: #fff;
  border-radius: 2px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.33);
}

.modal-header {
  justify-content: center;
}

.modal-body {
  display: flex;
  padding: 10px 0;
  justify-content: center;
}

.modal-footer {
  display: flex;
  justify-content: center;
}

</style>
