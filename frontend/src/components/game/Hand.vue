<script setup lang="ts">
interface Props {
  cards: CardInterface[]
}

defineProps<Props>()
</script>

<template>
  <div style="display:inline-flex;background-color: green;height: 100%">
    <div v-for="card in cards" class="card">
      <div class="card-body">
        <h5 class="card-title">{{ card.name }}</h5>
      </div>
    </div>
  </div>
</template>

<style scoped>
  .card {
    min-width: 100px;
    max-width: 150px;
    margin-left: 5px;
    margin-right: 5px;
  }
  .card-title {
    text-align: center;
  }
</style>
