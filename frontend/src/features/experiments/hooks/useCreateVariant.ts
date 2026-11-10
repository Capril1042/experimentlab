
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { post } from '../../../services/api'
import type { Variant } from '../types/variant'

type CreateVariantInput = {
    experimentId: string
    name: string
    description: string
    configuration: Record<string, unknown>
    trafficPercentage: number
}

type CreateVariantRequest = Omit<CreateVariantInput, 'experimentId'>

export function useCreateVariant() {
    const queryClient = useQueryClient()

    return useMutation({
        mutationFn: ({
                         experimentId,
                         ...variant
                     }: CreateVariantInput) =>
            post<CreateVariantRequest, Variant>(
                `/api/experiments/${experimentId}/variants`,
                variant
            ),
        onSuccess: (_, variables) => {
            queryClient.invalidateQueries({
                queryKey: [
                    'experiments',
                    variables.experimentId,
                    'variants',
                ],
            })
        },
    })
}
