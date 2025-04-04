<script setup lang="ts">
interface Props {
  name: String
  lifePoints: Number
  mindbugCount: Number
  drawPileCount: Number
}

defineProps<Props>()
</script>

<template>
  <div style="width:100px;height:50px;">
    <p>{{ name }}</p>
    <p>{{ lifePoints }} LP / {{ mindbugCount }} MB </p>
    <p>{{ drawPileCount }} cards left</p>
  </div>
</template>

<style scoped>
  p {
    margin-bottom: 0;
  }
</style>