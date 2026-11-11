
export interface Simulation {
    id: string
    status: 'PENDING' | 'RUNNING' | 'COMPLETED' | 'FAILED'
    targetUsers: number
    usersProcessed: number
    createdAt: string
    startedAt: string | null
    completedAt: string | null
}

export interface VariantSimulationResult {
    variantId: string
    variantName: string
    assignedUsers: number
    exposures: number
    clicks: number
    conversions: number
    clickThroughRate: number
    conversionRate: number
    clickToConversionRate: number
}
