<script setup lang="ts">
import LoginModal from "@/components/home/LoginModal.vue";
import {getPlayerData} from "@/shared/RestService";
import {Store, useStore} from "vuex";
import SearchButton from "@/components/home/SearchButton.vue";
import {useI18n} from "vue-i18n";

const { t } = useI18n();

// Retrieve VueX store to save player data
const store: Store<AppState> = useStore()

// Triggered when the player has chosen his/her nickname
async function onLogin(name: string) {
  const playerData = await getPlayerData(name);
  store.commit('savePlayerData', playerData);
}
</script>

<template>
  <div id="home" @contextmenu.prevent>
    <h1>{{ t("home.title")}}</h1>
    <div id="home-buttons">
      <search-button></search-button>
      <router-link to="/sets" class="styled-button">{{t("router.available_sets")}}</router-link>
    </div>
  </div>
  <login-modal v-if="!store.state.playerData" @button-clicked="onLogin($event)"></login-modal>
</template>

<style scoped>
#home {
  text-align: center;
  padding-top: 3%;
  height: 100%;

  h1 {
    margin-bottom: 7%;
    font-size: xxx-large;
  }
}

#home-buttons {
  width: 100%;
  height: 40%;

  display: flex;
  justify-content: center;
  gap: 5%;
}

</style>
