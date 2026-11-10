import { useQuery } from '@tanstack/react-query'
import { get } from './services/api'

type HealthResponse = {
    status: string
    application: string
}

function App() {
    const { data, isLoading, isError } = useQuery<HealthResponse>({
        queryKey: ['health'],
        queryFn: () => get<HealthResponse>('/api/health'),
    })

    return (
        <main className="min-h-screen bg-slate-950 text-white p-8">
            <h1 className="text-4xl font-bold text-lime-400">
                ExperimentLab
            </h1>

            <div className="mt-6 rounded-lg bg-slate-900 p-4">
                <h2 className="text-lg font-semibold">System Status</h2>

                {isLoading && <p className="text-slate-400">Checking backend...</p>}

                {isError && <p className="text-pink-400">Backend unavailable</p>}

                {data && (
                    <p className="text-lime-400">
                        ● {data.application}: {data.status}
                    </p>
                )}
            </div>
        </main>
    )
}

export default App