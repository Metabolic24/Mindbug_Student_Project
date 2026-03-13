<script setup lang="ts">

interface Player {
  name: string
  mindbugCount: number
  drawPileCount: number
}

interface Props {
  teamLife: number
  ally: Player
  player: Player
}

defineProps<Props>()

function getAvatar(name: string = "default") {
  const url = new URL("@/assets/avatars/", import.meta.url)
  return `${url}/${name}.jpg`
}

</script>

<template>

<div class="teamContainer">

  <!-- LEFT PLAYER -->
  <div class="playerSide left">

    <div class="mindbugs">
      <img
        v-for="n in ally.mindbugCount"
        :key="n"
        src="@/assets/profil-in-game/mindbug.png"
        class="mindbug"
      />
    </div>

    <div class="drawPile">
      <img src="@/assets/profil-in-game/cardback-pile.png" class="pile"/>
      <span class="pileCount">{{ ally.drawPileCount }}</span>
    </div>

    <div class="playerProfile">
      <img :src="getAvatar()" class="avatar"/>
      <span class="name">{{ ally.name }}</span>
    </div>

  </div>


  <!-- TEAM LIFE -->
  <div class="teamLife">

    <img src="@/assets/profil-in-game/hearts-game.svg" class="heart"/>
    <span class="lifeText">{{ teamLife }}</span>

  </div>


  <!-- RIGHT PLAYER -->
  <div class="playerSide right">

    <div class="playerProfile">
      <img :src="getAvatar()" class="avatar"/>
      <span class="name">{{ player.name }}</span>
    </div>

    <div class="drawPile">
      <img src="@/assets/profil-in-game/cardback-pile.png" class="pile"/>
      <span class="pileCount">{{ player.drawPileCount }}</span>
    </div>

    <div class="mindbugs">
      <img
        v-for="n in player.mindbugCount"
        :key="n"
        src="@/assets/profil-in-game/mindbug.png"
        class="mindbug"
      />
    </div>

  </div>

</div>

</template>

<style scoped>

.teamContainer{
  display:flex;
  align-items:center;
  justify-content:center;
  gap:2vw;
  width:100%;
}

.playerSide{
  display:flex;
  align-items:center;
  gap:10px;
}

/* avatars */

.playerProfile{
  display:flex;
  flex-direction:column;
  align-items:center;
}

.avatar{
  width:4vw;
  height:4vw;
  border-radius:50%;
  border:2px solid black;
}

.name{
  font-size:1.6vh;
  font-weight:bold;
  color: black;
  background:white;
  padding:2px 6px;
  border-radius:5px;
}

/* team life */

.teamLife{
  position:relative;
  width:4vw;
  height:4vw;
  display:flex;
  align-items:center;
  justify-content:center;
}

.heart{
  width:100%;
}

.lifeText{
  position:absolute;
  font-size:2vh;
  font-weight:bold;
  color:white;
}

/* draw pile */

.drawPile{
  position:relative;
}

.pile{
  width:2.5vw;
}

.pileCount{
  position:absolute;
  top:50%;
  left:60%;
  transform:translate(-50%,-50%);
  font-size:1.5vh;
  color:white;
  background:black;
  border-radius:50%;
  padding:2px 5px;
}

/* mindbugs */

.mindbugs{
  display:flex;
  gap:3px;
}

.mindbug{
  width:1.5vw;
}

</style>