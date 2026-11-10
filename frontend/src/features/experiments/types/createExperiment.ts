export type CreateExperimentRequest = {
    name: string
    description: string
    hypothesis: string
    primaryMetric: 'CONVERSION_RATE'
        | 'CLICK_THROUGH_RATE'
        | 'CLICKS'
        | 'CONVERSIONS'
}