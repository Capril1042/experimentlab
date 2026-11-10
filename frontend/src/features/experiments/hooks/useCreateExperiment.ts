import { useMutation, useQueryClient } from '@tanstack/react-query'
import { post } from '../../../services/api'
import type { CreateExperimentRequest } from '../types/createExperiment'
import type { Experiment } from '../types/experiment'

export function useCreateExperiment() {
    const queryClient = useQueryClient()

    return useMutation({
        mutationFn: (request: CreateExperimentRequest) =>
            post<CreateExperimentRequest, Experiment>(
                '/api/experiments',
                request
            ),

        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: ['experiments'],
            })
        },
    })
}