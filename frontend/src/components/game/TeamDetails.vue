<script setup lang="ts">
import BoardButtons from "@/components/game/board/BoardButtons.vue"
import { computed } from "vue"
import Card from "@/components/game/Card.vue";

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

const message = computed(() => {
  if (props.gameState.winner) {
    return props.gameState.winner === props.gameState.player.uuid
      ? "Game Over : You WIN !"
      : "Game Over : You LOSE !"
  }

  if (props.gameState?.choice) {
    const choice = props.gameState.choice
    if (choice.playerToChoose === props.gameState.player.uuid) {
      switch (choice.type) {
        case "FRENZY":  return "Attack again?"
        case "BOOLEAN": return props.pickedCard ? "Use Mindbug?" : choice.message
        case "HUNTER":  return "Choose a target"
      }
    } else {
      const chooser = props.gameState.ally?.uuid === choice.playerToChoose
        ? props.gameState.ally.name
        : props.gameState.opponents?.find(o => o.uuid === choice.playerToChoose)?.name
          ?? "Opponent"
      return `Waiting for ${chooser}...`
    }
  }

  if (props.pickedCard) return "Use Mindbug?"
  if (props.attackingCard) return "Block or Lose LP"
  if (props.gameState?.playerTurn) return "Play or Attack"
  return "Waiting opponent..."
})

</script>

<template>

<div class="teamWrapper" :class="{ enemy: props.isEnemy }">

  <!-- DEMI-CERCLE -->
  <div class="halfCircle">
    
    <Transition name="card-pop">
      <div v-if="pickedCard && !props.isEnemy" class="pickedCardOverlay">
        <Card
          :card="pickedCard"
          context="board"
          visibility="enemy"
          :selected="false"
          :attacking="false"
          :clickable="false"
        />
      </div>
    </Transition>

    <!-- LIFE -->
    <div class="life">
      <img src="@/assets/profil-in-game/hearts-game.svg"/>
      <span>{{ teamLife }}</span>
    </div>
    
    <!-- CONTENU HORIZONTAL -->
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

  <!-- OUTSIDE LEFT -->
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

  <!-- OUTSIDE RIGHT -->
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

  <!-- BUTTONS -->
    <div v-if="!props.isEnemy" class="buttonsContainer">
      <div class="actionText">
        {{ message }}
      </div>
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

/* DEMI-CERCLE */
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

/* LIFE */
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

/* ligne interne */
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

/* DEMI CERCLE INVERSÉ */
.enemy .halfCircle{
  border-radius:0 0 14vw 14vw; /* inversé */
}

/* LIFE en bas */
.enemy .life{
  top:auto;
  bottom:5%;
}

/* avatars remontés */
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

.buttonsContainer {
  position: absolute;
  right: 18%;
  top: 50%;
  transform: translateY(-50%);

  display: flex;
  flex-direction: column; 
  align-items: center;
  justify-content: center;
  gap: 1vh;
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

.infoText {
  max-width: 9vw;
  font-size: 1.2vh;
  font-weight: bold;

  color: white;
  background: rgba(0,0,0,0.65);

  padding: 6px 10px;
  border-radius: 6px;

  text-align: center;
  line-height: 1.2;
}

.actionText {
  position: static;

  font-size: 1.5vh;
  font-weight: bold;
  color: mediumvioletred;

  background: rgba(255,255,255,0.85);
  padding: 4px 10px;
  border-radius: 8px;

  text-align: center;
}

.pickedCardOverlay {
  position: absolute;
  top: -60%;
  left: 50%;
  transform: translateX(-50%);
  z-index: 20;

  width: 5vw;
  height: 7.5vw;

  filter: drop-shadow(0 0 12px rgba(255, 200, 50, 0.8));
}

.pickedCardOverlay :deep(.card-wrapper) {
  width: 5vw;
  height: 7.5vw;
}

/* Animation d'apparition */
.card-pop-enter-active {
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.card-pop-leave-active {
  transition: all 0.2s ease-in;
}
.card-pop-enter-from {
  opacity: 0;
  transform: translateX(-50%) scale(0.5) translateY(20px);
}
.card-pop-leave-to {
  opacity: 0;
  transform: translateX(-50%) scale(0.8) translateY(-10px);
}

/* Midlle card*/ 
.pickedCardOverlay {
  position: absolute;
  top: -60%;
  left: 50%;
  transform: translateX(-50%);
  z-index: 20;

  width: 7vw;
  height: 10vw;

  filter: drop-shadow(0 0 12px rgba(255, 200, 50, 0.8));
}

.pickedCardOverlay :deep(.card-wrapper) {
  width: 7vw;
  height: 10vw;
}

/* Animation d'apparition */
.card-pop-enter-active {
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.card-pop-leave-active {
  transition: all 0.2s ease-in;
}
.card-pop-enter-from {
  opacity: 0;
  transform: translateX(-50%) scale(0.5) translateY(20px);
}
.card-pop-leave-to {
  opacity: 0;
  transform: translateX(-50%) scale(0.8) translateY(-10px);
}

</style>