<script setup lang="ts">
import {Store, useStore} from "vuex";
import {useRouter} from "vue-router";
import {computed, Ref, ref} from "vue";

// Retrieve VueX store to get player data
const store: Store<AppState> = useStore()
// Retrieve VueRouter to change the current route when a game is found
const router = useRouter()

// Declare boolean to disable search button when search is already running
const searchDisabled: Ref<boolean> = ref(false);

// Computed value for search button label
const searchLabel = computed(() => {
  return searchDisabled.value ? "Recherche en cours..." : "Rechercher une partie"
})

// Triggered when search button is clicked
function searchGame() {
  searchDisabled.value = true;

  try {
    // Connect to 'join' WebSocket so the server can detect that the player is looking for a game
    const connection = new WebSocket("ws://localhost:8080/ws/join?playerId=" + store.state.playerData?.uuid + "&playerName=" + store.state.playerData?.name);
    connection.onmessage = (event: MessageEvent<string>) => {
      // Change route to 'Game' one as the server found a game
      router.push({name: "Game", query: {gameId: event.data}});

      // Close the WS connection and enable the search button (even if we are no more in the 'Home' route)
      connection.close()
      searchDisabled.value = false;
    }
  } catch (error) {
    // TODO Améliorer l'affichage des erreurs
    alert("Connexion perdue avec le serveur")
    searchDisabled.value = false;
  }
}
</script>

<template>
  <button class="styled-button" @click="searchGame()" :disabled="searchDisabled">{{searchLabel}}</button>
</template>

<style scoped>

</style>
