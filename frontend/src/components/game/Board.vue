<script setup lang="ts">

import Card from "@/components/game/Card.vue";
import DiscardPile from "@/components/game/DiscardPile.vue";

interface Props {
  playerBoard: CardInterface[]
  playerDiscard: CardInterface[]
  opponentBoard: CardInterface[]
  opponentDiscard: CardInterface[]
}

defineProps<Props>()

</script>

<template>
    <div class="row" style="width: 100%;height: 40%">
      <div class="col-2" style="background-color: green"> </div>
      <div class="col-8" style="background-color: yellow">
        <card v-for="card in opponentBoard" :value="card"> </card>
      </div>
      <div class="col-2" style="background-color: green">
        <discard-pile :cards="opponentDiscard"></discard-pile>
      </div>
    </div>
    <div class="row" style="width: 100%;height: 40%">
      <div class="col-2" style="background-color: green">
        <discard-pile :cards="playerDiscard"></discard-pile>
      </div>
      <div class="col-8" style="background-color: yellow">
        <card v-for="card in playerBoard" :value="card"> </card>
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
