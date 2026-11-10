import { useQuery } from '@tanstack/react-query'
import { initializeDemoSession } from '../../../services/api'

export function useDemoSession() {
    return useQuery({
        queryKey: ['demo-session'],
        queryFn: initializeDemoSession,
        staleTime: 1000 * 60 * 60,
        retry: 1,
    })
}