// Get the image of the given card
export function getCardImage(cardId: number): string {
    const url = new URL("@/assets/cards/", import.meta.url)
    return cardId ? `${url}/${cardId}.jpg` : `${url}/back.png`
}

// Get the alternative description of the given card
export function getCardAlt(card: CardInterface): string {
    return card.name ?? "back"
}

// Retrieve the image corresponding to the given set
export function getSetImage(set: string) {
    const url = new URL("@/assets/sets/", import.meta.url)
    if (set) {
        return `${url}/${set}.png`
    } else {
        return `${url}/create_new_set.png`
    }
}
