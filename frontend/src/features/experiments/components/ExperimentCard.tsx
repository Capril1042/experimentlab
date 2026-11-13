import { Link } from 'react-router-dom'
import type { Experiment } from '../types/experiment'

type ExperimentCardProps = {
    experiment: Experiment
}

export function ExperimentCard({ experiment }: ExperimentCardProps) {
    return (
        <Link
            to={`/experiments/${experiment.id}`}
            className="group flex h-full min-w-0 rounded-lg border border-slate-800 bg-slate-900 p-5 transition hover:border-lime-400 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-lime-400"
        >
            <article className="flex min-w-0 flex-1 flex-col">
                <div className="flex items-start justify-between gap-3">
                    <h3 className="min-w-0 break-words text-lg font-semibold text-lime-400">
                        {experiment.name}
                    </h3>

                    <span className="shrink-0 rounded-full bg-slate-800 px-3 py-1 text-xs text-orange-400">
                        {experiment.status}
                    </span>
                </div>

                <p className="mt-2 text-slate-400">
                    {experiment.description}
                </p>

                <p className="mt-3 text-sm text-slate-500">
                    Hypothesis: {experiment.hypothesis}
                </p>

                <div className="mt-auto flex items-center justify-between gap-3 pt-4">
                    <span className="text-sm text-slate-400">
                        {experiment.primaryMetric.replaceAll('_', ' ')}
                    </span>

                    <span className="shrink-0 text-sm text-lime-400 transition group-hover:translate-x-1">
                        View details →
                    </span>
                </div>
            </article>
        </Link>
    )
}
