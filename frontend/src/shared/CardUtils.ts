export function getCardImage(card: CardInterface): string {
    const url = new URL("@/assets/cards/", import.meta.url)

    if (card.setName && card.id) {
        return `${url}/${card.setName}/${card.id}.jpg`
    }
    else {
        return `${url}/back.png`
    }
}

export function getCardAlt(card: CardInterface): string {
    return card.name ?? "back"
}
