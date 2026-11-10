
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { put } from '../../../services/api'
import type { Variant } from '../types/variant'

type UpdateVariantInput = {
    experimentId: string
    variantId: string
    name: string
    description: string
    configuration: Record<string, unknown>
    trafficPercentage: number
}

type UpdateVariantRequest = Omit<
    UpdateVariantInput,
    'experimentId' | 'variantId'
>

export function useUpdateVariant() {
    const queryClient = useQueryClient()

    return useMutation({
        mutationFn: ({
                         experimentId,
                         variantId,
                         ...variant
                     }: UpdateVariantInput) =>
            put<UpdateVariantRequest, Variant>(
                `/api/experiments/${experimentId}/variants/${variantId}`,
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
