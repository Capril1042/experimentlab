import { useSimulations } from '../hooks/useSimulations'

interface SimulationHistoryProps {
    experimentId: string
    selectedSimulationId: string | null
    onSelectSimulation: (simulationId: string) => void
}

export function SimulationHistory({
                                      experimentId,
                                      selectedSimulationId,
                                      onSelectSimulation,
                                  }: SimulationHistoryProps) {
    const {
        data: simulations,
        isLoading,
        isError,
    } = useSimulations(experimentId)

    if (isLoading) {
        return (
            <section className="mt-8">
                <p className="text-slate-400">
                    Loading simulation history...
                </p>
            </section>
        )
    }

    if (isError) {
        return (
            <section className="mt-8">
                <p className="text-pink-400">
                    Could not load simulation history.
                </p>
            </section>
        )
    }

    if (!simulations || simulations.length === 0) {
        return (
            <section className="mt-8 rounded-xl border border-slate-800 bg-slate-950 p-5">
                <h3 className="text-lg font-semibold text-white">
                    Simulation history
                </h3>
                <p className="mt-2 text-sm text-slate-400">
                    No simulations yet. Run your first simulation to see results.
                </p>
            </section>
        )
    }

    return (
        <section className="mt-8 rounded-xl border border-slate-800 bg-slate-950 p-5">
            <div className="flex items-center justify-between">
                <h3 className="text-lg font-semibold text-white">
                    Simulation history
                </h3>
                <span className="text-sm text-slate-400">
                    {simulations.length} runs
                </span>
            </div>

            <div className="mt-5 space-y-3">
                {simulations.map((simulation) => {
                    const isSelected =
                        selectedSimulationId === simulation.id

                    return (
                        <button
                            key={simulation.id}
                            type="button"
                            onClick={() => onSelectSimulation(simulation.id)}
                            className={`w-full rounded-lg border p-4 text-left transition ${
                                isSelected
                                    ? 'border-lime-400 bg-slate-900'
                                    : 'border-slate-800 bg-slate-900 hover:border-slate-600'
                            }`}
                        >
                            <div className="flex flex-wrap items-center justify-between gap-3">
                                <div>
                                    <p className="font-semibold text-white">
                                        {new Date(
                                            simulation.createdAt
                                        ).toLocaleString()}
                                    </p>
                                    <p className="mt-1 text-sm text-slate-400">
                                        {simulation.usersProcessed.toLocaleString()} of{' '}
                                        {simulation.targetUsers.toLocaleString()} users processed
                                    </p>
                                </div>

                                <span
                                    className={`rounded-full px-3 py-1 text-xs font-semibold ${
                                        simulation.status === 'COMPLETED'
                                            ? 'bg-lime-950 text-lime-400'
                                            : simulation.status === 'FAILED'
                                                ? 'bg-pink-950 text-pink-400'
                                                : 'bg-orange-950 text-orange-400'
                                    }`}
                                >
                                    {simulation.status}
                                </span>
                            </div>
                        </button>
                    )
                })}
            </div>
        </section>
    )
}
