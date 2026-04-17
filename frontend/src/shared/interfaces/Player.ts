interface PlayerInterface {
    uuid: string
    teamId: string
    name: string
    /** True when this seat is played by the AI (no rematch flow). */
    ai?: boolean
    lifePoints: number
    mindbugCount: number

    drawPileCount: number
    hand: CardInterface[]
    board: CardInterface[]
    discard: CardInterface[]
}