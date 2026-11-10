
export type Variant = {
    id: string
    experimentId: string
    name: string
    description: string | null
    configuration: Record<string, unknown>
    trafficPercentage: number
}
