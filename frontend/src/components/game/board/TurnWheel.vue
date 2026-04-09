<script setup lang="ts">
import { computed, ref, watch } from "vue";

interface Props {
  gameState: GameStateInterface;
}

const props = defineProps<Props>();

const ANGLES: Record<string, number> = {
  opp1:    315,
  opp2:     45,
  ally:    225,
  player:  135,
};

function resolveKey(uuid: string | undefined): string | null {
  if (!uuid) return null;
  const g = props.gameState;
  if (uuid === g.player.uuid)            return "player";
  if (uuid === g.ally?.uuid)             return "ally";
  if (uuid === g.opponents?.[0]?.uuid)   return "opp1";
  if (uuid === g.opponents?.[1]?.uuid)   return "opp2";
  return null;
}

const activeInfo = computed((): { key: string | null; isChoice: boolean } => {
  const g = props.gameState;
  if (!g) return { key: null, isChoice: false };

  // Priorité 1 : un choix est en attente
  if (g.choice?.playerToChoose) {
    return {
      key: resolveKey(g.choice.playerToChoose),
      isChoice: true,
    };
  }

  // Priorité 2 : tour normal — on utilise currentPlayerUuid si dispo
  if (g.currentPlayerUuid) {
    return {
      key: resolveKey(g.currentPlayerUuid),
      isChoice: false,
    };
  }

  // Fallback (sans le patch backend) : playerTurn = true → toi, false → opp1
  return {
    key: g.playerTurn ? "player" : "opp1",
    isChoice: false,
  };
});

const currentAngle = ref(135);
let lastAngle = 135;

watch(
  activeInfo,
  ({ key }) => {
    if (!key) return;
    const target = ANGLES[key];
    let delta = target - lastAngle;
    if (delta > 180) delta -= 360;
    if (delta < -180) delta += 360;
    lastAngle += delta;
    currentAngle.value = lastAngle;
  },
  { immediate: true }
);

defineExpose({ activeInfo });
</script>

<template>
  <div class="turn-wheel">
    <div class="wheel" :class="{ 'is-choice': activeInfo.isChoice }">
      <svg
        class="arrow-svg"
        :style="{ transform: `rotate(${currentAngle}deg)` }"
        viewBox="0 0 56 56"
        xmlns="http://www.w3.org/2000/svg"
      >
        <circle cx="28" cy="28" r="26" fill="none" stroke="rgba(255,255,255,0.08)" stroke-width="1"/>
        <path
          d="M28 7 L36 23 L30 23 L30 49 L26 49 L26 23 L20 23 Z"
          :fill="activeInfo.isChoice ? '#78B4FF' : '#FFD700'"
          opacity="0.9"
        />
      </svg>
    </div>
  </div>
</template>

<style scoped>
.turn-wheel {
  position: absolute;
  top: 50%; left: 50%;
  transform: translate(-50%, -50%);
  z-index: 10;
  pointer-events: none;
}
.wheel {
  width: 100px; height: 100px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.78);
  border: 2px solid rgba(255, 255, 255, 0.12);
  display: flex; justify-content: center; align-items: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
  transition: border-color 0.3s ease;
}
.wheel.is-choice {
  border-color: rgba(120, 180, 255, 0.45);
}
.arrow-svg {
  width: 150px; height: 150px;
  transition: transform 0.55s cubic-bezier(0.4, 0, 0.2, 1);
}
</style>