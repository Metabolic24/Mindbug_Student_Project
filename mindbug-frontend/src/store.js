import { createStore } from 'vuex';

export default createStore({
  state: {
    gameId: null,
    playerId: null,
    handCards: [] 
  },
  mutations: {
    SET_GAME_ID(state, id) {
      state.gameId = id;
    },
    SET_PLAYER_ID(state, id) {
      state.playerId = id;
    },
    SET_HAND_CARDS(state, cards) {
      state.handCards = cards;
    }
  },
  actions: {
    
  }
});