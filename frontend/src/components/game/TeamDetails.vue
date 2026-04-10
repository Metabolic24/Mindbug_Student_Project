<script setup lang="ts">
import BoardButtons from "@/components/game/board/BoardButtons.vue"
import { computed } from "vue"
import Card from "@/components/game/Card.vue";
import { useI18n } from "vue-i18n";

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
  
  onSettingsButtonClick: () => void;
}

const props = defineProps<Props>()
const { t } = useI18n()
const isCurrentPlayerTurn = computed(() => props.gameState?.currentPlayerID === props.gameState?.player.uuid)

const emit = defineEmits(['button-clicked']);

function getAvatar(name: string = "default") {
  const url = new URL("@/assets/avatars/", import.meta.url)
  return `${url}/${name}.jpg`
}

function getWaitingPlayerName(playerId?: string) {
  if (!playerId) {
    return t("game.middle_area.waiting.opponent_fallback")
  }

  if (props.gameState.ally?.uuid === playerId) {
    return props.gameState.ally.name
  }

  return props.gameState.opponents?.find(o => o.uuid === playerId)?.name
    ?? t("game.middle_area.waiting.opponent_fallback")
}

const message = computed(() => {
  if (props.gameState.winners) {
    return props.gameState.winners.includes(props.gameState.player.uuid)
      ? t("game.middle_area.win")
      : t("game.middle_area.lose")
  }

  if (props.gameState?.choice) {
    const choice = props.gameState.choice
    if (choice.playerToChoose === props.gameState.player.uuid) {
      switch (choice.type) {
        case "FRENZY":
          return t("game.middle_area.choice.FRENZY")
        case "BOOLEAN":
          if (props.pickedCard) {
            return t("game.middle_area.choice.mindbug")
          }

          if ("sourceCard" in choice && choice.sourceCard?.id) {
            return t(`game.middle_area.choice.${choice.sourceCard.id}`)
          }

          return t("game.middle_area.choice.mindbug")
        case "HUNTER":
          return t("game.middle_area.choice.HUNTER")
      }
    } else {
      return t("game.middle_area.waiting.text", {
        player: getWaitingPlayerName(choice.playerToChoose)
      })
    }
  }

  if (props.pickedCard) return t("game.middle_area.choice.mindbug")
  if (props.attackingCard) return t("game.middle_area.choice.attacked")
  if (isCurrentPlayerTurn.value) return t("game.middle_area.player_turn")
  return t("game.middle_area.waiting.text", {
    player: getWaitingPlayerName(props.gameState?.currentPlayerID)
  })
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

    <button v-if="props.isEnemy" type="button" class="settings-button" @click="props.onSettingsButtonClick()" :title="props.settingsTooltip">
              <svg xmlns="http://www.w3.org/2000/svg" x="0px" y="0px" viewBox="0 0 122.88 122.878" xml:space="preserve"
                  fill="currentColor">
                <g>
                  <path
                    fill-rule="evenodd"
                    clip-rule="evenodd"
                    d="M101.589,14.7l8.818,8.819c2.321,2.321,2.321,6.118,0,8.439l-7.101,7.101 c1.959,3.658,3.454,7.601,4.405,11.752h9.199c3.283,0,5.969,2.686,5.969,5.968V69.25c0,3.283-2.686,5.969-5.969,5.969h-10.039 c-1.231,4.063-2.992,7.896-5.204,11.418l6.512,6.51c2.321,2.323,2.321,6.12,0,8.44l-8.818,8.819c-2.321,2.32-6.119,2.32-8.439,0 l-7.102-7.102c-3.657,1.96-7.601,3.456-11.753,4.406v9.199c0,3.282-2.685,5.968-5.968,5.968H53.629 c-3.283,0-5.969-2.686-5.969-5.968v-10.039c-4.063-1.232-7.896-2.993-11.417-5.205l-6.511,6.512c-2.323,2.321-6.12,2.321-8.441,0 l-8.818-8.818c-2.321-2.321-2.321-6.118,0-8.439l7.102-7.102c-1.96-3.657-3.456-7.6-4.405-11.751H5.968 C2.686,72.067,0,69.382,0,66.099V53.628c0-3.283,2.686-5.968,5.968-5.968h10.039c1.232-4.063,2.993-7.896,5.204-11.418l-6.511-6.51 c-2.321-2.322-2.321-6.12,0-8.44l8.819-8.819c2.321-2.321,6.118-2.321,8.439,0l7.101,7.101c3.658-1.96,7.601-3.456,11.753-4.406 V5.969C50.812,2.686,53.498,0,56.78,0h12.471c3.282,0,5.968,2.686,5.968,5.969v10.036c4.064,1.231,7.898,2.992,11.422,5.204 l6.507-6.509C95.471,12.379,99.268,12.379,101.589,14.7L101.589,14.7z M61.44,36.92c13.54,0,24.519,10.98,24.519,24.519 c0,13.538-10.979,24.519-24.519,24.519c-13.539,0-24.519-10.98-24.519-24.519C36.921,47.9,47.901,36.92,61.44,36.92L61.44,36.92z"
                  />
                </g>
              </svg>
      </button>
   
    <!-- LIFE -->
    <div class="life">
      <img src="@/assets/profil-in-game/hearts-game.svg"/>
      <span>{{ teamLife }}</span>
    </div>
    
    <!-- HORIZONTAL CONTENT -->
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

/* HALF CIRCLE */
.halfCircle{
  position:absolute;
  left:50%;
  top:50%;
  transform:translate(-50%,-50%);

  width:17vw;
  height:10vw;

  background:rgba(200,200,200,0.95);
  border:3px solid black;

  border-radius:14vw 14vw 0 0;
  
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

.innerRow{
  position:absolute;
  bottom:25%;

  width:100%;

  display:flex;
  justify-content:space-around;
  align-items:center;
}

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

/* INVERSED HALF CIRCLE OPPONENT */
.enemy .halfCircle{
  border-radius:0 0 14vw 14vw;
}

/* LIFE OPPONENT */
.enemy .life{
  top:auto;
  bottom:5%;
}

/* AVATARS OPPONENT */
.enemy .innerRow{
  bottom:auto;
  top:20%;
}

/* OUTSIDE */
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
  right: 15%;
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

/* APPARITION'S ANIMATION */
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

/* APPARITION'S ANIMATION */
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

.settings-button {
  width: 2.75vw;
  height:  2.75vw;

  position: fixed;
  top: 2vw;
  left: 50%;
  transform: translateX(-50%);

  z-index: 20;
  display: inline-flex;
  align-items: center;
  justify-content: center;

  background-color: rgba(255, 255, 255, 0.5);
  border: none;
  border-radius: 50%;
  transition: background-color 0.3s, transform 0.2s;
}

.settings-button svg {
  width: 5em;
  height: 5em;

}

.settings-button:hover {
  background-color: rgba(255, 255, 255, 0.9);
  transform: scale(1.05);
  transform: translateX(-50%);
}

.settings-button:active {
  background-color: #1e6f93;
  transform: scale(0.98);
}

</style>
