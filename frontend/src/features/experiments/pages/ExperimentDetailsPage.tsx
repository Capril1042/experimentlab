
import { Link, useParams } from 'react-router-dom'
import { useExperiment } from '../hooks/useExperiment'

export function ExperimentDetailsPage() {
    const { id } = useParams<{ id: string }>()
    const {
        data: experiment,
        isLoading,
        isError,
    } = useExperiment(id ?? '')

    if (isLoading) {
        return (
            <main className="min-h-screen bg-slate-950 p-8 text-white">
                <p className="text-slate-400">
                    Loading experiment...
                </p>
            </main>
        )
    }

    if (isError || !experiment) {
        return (
            <main className="min-h-screen bg-slate-950 p-8 text-white">
                <p className="text-pink-400">
                    Could not find this experiment.
                </p>

                <Link
                    to="/"
                    className="mt-4 inline-block text-lime-400 hover:underline"
                >
                    Back to experiments
                </Link>
            </main>
        )
    }

    return (
        <main className="min-h-screen bg-slate-950 p-8 text-white">
            <Link
                to="/"
                className="text-sm text-lime-400 hover:underline"
            >
                ← Back to experiments
            </Link>

            <section className="mt-8 rounded-xl border border-slate-800 bg-slate-900 p-6">
                <div className="flex items-start justify-between">
                    <div>
                        <h1 className="text-3xl font-bold text-white">
                            {experiment.name}
                        </h1>

                        <p className="mt-2 text-slate-400">
                            {experiment.description}
                        </p>
                    </div>

                    <span className="rounded-full bg-slate-800 px-3 py-1 text-sm text-orange-400">
                        {experiment.status}
                    </span>
                </div>

                <div className="mt-8">
                    <h2 className="text-sm font-semibold uppercase tracking-wide text-slate-500">
                        Hypothesis
                    </h2>

                    <p className="mt-2 text-slate-300">
                        {experiment.hypothesis}
                    </p>
                </div>

                <div className="mt-8">
                    <h2 className="text-sm font-semibold uppercase tracking-wide text-slate-500">
                        Primary metric
                    </h2>

                    <p className="mt-2 text-lg text-lime-400">
                        {experiment.primaryMetric.replaceAll('_', ' ')}
                    </p>
                </div>

                <div className="mt-8 grid gap-4 sm:grid-cols-2">
                    <div>
                        <h2 className="text-sm text-slate-500">
                            Created
                        </h2>
                        <p className="mt-1 text-slate-300">
                            {new Date(experiment.createdAt).toLocaleString()}
                        </p>
                    </div>

                    <div>
                        <h2 className="text-sm text-slate-500">
                            Last updated
                        </h2>
                        <p className="mt-1 text-slate-300">
                            {new Date(experiment.updatedAt).toLocaleString()}
                        </p>
                    </div>
                </div>
            </section>
        </main>
    )
}
