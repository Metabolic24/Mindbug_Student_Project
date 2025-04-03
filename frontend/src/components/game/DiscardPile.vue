<script setup lang="ts">
interface Props {
  cards: CardInterface[]
}

defineProps<Props>()
</script>

<template>
  <div class="card">
    <div class="card-title">Défausse</div>
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
