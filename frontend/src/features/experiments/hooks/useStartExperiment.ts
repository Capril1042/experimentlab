
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { post } from '../../../services/api'

export function useStartExperiment() {
    const queryClient = useQueryClient()

    return useMutation({
        mutationFn: (experimentId: string) =>
            post<void, unknown>(
                `/api/experiments/${experimentId}/start`,
                undefined
            ),
        onSuccess: (_, experimentId) => {
            queryClient.invalidateQueries({
                queryKey: ['experiments', experimentId],
            })

            queryClient.invalidateQueries({
                queryKey: ['experiments'],
            })
        },
    })
}
