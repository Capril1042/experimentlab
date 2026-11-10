
import { useQuery } from '@tanstack/react-query'
import { get } from '../../../services/api'
import type { Variant } from '../types/variant'

export function useVariants(experimentId: string) {
    return useQuery({
        queryKey: ['experiments', experimentId, 'variants'],
        queryFn: () =>
            get<Variant[]>(`/api/experiments/${experimentId}/variants`),
        enabled: Boolean(experimentId),
    })
}
