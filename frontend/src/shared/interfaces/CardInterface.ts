interface CommonCardInterface {
    id: number
    power: number
    keywords?: string[]
    description: String
}

interface LightCardInterface extends CommonCardInterface {
    evolutionId?: number
    parentId?: number
    setName: string
}

interface CardInterface extends CommonCardInterface {
    uuid: string
    ownerId: string
    hidden: boolean

    name?: string
    basePower: number

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
