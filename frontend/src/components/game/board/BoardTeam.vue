<script setup lang="ts">
    interface Props {
        gameState: GameStateInterface;
        selectedCard: SelectedCardInterface;
        pickedCard: CardInterface;
        attackingCard: CardInterface;
        onCardSelected: (card: CardInterface, location: "Board" | "Hand") => void;
        onActionButtonClick: (actionLabel: string) => void;
        onLeaveButtonClick: () => void;
    }
    const props = defineProps<Props>();
</script>

<template>
    <div class="board-team">

  <!-- ZONES JOUEURS -->

  <div class="player-zone player-1">
    Player 1 board
  </div>

  <div class="player-zone player-2">
    Player 2 board
  </div>

  <div class="player-zone player-3">
    Player 3 board
  </div>

  <div class="player-zone player-4">
    Player 4 board
  </div>

  <!-- ROUE DU TOUR -->

  <div class="turn-wheel">
    <div class="wheel">
      <button type="button" class="leave-button" @click="props.onLeaveButtonClick()">
            <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor" class="bi bi-door-open" viewBox="0 0 16 16">
                <path d="M8.5 10c-.276 0-.5-.448-.5-1s.224-1 .5-1 .5.448.5 1-.224 1-.5 1"></path>
                <path d="M10.828.122A.5.5 0 0 1 11 .5V1h.5A1.5 1.5 0 0 1 13 2.5V15h1.5a.5.5 0 0 1 0 1h-13a.5.5 0 0 1 0-1H3V1.5a.5.5 0 0 1 .43-.495l7-1a.5.5 0 0 1 .398.117M11.5 2H11v13h1V2.5a.5.5 0 0 0-.5-.5M4 1.934V15h6V1.077z"></path>
            </svg>
        </button>
    </div>
  </div>

</div>
</template>

<style scoped>

.board-team {

  position: relative;

  width: 100%;
  height: 100%;

  display: grid;

  grid-template-columns: 1fr 1fr;
  grid-template-rows: 1fr 1fr;

}

/* ZONES JOUEURS */

.player-zone {

  display: flex;
  justify-content: center;
  align-items: center;

  border: 2px solid rgba(255,255,255,0.2);

}

.player-1 { grid-area: 1 / 1 }
.player-2 { grid-area: 1 / 2 }
.player-3 { grid-area: 2 / 1 }
.player-4 { grid-area: 2 / 2 }

/* ROUE DU TOUR */

.turn-wheel {

  position: absolute;

  top: 50%;
  left: 50%;

  transform: translate(-50%, -50%);

  z-index: 10;

  pointer-events: none;

}

.wheel {

  width: 120px;
  height: 120px;

  border-radius: 50%;

  background: rgba(0,0,0,0.7);

  display: flex;
  justify-content: center;
  align-items: center;

  color: white;
  font-size: 30px;
  font-weight: bold;
  
  pointer-events: auto;
}
.leave-button {
  width: 2vw;
  height: 4vh;
  background-color: rgba(255, 255, 255, 0.5);
  border: none;
  border-radius: 25px;
  transition: background-color 0.3s, transform 0.2s;
}
</style>