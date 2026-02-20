<script setup lang="ts">
// Declare the interface for the data given by the parent component
interface Props {
  name: string
  lifePoints: number
  mindbugCount: number
  drawPileCount: number
}

defineProps<Props>()

// Get the avatar picture
function getAvatar(name: string = "default") {
  // TODO Ajouter la possibilité pour un joueur d'avoir un avatar customisé
  const url = new URL("@/assets/avatars/", import.meta.url)
  return `${url}/${name}.jpg`
}
</script>

<template>
  <div class="playerContainer">
    <div class="playerDetails">
      <div class="playerProfile">
        <img class="playerAvatar" :src="getAvatar()" alt="Avatar" draggable="false">
        <span class="playerName">{{ name }}</span>
      </div>
      <div class="playerData">
        
        <!-- Mindbug tokens -->
        <div class="mindbugContainer">
          <img
            v-for="n in mindbugCount"
            :key="n"
                        src="@/assets/profil-in-game/mindbug.png"
            class="mindbugToken"
            alt="Mindbug"
            draggable="false"
          />
        </div>

        <!-- Draw pile -->
        <div class="drawPileContainer">
          <img
            src="@/assets/profil-in-game/cardback-pile.png"
            class="pileImage"
            alt="Draw pile"
            draggable="false"
          />
          <span class="pileCount">{{ drawPileCount }}</span>
        </div>

      </div>
    </div>
    
    <!-- Life points -->
    <div class="lifeContainer">
      <img 
        src="@/assets/profil-in-game/hearts-game.svg" 
        class="heartImage"
        alt="Life"
        draggable="false"
      />
      <span class="lifeText">{{ lifePoints }}</span>
    </div>

  </div>
</template>

<style scoped>
img {
  pointer-events: none;
}

/* Global container */
.playerContainer {
  display: flex;
  align-items: center;
  gap: 10px;
}

.playerDetails {
  display: flex;
  align-items: center;

  padding: 5px;
  margin-top: 1px;

  background: rgba(239, 168, 48, 0.8);

  border-radius: 10px;
  box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.2);
}

.playerProfile {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}

.playerAvatar {
  width: 5vw;
  height: 10vh;

  margin-left: 1vw;
  margin-right: 1vw;

  border: 2px solid black;
  border-radius: 50%;
}

.playerName {
  line-height: 2vh;
  font-size: 1.8vh;
  font-weight: bold;

  text-align: left;
  padding: 0 0.5vw;
  margin-bottom: 0;
  overflow: hidden;

  color: #000;
  background-color: rgba(255, 255, 255, 0.8);
  cursor: default;

  border: 2px solid rgba(0, 0, 0, 0.8);
  border-radius: 5px;
}

.playerData {
  display: grid;
  grid-template-rows: 30px auto;
  gap: 4px;
  padding: 0 0.5vw;

  span {
    font-size: 2vh;
    font-weight: bold;
  }
}

/* Container for mindbug tokens */
.mindbugContainer {
  display: flex;
  gap: 4px;
  align-items: center;

  height: 30px;

  min-width: 70px;
}

.mindbugToken {
  width: 1.5vw;
  max-width: 30px;
  height: auto;
}

/* Container for the draw pile */
.drawPileContainer {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pileImage {
  width: 3vw;
  max-width: 60px;
  height: auto;
  object-fit: contain;
}

.pileCount {
  position: absolute;

  top: 50%;
  left: 65%;
  transform: translate(-50%, -50%);

  width: 3vh;
  height: 3vh;

  font-size: 1.6vh;

  color: white;

  background: rgba(0, 0, 0, 0.75);
  border-radius: 50%;

  display: flex;
  align-items: center;
  justify-content: center;
}

/* Heart container */
.lifeContainer {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;

  height: 100%;
}

.heartImage {
  height: 100%;
  max-height: 75px;
  object-fit: contain;
}

.lifeText {
  position: absolute;
  font-weight: bold;
  color: white;
  font-size: 4vh;
  pointer-events: none;
}

.mbPoint {
  color: black;
}

.drawPileCount {
  display: flex;
  align-items: center;

  img {
    width: 1vw;
    height: 3vh;

    margin-left: 0.3vw;
  }

  span {
    margin-left: 0.35vw;

    color: blue;
  }
}

</style>
