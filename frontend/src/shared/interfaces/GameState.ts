interface WsMessage {
    type: WsMessageType
    state: GameStateInterface
}

/** WebSocket payloads after game over (revenge / rematch), without full game state. */
type WsRevengeMessageType = "REVENGE_PROGRESS" | "REVENGE_STARTED" | "REVENGE_BLOCKED"

interface WsRevengeProgressMessage {
    type: "REVENGE_PROGRESS"
    revengeVotes: number
    revengeNeeded: number
}

interface WsRevengeStartedMessage {
    type: "REVENGE_STARTED"
    newGameId: string
}

interface WsRevengeBlockedMessage {
    type: "REVENGE_BLOCKED"
    reason: string
}

type WsInboundMessage =
    | WsMessage
    | WsRevengeProgressMessage
    | WsRevengeStartedMessage
    | WsRevengeBlockedMessage

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
    ally: PlayerInterface
    opponents: PlayerInterface[]
    currentPlayerID: string
    winners?: string[]
    choice?: ChoiceInterface | TargetChoiceInterface | SimultaneousEffectsChoiceInterface
    forcedAttack?: boolean
}