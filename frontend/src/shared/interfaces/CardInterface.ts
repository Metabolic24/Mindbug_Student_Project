interface CardInterface {
    uuid: string
    ownerId: string
    id: number
    setName: string
    hidden: boolean

    name?: string
    power: number
    basePower: number
    keywords?: string[]
    stillTough: boolean
    ableToBlock: boolean
    ableToAttack: boolean
    ableToAttackTwice: boolean
    hasAction: boolean
}

type CardLocation = "Hand" | "Board"

interface SelectedCardInterface extends CardInterface {
    location: CardLocation
}
