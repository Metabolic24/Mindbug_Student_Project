interface GameStateInterface {
    uuid: string;
    player: PlayerInterface
    opponent: PlayerInterface
    playerTurn?: boolean
    finished: boolean
    card: CardInterface
    choice: ChoiceInterface | TargetChoiceInterface | SimultaneousChoiceInterface
}

interface CardInterface {
    uuid: string
    ownerId: string
    id: number
    setName: string
    hidden: boolean

    name?: string
    power: number
    keywords?: string[]
    stillTough: boolean
    ableToBlock: boolean
    ableToAttack: boolean
    ableToAttackTwice: boolean
}

interface SelectedCardInterface extends CardInterface {
    location: CardLocation
}

type CardLocation = "Hand" | "Board"

interface PlayerInterface {
    uuid: string
    name: string
    lifePoints: number
    mindbugCount: number

    drawPileCount: number
    hand: CardInterface[]
    board: CardInterface[]
    discard: CardInterface[]
}

interface ChoiceInterface {
    type: ChoiceType
    playerToChoose: string
    sourceCard: CardInterface
}

type ChoiceType = "SIMULTANEOUS" | "FRENZY" | "BOOLEAN" | "TARGET" | "HUNTER";

interface SimultaneousChoiceInterface extends ChoiceInterface {
    type: "SIMULTANEOUS"
    sourceCard: undefined
    availableEffects: CardInterface[]
}

interface TargetChoiceInterface extends ChoiceInterface {
    type: "TARGET" | "HUNTER"
    availableTargets: CardInterface[]
    targetsCount: number
    optional: boolean
}

type Owner = "Player" | "Opponent"

type WsMessageType =
    "CARD_PICKED"
    | "CARD_PLAYED"
    | "ATTACK_DECLARED"
    | "WAITING_ATTACK_RESOLUTION"
    | "CARD_DESTROYED"
    | "EFFECT_RESOLVED"
    | "CHOICE"
    | "NEW_TURN"
    | "LP_DOWN"
    | "FINISHED";

interface WsMessage {
    type: WsMessageType
    state: GameStateInterface
}

interface ChoiceModalData {
    type: "SIMULTANEOUS" | "TARGET"
    count: number
    cards: CardInterface[]
    optional: boolean
}

interface PlayerData {
    uuid: string
    name: string
}