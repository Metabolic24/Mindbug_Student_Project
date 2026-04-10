<script setup lang="ts">
import Board from "@/components/game/board/Board.vue";
import Hand from "@/components/game/Hand.vue";
import PlayerDetails from "@/components/game/PlayerDetails.vue";

interface Props {
  gameState: GameStateInterface;
  selectedCard: SelectedCardInterface;
  pickedCard: CardInterface;
  attackingCard: CardInterface;
  isPlayerActive: boolean;
  settingsTooltip: string;
  onCardSelected: (card: CardInterface, location: "Board" | "Hand") => void;
  onActionButtonClick: (actionLabel: string) => void;
  onSettingsButtonClick: () => void;
  onCardPreview: ((args: any[]) => void) | undefined;
}

const props = defineProps<Props>();
</script>

<template>
  <div class="container-fluid game">
    <button type="button" class="settings-button" @click="props.onSettingsButtonClick()" :title="props.settingsTooltip">
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

    <div class="row top-row">
      <div class="col-2 player-container player-container--top">
        <player-details :name="gameState?.opponents[0]?.name" :life-points="gameState?.opponents[0]?.lifePoints"
                        :draw-pile-count="gameState?.opponents[0]?.drawPileCount"
                        :mindbug-count="gameState?.opponents[0]?.mindbugCount"
                        :is-active="!props.isPlayerActive">
        </player-details>
      </div>
      <div class="col-8">
        <hand
          :cards="gameState?.opponents[0]?.hand"
          visibility="enemy"
          :selected-card="selectedCard"
          @card-preview="onCardPreview"
        ></hand>
      </div>
    </div>

    <div class="board-wrapper">
      <board :game-state="props.gameState" 
            :selected-card="props.selectedCard" 
            :picked-card="props.pickedCard"
            :attacking-card="props.attackingCard" 
            @button-clicked="props.onActionButtonClick($event)"
            @card-selected="props.onCardSelected($event, 'Board')"
            @card-preview="onCardPreview">
      </board>
    </div>

    <div class="row bottom-row">
      <div class="col-2 player-container player-container--bottom">
        <player-details :name="props.gameState?.player?.name" 
                        :life-points="props.gameState?.player?.lifePoints"
                        :draw-pile-count="props.gameState?.player?.drawPileCount"
                        :mindbug-count="props.gameState?.player?.mindbugCount"
                        :is-active="props.isPlayerActive">
        </player-details>
      </div>
      <div class="col-8">
        <hand :cards="props.gameState?.player?.hand" visibility="self" :selected-card="props.selectedCard"
              @card-selected="props.onCardSelected($event, 'Hand')"
              @card-preview="onCardPreview">
        </hand>
      </div>
      <div class="col-2"></div>
    </div>
    </div>
</template>

<style scoped>
.game {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  overflow: hidden;
  height: 100vh;
  position: relative;

  background-image: url("../../assets/playmats/default.png");
  background-repeat: no-repeat;
  background-size: cover;
  background-position: center;
}

.top-row {
  width: 100%;
  height: 14vh;

  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: start;
  column-gap: 16px;
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  padding: 8px 8px 0;
  pointer-events: none;
  z-index: 5;

  .player-container {
    align-items: start;
  }
}

.bottom-row {
  width: 100%;
  height: max(20vh, 12vw);

  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: end;
  column-gap: 16px;
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 0 8px 8px;
  pointer-events: none;
  z-index: 5;

  .player-container {
    align-items: end;
  }
}

.top-row .player-container,
.top-row .top-buttons,
.top-row .hand,
.bottom-row .player-container,
.bottom-row .hand {
  pointer-events: auto;
}

.top-row > .col-2,
.top-row > .col-8,
.bottom-row > .col-2,
.bottom-row > .col-8 {
  width: auto;
  max-width: none;
}

.top-row > .col-8,
.bottom-row > .col-8 {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  display: flex;
  justify-content: center;
}

.board-wrapper {
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  display: flex;
  padding-top: 14vh;
  padding-bottom: max(20vh, 12vw);
  box-sizing: border-box;
}

.player-container {
  display: flex;
}

.settings-button {
  width: 44px;
  height: 44px;
  position: fixed;
  top: 10px;
  right: 10px;
  z-index: 20;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(255, 255, 255, 0.5);
  border: none;
  border-radius: 25px;
  transition: background-color 0.3s, transform 0.2s;
}

.settings-button svg {
  width: 20px;
  height: 20px;
}

.settings-button:hover {
  background-color: rgba(255, 255, 255, 0.9);
  transform: scale(1.05);
}

.settings-button:active {
  background-color: #1e6f93;
  transform: scale(0.98);
}
</style>
