<script setup lang="ts">
import {Store, useStore} from "vuex";
import {useRouter} from "vue-router";
import {computed, onUnmounted, Ref, ref} from "vue";
import GameSettingsModal from "@/components/home/GameSettingsModal.vue";

// Retrieve VueX store to get player data
const store: Store<AppState> = useStore()
// Retrieve VueRouter to change the current route when a game is found
const router = useRouter()

// Declare boolean to disable search button when search is already running
const searchDisabled: Ref<boolean> = ref(false);

// Computed value for search button label
const searchLabel = computed(() => {
  return searchDisabled.value ? "Searching players..." : "Play"
})

let wsConnection: WebSocket;
let isModalVisible: Ref<boolean> = ref(false);

// Triggered when search button is clicked
function searchGame(selectedSets: string[]) {
  isModalVisible.value = false;
  searchDisabled.value = true;

  try {
    // Connect to 'join' WebSocket so the server can detect that the player is looking for a game
    wsConnection = new WebSocket("ws://localhost:8080/ws/join?playerId=" + store.state.playerData?.uuid + "&playerName=" + store.state.playerData?.name + "&sets=" + selectedSets.join(","));
    wsConnection.onmessage = (event: MessageEvent<string>) => {
      // Change route to 'Game' one as the server found a game
      router.push({name: "Game", query: {gameId: event.data}});

      // Close the WS connection and enable the search button (even if we are no more in the 'Home' route)
      wsConnection.close()
      searchDisabled.value = false;
    }
  } catch (error) {
    // TODO Améliorer l'affichage des erreurs
    alert("Connexion perdue avec le serveur")
    searchDisabled.value = false;
  }
}

function displayModal() {
  isModalVisible.value = true;
}

onUnmounted(() => {
  if (wsConnection) {
    wsConnection.close();
  }
})
</script>

<template>
  <button class="styled-button" @click="displayModal()" :disabled="searchDisabled">{{searchLabel}}</button>
  <game-settings-modal v-if="isModalVisible" @button-clicked="searchGame($event)"></game-settings-modal>
</template>

<style scoped>

</style>
