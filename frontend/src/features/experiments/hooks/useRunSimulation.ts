
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { post } from '../../../services/api'

export interface SimulationResponse {
    id: string
    status: 'PENDING' | 'RUNNING' | 'COMPLETED' | 'FAILED'
    targetUsers: number
    usersProcessed: number
    createdAt: string
    startedAt: string | null
    completedAt: string | null
}

interface RunSimulationRequest {
    experimentId: string
    targetUsers: number
}

export function useRunSimulation() {
    const queryClient = useQueryClient()

    return useMutation({
        mutationFn: ({ experimentId, targetUsers }: RunSimulationRequest) =>
            post<{ targetUsers: number }, SimulationResponse>(`/api/experiments/${experimentId}/simulations`,
                { targetUsers }
            ),
        onSuccess: (_, variables) => {
            queryClient.invalidateQueries({
                queryKey: ['simulations', variables.experimentId],
            })
        },
    })
}
