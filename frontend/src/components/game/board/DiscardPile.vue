<script setup lang="ts">
// Declare the interface for the data given by the parent component
interface Props {
  cards: CardInterface[]
}
const props = defineProps<Props>()

// Declare events emitted by this component
const emit = defineEmits(['clicked'])

// Triggered when the player clicks on the component
function onClick() {
  if (props.cards.length > 0) {
    emit('clicked')
  }
}
</script>

<template>
  <div class="card" @click="onClick()">
    <h4>Défausse</h4>
    <span>{{cards?.length}}</span>
  </div>
</template>

<style scoped>
.card {
  height: 200px;
  min-width: 120px;

  display: flex;
  align-items: center;
  justify-content: center;

  span {
    font-size: xxx-large;
    font-weight: bolder;
  }
}
</style>
