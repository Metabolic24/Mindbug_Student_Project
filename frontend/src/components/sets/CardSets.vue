<template>
  <nav>
    <router-link to="/">Home</router-link>
  </nav>
  <div id="sets">
    <h1>Select a set of cards</h1>
    <div class="sets-container">
      <router-link v-for="set in sets" :key="set" :to="`/sets/${set}`">
        <div class="set-card">
          <img :src="getSetImage(set)" :alt="set"/>
          <h2>{{ set }}</h2>
        </div>
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">

import {onMounted, ref, Ref} from "vue";
import {getAvailableSets} from "@/shared/RestService";

let sets: Ref<string[]> = ref([])

function getSetImage(set: string) {
  const url = new URL("@/assets/sets/", import.meta.url)
  return `${url}/${set}.png`
}

onMounted(async () => {
  sets.value = await getAvailableSets()
})

</script>

<style scoped>

#sets {
  text-align: center;
}

.sets-container {
  display: flex;
  flex-wrap: wrap;
  padding: 20px;
  background-color: #f4f7fb;
  border-radius: 12px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

h2 {
  font-size: 24px;
  color: #2c3e50;
  margin-bottom: 20px;
  text-align: center;
}

.set-card {
  width: 180px;
  height: 225px;

  display: flex;
  flex-direction: column;
  align-items: center;

  background-color: white;
  border-radius: 12px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.set-card:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.2);
}

.set-card img {
  width: 100%;
  height: 100%;
}

</style>
  