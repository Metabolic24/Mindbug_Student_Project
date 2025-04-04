<script setup lang="ts">
interface Props {
  count: Number
}

defineProps<Props>()
</script>

<template>
  <div class="card">
    <div class="card-title">Défausse</div>
    <span>{{count}}</span>
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
