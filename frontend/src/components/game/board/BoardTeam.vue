<script setup lang="ts">
    /*interface Props {
        gameState: GameStateInterface;
        selectedCard: SelectedCardInterface;
        pickedCard: CardInterface;
        attackingCard: CardInterface;
        onCardSelected: (card: CardInterface, location: "Board" | "Hand") => void;
        onActionButtonClick: (actionLabel: string) => void;
    }
    const props = defineProps<Props>();*/
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
      00
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