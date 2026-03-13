interface CardDetails {
    id: number
    setName: string
    name: string
    power: number
    keywords?: string[]
    effects?: Record<string, unknown[]>
    unique?: boolean
    evolution?: boolean
}
