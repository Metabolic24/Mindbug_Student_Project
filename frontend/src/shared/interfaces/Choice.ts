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

interface ChoiceModalData {
    type: "SIMULTANEOUS" | "TARGET"
    count: number
    cards: CardInterface[]
    optional: boolean
}