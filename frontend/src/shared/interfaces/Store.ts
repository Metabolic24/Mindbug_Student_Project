// Interface for the data structure that will be stored through VueX
interface AppState {
    playerData: PlayerData
}

// Interface for player data
interface PlayerData {
    uuid: string
    name: string
}
