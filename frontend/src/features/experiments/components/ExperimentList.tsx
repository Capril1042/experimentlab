
import type { Experiment } from '../types/experiment'
import { ExperimentCard } from './ExperimentCard'

type ExperimentListProps = {
    experiments: Experiment[]
    isLoading: boolean
    isError: boolean
}

export function ExperimentList({
                                   experiments,
                                   isLoading,
                                   isError,
                               }: ExperimentListProps) {

    if (isLoading) {
        return (
            <p className="mt-4 text-slate-400">
                Loading experiments...
            </p>
        )
    }

    if (isError) {
        return (
            <p className="mt-4 text-pink-400">
                Could not load experiments.
            </p>
        )
    }

    if (experiments.length === 0) {
        return (
            <p className="mt-4 text-slate-400">
                No experiments yet. Create your first experiment!
            </p>
        )
    }

    return (
        <div className="mt-4 grid grid-cols-1 gap-5 md:grid-cols-2 xl:grid-cols-3">
            {experiments.map((experiment) => (
                <ExperimentCard
                    key={experiment.id}
                    experiment={experiment}
                />
            ))}
        </div>
    )
}
