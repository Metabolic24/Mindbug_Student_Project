<script setup lang="ts">
import BoardButtons from "@/components/game/board/BoardButtons.vue"

interface Player {
  name: string
  mindbugCount: number
  drawPileCount: number
}

interface Props {
  teamLife: number
  ally: Player
  player: Player
  isEnemy?: boolean

  gameState: GameStateInterface
  selectedCard: SelectedCardInterface
  pickedCard: CardInterface
  attackingCard: CardInterface
}

const props = defineProps<Props>()

const emit = defineEmits(['button-clicked']);

function getAvatar(name: string = "default") {
  const url = new URL("@/assets/avatars/", import.meta.url)
  return `${url}/${name}.jpg`
}

</script>

<template>

<div class="teamWrapper" :class="{ enemy: props.isEnemy }">

  <!-- 🔵 DEMI-CERCLE -->
  <div class="halfCircle">

    <!-- ❤️ LIFE -->
    <div class="life">
      <img src="@/assets/profil-in-game/hearts-game.svg"/>
      <span>{{ teamLife }}</span>
    </div>

    <!-- 👤 CONTENU HORIZONTAL -->
    <div class="innerRow">

      <!-- ALLY -->
      <div class="playerBlock">
        <img :src="getAvatar()" class="avatar"/>
        <span>{{ ally.name }}</span>
      </div>

      <!-- PLAYER -->
      <div class="playerBlock">
        <img :src="getAvatar()" class="avatar"/>
        <span>{{ player.name }}</span>
      </div>

    </div>

  </div>

  <!-- ⬅️ OUTSIDE LEFT -->
  <div class="outside left">

    <div class="mindbugs">
      <img v-for="n in ally.mindbugCount" :key="n"
           src="@/assets/profil-in-game/mindbug.png"/>
    </div>

    <div class="drawPile">
      <img src="@/assets/profil-in-game/cardback-pile.png"/>
      <span>{{ ally.drawPileCount }}</span>
    </div>

  </div>

  <!-- ➡️ OUTSIDE RIGHT -->
  <div class="outside right">

    <div class="drawPile">
      <img src="@/assets/profil-in-game/cardback-pile.png"/>
      <span>{{ player.drawPileCount }}</span>
    </div>

    <div class="mindbugs">
      <img v-for="n in player.mindbugCount" :key="n"
           src="@/assets/profil-in-game/mindbug.png"/>
    </div>

  </div>

  <!-- 🔥 BUTTONS -->
    <div v-if="!props.isEnemy" class="buttonsContainer">
      <BoardButtons
        :game-state="gameState"
        :selected-card="selectedCard"
        :picked-card="pickedCard"
        :attacking-card="attackingCard"
        @button-clicked="emit('button-clicked', $event)"
      />
    </div>

</div>

</template>

<style scoped>
.teamWrapper{
  position:relative;
  width:100%;
  height:100%;
  background:rgba(200,200,200,0.95);
}

/* 🔵 DEMI-CERCLE */
.halfCircle{
  position:absolute;
  left:50%;
  top:50%;
  transform:translate(-50%,-50%);

  width:17vw;
  height:10vw;

  background:rgba(200,200,200,0.95);
  border:3px solid black;

  border-radius:14vw 14vw 0 0; /* demi cercle */
  
  display:flex;
  flex-direction:column;
  align-items:center;
  justify-content:flex-start;
}

/* ❤️ LIFE */
.life{
  position:absolute;
  top:5%;
  display:flex;
  align-items:center;
  justify-content:center;
}

.life img{
  width:3.5vw;
}

.life span{
  position:absolute;
  color:white;
  font-weight:bold;
}

/* 👥 ligne interne */
.innerRow{
  position:absolute;
  bottom:25%;

  width:100%;

  display:flex;
  justify-content:space-around;
  align-items:center;
}

/* joueurs */
.playerBlock{
  display:flex;
  flex-direction:column;
  align-items:center;
}

.avatar{
  width:3.5vw;
  height:3.5vw;
  border-radius:50%;
  border:2px solid black;
}

.playerBlock span{
  font-size:1.4vh;
  font-weight:bold;
  background:white;
  padding:2px 5px;
  border-radius:4px;
  color:black;
}

/* 🔴 DEMI CERCLE INVERSÉ */
.enemy .halfCircle{
  border-radius:0 0 14vw 14vw; /* inversé */
}

/* ❤️ LIFE en bas */
.enemy .life{
  top:auto;
  bottom:5%;
}

/* 👥 avatars remontés */
.enemy .innerRow{
  bottom:auto;
  top:20%;
}

/* EXTÉRIEUR */
.outside{
  position:absolute;
  top:50%;
  transform:translateY(-50%);
  display:flex;
  align-items:center;
  gap:8px;
}

.left{
  left:32%;
}

.right{
  right:32%;
}

/* pile */
.drawPile{
  position:relative;
}

.drawPile img{
  width:4vw;
}

.drawPile span{
  position:absolute;
  top:50%;
  left:60%;
  transform:translate(-50%,-50%);
  font-size:1.3vh;
  background:black;
  color:white;
  border-radius:50%;
  padding:2px 5px;
}

/* mindbugs */
.mindbugs{
  display:flex;
  gap:3px;
}

.mindbugs img{
  width:2vw;
}

.buttonsContainer{
  position:absolute;

  right:18%;  /* ajuste selon ton écran */
  top:50%;
  transform:translateY(-50%);

  display:flex;
  align-items:center;
  justify-content:center;
}

.buttonsContainer :deep(.buttons){
  flex-direction: row;
  gap:1vw;

  width:auto;
  height:auto;
}

.buttonsContainer :deep(button){
  width: fit-content;
  min-width: 5vw;

  padding:0.5vw;
  font-size:1.5vh;
}
</style>