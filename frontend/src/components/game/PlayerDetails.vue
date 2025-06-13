<script setup lang="ts">
// Declare the interface for the data given by the parent component
interface Props {
  name: String
  lifePoints: Number
  mindbugCount: Number
  drawPileCount: Number
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
  <div class="playerDetails">
    <div class="playerProfile">
      <img class="playerAvatar" :src="getAvatar()" alt="Avatar" draggable="false">
      <span class="playerName">{{ name }}</span>
    </div>
    <div class="playerData">
      <span class="lifePoint">❤️ {{ lifePoints }}</span>
      <span class="mbPoint">🧠 {{ mindbugCount }}</span>
      <div class="drawPileCount">
        <img src="@/assets/cards/back.png" alt="back" draggable="false"/>
        <span>{{ drawPileCount }}</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
img {
  pointer-events: none;
}

.playerDetails {
  display: flex;
  align-items: center;

  padding: 0 5px 2px 5px;
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

  background-color: rgba(255, 255, 255, 0.8);
  cursor: default;

  border: 2px solid rgba(0, 0, 0, 0.8);
  border-radius: 5px;
}

.playerData {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 0 0.5vw;

  span {
    font-size: 2vh;
    font-weight: bold;
  }
}

.lifePoint {
  color: red;
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
