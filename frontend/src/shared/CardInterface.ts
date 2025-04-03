interface CardInterface {
    uuid: string
    name: string
    keywords: string[]
    effect: string
}

interface PlayerInterface  {
    uuid: string
    name: string
    lifePoints: number
    mindbugCount: number

    drawPileCount: number
    hand: CardInterface[]
    board: CardInterface[]
    discard: CardInterface[]

}

interface GameStateInterface {
    uuid: string;
    player: PlayerInterface
    opponent: PlayerInterface
    finished: boolean
}
