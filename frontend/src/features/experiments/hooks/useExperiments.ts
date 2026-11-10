
import { useQuery } from '@tanstack/react-query'
import { get } from '../../../services/api'
import { useDemoSession } from './useDemoSession'
import type { Experiment } from '../types/experiment'

export function useExperiments() {
    const { data: session } = useDemoSession()

    return useQuery({
        queryKey: ['experiments'],
        queryFn: () => get<Experiment[]>('/api/experiments'),
        enabled: session?.active === true,
    })
}
