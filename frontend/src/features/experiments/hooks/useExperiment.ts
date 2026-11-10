import { useQuery } from '@tanstack/react-query'
import { get } from '../../../services/api'
import type { Experiment } from '../types/experiment'

export function useExperiment(id: string) {
    return useQuery({
        queryKey: ['experiments', id],
        queryFn: () => get<Experiment>(`/api/experiments/${id}`),
        enabled: Boolean(id),
    })
}