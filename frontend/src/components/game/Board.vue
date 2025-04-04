<script setup lang="ts">

import Card from "@/components/game/Card.vue";
import DiscardPile from "@/components/game/DiscardPile.vue";

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

</script>

<template>
    <div class="row" style="width: 100%;height: 40%">
      <div class="col-2" style="background-color: green"> </div>
      <div class="col-8" style="background-color: yellow">
        <card :value="exampleCard1"> </card>
        <card :value="exampleCard2"> </card>
      </div>
      <div class="col-2" style="background-color: green">
        <discard-pile count=1></discard-pile>
      </div>
    </div>
    <div class="row" style="width: 100%;height: 40%">
      <div class="col-2" style="background-color: green">
        <discard-pile count=0></discard-pile>
      </div>
      <div class="col-8" style="background-color: yellow">
        <card :value="exampleCard3"> </card>
      </div>
      <div class="col-2" style="background-color: green">
        <button>Attack</button>
        <button>Use Mindbug</button>
        <button>Block</button>
      </div>
    </div>
</template>

<style scoped>
  .col-8 {
    display: flex;
  }
</style>
