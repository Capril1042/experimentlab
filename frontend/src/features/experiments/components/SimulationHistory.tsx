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

    return (
        <section className="mt-8 border-t border-slate-800 pt-6">
            <div className="flex items-center justify-between gap-4">
                <div>
                    <h3 className="text-lg font-semibold text-white">
                        Simulation history
                    </h3>
                    <p className="mt-1 text-sm text-slate-400">
                        Select a run to explore its results.
                    </p>
                </div>

                {!isLoading && !isError && simulations && (
                    <span className="shrink-0 text-sm text-slate-400">
                        {simulations.length} runs
                    </span>
                )}
            </div>

            {isLoading && (
                <p className="mt-4 text-sm text-slate-400">
                    Loading simulation history...
                </p>
            )}

            {isError && (
                <p className="mt-4 text-sm text-pink-400">
                    Could not load simulation history.
                </p>
            )}

            {!isLoading && !isError && simulations?.length === 0 && (
                <p className="mt-4 text-sm text-slate-400">
                    No simulations yet. Run your first simulation to see results.
                </p>
            )}

            {simulations && simulations.length > 0 && (
                <div className="mt-4 grid gap-3 sm:grid-cols-2">
                    {simulations.map((simulation) => {
                        const isSelected =
                            selectedSimulationId === simulation.id

                        return (
                            <button
                                key={simulation.id}
                                type="button"
                                onClick={() =>
                                    onSelectSimulation(simulation.id)
                                }
                                className={`rounded-lg border p-4 text-left transition focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-lime-400 ${
                                    isSelected
                                        ? 'border-lime-400 bg-slate-950'
                                        : 'border-slate-800 bg-slate-950 hover:border-slate-600'
                                }`}
                            >
                                <div className="flex items-center justify-between gap-3">
                                    <span className="font-medium text-white">
                                        {new Date(
                                            simulation.createdAt
                                        ).toLocaleString()}
                                    </span>

                                    <span
                                        className={`shrink-0 rounded-full px-2 py-1 text-xs font-semibold ${
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

                                <p className="mt-2 text-sm text-slate-400">
                                    {simulation.usersProcessed.toLocaleString()} of{' '}
                                    {simulation.targetUsers.toLocaleString()} users processed
                                </p>

                                {isSelected && (
                                    <p className="mt-2 text-xs font-medium text-lime-400">
                                        Selected run
                                    </p>
                                )}
                            </button>
                        )
                    })}
                </div>
            )}
        </section>
    )
}
