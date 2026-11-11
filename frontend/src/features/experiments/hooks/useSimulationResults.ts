
import { useQuery } from '@tanstack/react-query'
import { getSimulationResults } from '../../../services/api'
import { useDemoSession } from './useDemoSession'

export function useSimulationResults(
    experimentId: string,
    simulationId: string | null
) {
    const { data: session } = useDemoSession()

    return useQuery({
        queryKey: ['simulation-results', experimentId, simulationId],
        queryFn: () => getSimulationResults(experimentId, simulationId!),
        enabled:
            session?.active === true &&
            Boolean(experimentId) &&
            Boolean(simulationId),
    })
}
