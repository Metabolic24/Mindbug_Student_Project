import {createStore} from "vuex";

interface AppState {
    playerData: PlayerData
}

let store = createStore({
    state(): AppState {
        return {
            playerData: undefined
        }
    },
    mutations: {
        savePlayerData(state: AppState, playerData: PlayerData) {
            state.playerData = playerData;
        }
    }
})

export default store;