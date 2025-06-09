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
    <div class="playerData">
      <img class="playerAvatar" :src="getAvatar()" alt="Avatar">
      <p class="playerName">{{ name }}</p>
    </div>
    <div class="playerData">
      <span class="lifePoint">❤️ {{ lifePoints }}</span>
      <span class="mbPoint">🧠 {{ mindbugCount }}</span>
      <div class="drawPileCount">
        <img src="@/assets/cards/back.png" alt="back"/>
        <span>{{ drawPileCount }}</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.playerDetails {
  display: flex;
  align-items: center;

  padding: 0 5px 2px 5px;
  margin-top: 1px;

  background: rgba(239, 168, 48, 0.35);

  border-radius: 10px;
  box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.2);
}

.playerAvatar {
  width: 50px;
  height: 50px;

  margin-left: 10px;
  margin-right: 20px;

  border: 2px solid black;
  border-radius: 50%;
}

.playerName {
  height: 22px;
  width: fit-content;

  text-align: left;
  padding: 0 8px;
  margin-bottom: 0;

  line-height: 18px;
  font-size: 16px;
  font-weight: bold;

  background-color: white;

  border: 2px solid black;
  border-radius: 5px;
}

.playerData {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.lifePoint {
  color: red;
  font-weight: bold;
}

.mbPoint {
  color: black;
  font-weight: bold;
}

.drawPileCount > img {
  width: 10px;
  height: 15px;

  margin-left: 5px;
}

.drawPileCount > span {
  margin-left: 8px;

  color: blue;
  font-weight: bold;
}
</style>
