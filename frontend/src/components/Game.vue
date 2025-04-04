<script setup lang="ts">
import Board from "@/components/game/Board.vue";
import Hand from "@/components/game/Hand.vue";
import PlayerDetails from "@/components/game/PlayerDetails.vue";

const exampleCard1: CardInterface = {
  name: "Gorillion",
  keywords: [],
  effect: ""
}

const exampleCard2: CardInterface = {
  name: "Tigrécureuil",
  keywords: ["FURTIF"],
  effect: "Détruisez une carte ennemie de puissance 7 ou plus"
}

const exampleCard3: CardInterface = {
  name: "Furet Saboteur",
  keywords: ["FURTIF"],
  effect: "Votre adversaire défausse 2 cartes"
}


const hand1: CardInterface[] = [exampleCard3, exampleCard1];
const hand2: CardInterface[] = [exampleCard1, exampleCard2, exampleCard3];

</script>

<template>
  <nav>
    <router-link to="/">Home</router-link>
  </nav>
  <div class="container" style="width: 100%;height:100%">
    <div class="row" style="background-color: red;width: 100%;height:10%">
      <div class="col-2" style="background-color: orange">
        <player-details name="Player2" life-points="3" draw-pile-count="5" :mindbug-count="2"></player-details>
      </div>
      <div class="col-8" style="background-color: yellow">
        <hand :cards="hand1"></hand>
      </div>
      <div class="col-2" style="background-color: black"></div>
    </div>
    <board></board>
    <div class="row" style="background-color: red;width: 100%;height:10%">
      <div class="col-2" style="background-color: orange;height: 100%">
        <player-details name="Player1" life-points="3" draw-pile-count="5" :mindbug-count="2"></player-details>
      </div>
      <div class="col-8" style="background-color: yellow;height: 100%">
        <hand :cards="hand2"></hand>
      </div>
      <div class="col-2" style="background-color: black;height: 100%"></div>
    </div>
  </div>
</template>


<style scoped>

</style>
