<script setup lang="ts">
import {computed, ref, Ref} from "vue";
import {useI18n} from "vue-i18n";

const { t } = useI18n();

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
        <h5 class="modal-title">{{ t('modal.login.title') }}</h5>
      </div>
      <div class="modal-body">
        <input v-model="playerName" :placeholder="t('modal.login.name_placeholder')"/>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" @click="emit('button-clicked', playerName)" :disabled="isButtonDisabled">
          {{ t('modal.login.button') }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modal-container {
  width: 15%;
  margin: 15% auto;
  padding: 1%;
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
  justify-content: center;
}
</style>
