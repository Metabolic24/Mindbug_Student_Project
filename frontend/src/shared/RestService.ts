import axios, {AxiosError, AxiosResponse} from "axios";

const baseUrl = "http://localhost:5173/api";
const cardSetBaseUrl = `${baseUrl}/sets`;
const gameBaseUrl = `${baseUrl}/game`;
const playerBaseUrl = `${baseUrl}/player`;

//TODO Improve error management (#192)
async function manageRestCall<T>(responsePromise: Promise<AxiosResponse<T>>): Promise<T> {
    try {
        const response: AxiosResponse<T> = await responsePromise;
        return response.data
    } catch (error) {
        if (error instanceof AxiosError) {
            console.error("HTTP error (" + error.status + ":" + error.message + ") : " + error.response?.data)
        }
    }
}

// Player data endpoints

export async function getPlayerData(name: string): Promise<PlayerData> {
    return manageRestCall(axios.post(playerBaseUrl, {name}))
}

// Sets endpoints

export async function getAvailableSets(): Promise<string[]> {
    return manageRestCall(axios.get(cardSetBaseUrl))
}

export async function getCardSetDetails(setName: String): Promise<string[]> {
    return manageRestCall(axios.get(`${cardSetBaseUrl}/${setName}`))
}

export async function createCardSet(setName:string, cards: number[]): Promise<string[]> {
    return manageRestCall(axios.post(`${cardSetBaseUrl}`, {name: setName, cards}))
}

export async function getAllCards(): Promise<LightCardInterface[]> {
    return manageRestCall(axios.get(`${baseUrl}/cards`))
}

// Game endpoints

export async function startOfflineGame(playerId: string, cardSetName: string): Promise<string> {
    return manageRestCall(axios.post(gameBaseUrl + "/startOffline", {playerId, cardSetName}))
}

export async function pickCard(gameId: string, cardId: string): Promise<void> {
    return manageRestCall(axios.post(gameBaseUrl + "/pick", {gameId, cardId}))
}

export async function playCard(gameId: string, mindbuggerId: string): Promise<void> {
    return manageRestCall(axios.post(gameBaseUrl + "/play", {gameId, mindbuggerId}))
}

export async function declareAttack(gameId: string, attackingCardId: string): Promise<void> {
    return manageRestCall(axios.post(gameBaseUrl + "/attack", {gameId, attackingCardId}))
}

export async function resolveAttack(gameId: string, defendingPlayerId: string, defenseCardId: string): Promise<void> {
    return manageRestCall(axios.put(gameBaseUrl + "/attack", {gameId, defendingPlayerId, defenseCardId}))
}

export async function resolveBoolean(gameId: string, ok: boolean): Promise<void> {
    return manageRestCall(axios.post(gameBaseUrl + "/choice/boolean", {gameId, ok}))
}

export async function resolveSingleTargetChoice(gameId: string, cardId: string): Promise<void> {
    return manageRestCall(axios.post(gameBaseUrl + "/choice/single", {gameId, cardId}))
}

export async function resolveMultipleTargetChoice(gameId: string, targets: string[]): Promise<void> {
    return manageRestCall(axios.post(gameBaseUrl + "/choice/target", {gameId, targets}))
}

export async function resolveAction(gameId: string, cardId: string): Promise<void> {
    return manageRestCall(axios.post(gameBaseUrl + "/action", {gameId, cardId}))
}

export async function surrender(gameId: string, playerId: string): Promise<void> {
    return manageRestCall(axios.post(gameBaseUrl + "/surrender", {gameId, playerId}))
}
