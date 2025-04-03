<template>
  <nav>
    <router-link to="/">Home</router-link>
  </nav>
  <div class="container" style="width: 100%;height:100%">
    <div class="row" style="background-color: red;width: 100%;height:10%">
      <div class="col-2" style="background-color: orange">
        <player-details :name="gameState?.opponent?.name" :life-points="gameState?.opponent?.lifePoints"
                        :draw-pile-count="gameState?.opponent?.drawPileCount" :mindbug-count="gameState?.opponent?.mindbugCount">
        </player-details>
      </div>
      <div class="col-8" style="background-color: yellow">
        <hand :cards="gameState?.opponent?.hand"></hand>
      </div>
      <div class="col-2" style="background-color: black"></div>
    </div>

    <board :player-board="gameState?.player?.board" :player-discard="gameState?.player?.discard"
           :opponent-board="gameState?.opponent?.board" :opponent-discard="gameState?.opponent?.discard">
    </board>

    <div class="row" style="background-color: red;width: 100%;height:10%">
      <div class="col-2" style="background-color: orange;height: 100%">
        <player-details :name="gameState?.player?.name" :life-points="gameState?.player?.lifePoints"
                        :draw-pile-count="gameState?.player?.drawPileCount" :mindbug-count="gameState?.player?.mindbugCount">
        </player-details>
      </div>
      <div class="col-8" style="background-color: yellow;height: 100%">
        <hand :cards="gameState?.player?.hand"></hand>
      </div>
      <div class="col-2" style="background-color: black;height: 100%"></div>
    </div>
  </div>
</template>


<script setup lang="ts">
import Board from "@/components/game/Board.vue";
import Hand from "@/components/game/Hand.vue";
import PlayerDetails from "@/components/game/PlayerDetails.vue";
import {onMounted, Ref, ref} from "vue";
import {startGame} from "@/shared/RestService";

let gameState: Ref<GameStateInterface> = ref({
  uuid: undefined,
  player: {
    uuid: undefined,
    name: undefined,
    lifePoints: undefined,
    mindbugCount: undefined,

    drawPileCount: undefined,
    hand: [],
    board: [],
    discard: [],
  },
  opponent: {
    uuid: undefined,
    name: undefined,
    lifePoints: undefined,
    mindbugCount: undefined,

    drawPileCount: undefined,
    hand: [],
    board: [],
    discard: [],
  },
  finished: true
})

onMounted(async() => {
  gameState.value = await startGame()
})
</script>


<style scoped>

</style>
