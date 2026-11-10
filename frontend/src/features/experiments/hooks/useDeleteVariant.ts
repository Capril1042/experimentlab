
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { del } from '../../../services/api'

type DeleteVariantInput = {
    experimentId: string
    variantId: string
}

export function useDeleteVariant() {
    const queryClient = useQueryClient()

    return useMutation({
        mutationFn: ({ experimentId, variantId }: DeleteVariantInput) =>
            del(`/api/experiments/${experimentId}/variants/${variantId}`),

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
