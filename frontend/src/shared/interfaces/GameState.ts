interface WsMessage {
    type: WsMessageType
    state: GameStateInterface
}

type WsMessageType =
    "STATE"
    | "CARD_PICKED"
    | "CARD_PLAYED"
    | "ATTACK_DECLARED"
    | "WAITING_ATTACK_RESOLUTION"
    | "CARD_DESTROYED"
    | "EFFECT_RESOLVED"
    | "CHOICE"
    | "NEW_TURN"
    | "LP_DOWN"
    | "FINISHED";

interface GameStateInterface {
    uuid: string;
    player: PlayerInterface
    opponent: PlayerInterface
    playerTurn?: boolean
    finished: boolean
    card: CardInterface
    choice: ChoiceInterface | TargetChoiceInterface | SimultaneousChoiceInterface
}