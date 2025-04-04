<script setup lang="ts">
interface Props {
  value: CardInterface
}

defineProps<Props>()
</script>

<template>
  <div class="card">
    <div class="card-body">
      <h5 class="card-title">{{ value?.name }}</h5>
      <h6 class="card-subtitle"> {{ value?.keywords }}</h6>
      <p class="card-text"> {{ value?.effect }}</p>
    </div>
  </div>
</template>

<style scoped>
.card {
  height: 200px;
  width: 150px;
  margin-left: 5px;
  margin-right: 5px;
}
.card-title,.card-subtitle {
  margin-bottom:15px;
  text-align:center;
}
.card-text {
  margin-top: 15px;
  text-align: center;
}
</style>
