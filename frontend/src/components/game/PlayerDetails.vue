<script setup lang="ts">
interface Props {
  name: String
  lifePoints: Number
  mindbugCount: Number
  drawPileCount: Number
}

defineProps<Props>()

function getAvatar(name: string = "default") {
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
      <div class="drawPileCount"><img src="@/assets/cards/back.png" alt="back" style="width: 10px;height: 15px;"/><span>{{
          drawPileCount
        }}</span></div>
    </div>
  </div>
</template>

<style scoped>
.playerDetails {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.8);
  padding: 0 5px 2px 5px;
  border-radius: 10px;
  box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.2);
  margin-top: 1px;
}

.playerAvatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  border: 2px solid black;
  margin-left: 10px;
  margin-right: 20px;
}

.playerName {
  height: 22px;
  width: fit-content;
  padding: 0 8px;
  text-align: left;
  line-height: 18px;
  font-size: 16px;
  font-weight: bold;
  border: 2px solid black;
  border-radius: 5px;
  background-color: white;
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
  margin-left: 5px;
}

.drawPileCount > span {
  color: blue;
  font-weight: bold;
  margin-left: 8px;
}

p {
  margin-bottom: 0;
}
</style>
