<script setup lang="ts">
import {onMounted, ref, Ref} from "vue";
import {getAvailableSets} from "@/shared/RestService";
import {getSetImage} from "@/shared/CardUtils";
import {useI18n} from "vue-i18n";

const { t } = useI18n();

// Declare a list that will contain all the names of the available sets
let sets: Ref<string[]> = ref([])

onMounted(async () => {
  // Get the list of available sets from the server
  sets.value = await getAvailableSets()
})
</script>

<template>
  <nav @contextmenu.prevent>
    <router-link to="/">{{ t('router.home') }}</router-link>
  </nav>
  <div id="cards-sets" @contextmenu.prevent>
    <h1>{{ t('card_sets.title') }}</h1>
    <div id="cards-sets-container">
      <router-link :to="`/createSet`" class="cards-set-link">
        <div class="cards-set">
          <img :src="getSetImage('')" :alt="t('router.create_set')" :title="t('router.create_set')"/>
        </div>
      </router-link>
      <router-link v-for="(set, index) in sets" :key="set" :to="`/sets/${set}?custom=${index >= 2}`" class="cards-set-link">
        <div class="cards-set">
          <img v-if="index < 2" :src="getSetImage(set)" :alt="t('card_sets.' + set)"/>
          <h2 v-if="index >= 2">{{set}}</h2>
        </div>
      </router-link>
    </div>
  </div>
</template>

<style scoped>
nav {
  position: absolute;
}

#cards-sets {
  text-align: center;
  padding-top: 3%;
  height: 97%;

  display: flex;
  flex-direction: column;
  align-items: center;

  h1 {
    margin-bottom: 7%;
    font-size: xxx-large;
  }
}

#cards-sets-container {
  width: 97%;
  height: 50%;
  padding: 1%;

  display: flex;
  flex-wrap: wrap;
  gap: 15px;

  background-color: #f4f7fb;
  border-radius: 12px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.cards-set-link {
  width: 16%;
  height: 100%;
}

.cards-set {
  width: 100%;
  height: 100%;

  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  overflow: hidden;

  background-color: white;
  border-radius: 12px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;

  img {
    width: 100%;
    height: 100%;
  }

  h2 {
    width: 100%;
    overflow-wrap: break-word;
  }
}

.cards-set:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.2);
}
</style>
  