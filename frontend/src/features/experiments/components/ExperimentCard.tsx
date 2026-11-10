
import { Link } from 'react-router-dom'
import type { Experiment } from '../types/experiment'

type ExperimentCardProps = {
    experiment: Experiment
}

export function ExperimentCard({ experiment }: ExperimentCardProps) {
    return (
        <Link
            to={`/experiments/${experiment.id}`}
            className="block rounded-lg border border-slate-800 bg-slate-900 p-5 transition hover:border-lime-400"
        >
            <article>
                <div className="flex items-start justify-between">
                    <h3 className="text-lg font-semibold text-lime-400">
                        {experiment.name}
                    </h3>

                    <span className="rounded-full bg-slate-800 px-3 py-1 text-xs text-orange-400">
                        {experiment.status}
                    </span>
                </div>

                <p className="mt-2 text-slate-400">
                    {experiment.description}
                </p>

                <p className="mt-3 text-sm text-slate-500">
                    Hypothesis: {experiment.hypothesis}
                </p>

                <div className="mt-4 flex items-center justify-between">
                    <span className="text-sm text-slate-400">
                        {experiment.primaryMetric.replaceAll('_', ' ')}
                    </span>

                    <span className="text-sm text-lime-400">
                        View details →
                    </span>
                </div>
            </article>
        </Link>
    )
}
