
import { useQuery } from '@tanstack/react-query'
import { getSimulations } from '../../../services/api'
import { useDemoSession } from './useDemoSession'

export function useSimulations(experimentId: string) {
    const { data: session } = useDemoSession()

    return useQuery({
        queryKey: ['simulations', experimentId],
        queryFn: () => getSimulations(experimentId),
        enabled: session?.active === true && Boolean(experimentId),
    })
}
