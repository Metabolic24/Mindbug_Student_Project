interface PlayerInterface {
    uuid: string
    teamId: string
    name: string
    lifePoints: number
    mindbugCount: number

    drawPileCount: number
    hand: CardInterface[]
    board: CardInterface[]
    discard: CardInterface[]
}