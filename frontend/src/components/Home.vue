<template>
  <div id="home">
    <h1>Welcome to Mindbug App</h1>
    <div class="button-group">
      <button :class="searchButtonClass" @click="searchGame()" :disabled="searchDisabled">{{searchLabel}}</button>
      <router-link to="/sets" class="styled-button">Available Sets</router-link>
    </div>
  </div>
  <login-modal v-if="!store.state.playerData" @button-clicked="onLogin($event)"></login-modal>
</template>

<script setup lang="ts">
import LoginModal from "@/components/LoginModal.vue";
import {getPlayerData} from "@/shared/RestService";
import {Store, useStore} from "vuex";
import {useRouter} from "vue-router";
import {computed, Ref, ref} from "vue";

const store: Store<AppState> = useStore()
const router = useRouter()

const searchDisabled: Ref<boolean> = ref(false);

const searchLabel = computed(() => {
  return searchDisabled.value ? "Recherche en cours..." : "Rechercher une partie"
})

const searchButtonClass = computed(() => {
  return searchDisabled.value ? "styled-button" : "styled-button"
})

async function onLogin(name: string) {
  const playerData = await getPlayerData(name);
  store.commit('savePlayerData', playerData);
}

function searchGame() {
  // TODO Réactiver le bouton si la connexion à la WS échoue ou est perdue
  searchDisabled.value = true;
  const connection = new WebSocket("ws://localhost:8080/ws/join?playerId=" + store.state.playerData?.uuid + "&playerName=" + store.state.playerData?.name);
  connection.onmessage = (event: MessageEvent<string>) => {
    router.push({name: "Game", query: {gameId: event.data}});
    connection.close()
    searchDisabled.value = false;
  }
}

</script>

<style scoped>
#home {
  text-align: center;
  margin-top: 50px;
}

h1 {
  font-size: 2.5rem;
  color: #2c3e50;
  margin-bottom: 40px;
}

.button-group {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.styled-button {
  padding: 12px 30px;
  font-size: 18px;
  color: #fff;
  background-color: #3498db;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: background-color 0.3s, transform 0.2s;
}

.styled-button:disabled {
  background-color: #7b7a7a;

  cursor: not-allowed;
  pointer-events: none;
}

.styled-button:hover {
  background-color: #2980b9;
  transform: scale(1.05);
}

.styled-button:active {
  background-color: #1e6f93;
  transform: scale(0.98);
}
</style>
