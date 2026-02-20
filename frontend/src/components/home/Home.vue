<script setup lang="ts">
import LoginModal from "@/components/home/LoginModal.vue";
import {getPlayerData} from "@/shared/RestService";
import {Store, useStore} from "vuex";
import SearchButton from "@/components/home/SearchButton.vue";

// Retrieve VueX store to save player data
const store: Store<AppState> = useStore()

// Triggered when the player has chosen his/her nickname
async function onLogin(name: string) {
  const playerData = await getPlayerData(name);
  store.commit('savePlayerData', playerData);
}
</script>

<template>
  <div id="home">
    <h1>Welcome to Mindbug App</h1>
    <div class="button-group">
      <search-button></search-button>
      <router-link to="/sets" class="styled-button">Available Sets</router-link>
    </div>
  </div> 
   <login-modal v-if="!store.state.playerData" @button-clicked="onLogin($event)"></login-modal>
</template>

<style scoped>
#home {
  text-align: center;
  margin-top: 50px;
}

h1 {
  margin-bottom: 40px;
}

.button-group {
  display: flex;
  justify-content: center;
  gap: 20px;
}
</style>
