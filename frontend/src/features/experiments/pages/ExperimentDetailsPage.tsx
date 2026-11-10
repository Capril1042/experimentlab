
import { Link, useParams } from 'react-router-dom'
import { useExperiment } from '../hooks/useExperiment'
import { useVariants } from '../hooks/useVariants'
import { CreateVariantForm } from '../components/CreateVariantForm'
import { useState } from 'react'
import { EditVariantForm } from '../components/EditVariantForm'
import { useDeleteVariant } from '../hooks/useDeleteVariant'

export function ExperimentDetailsPage() {
    const { id } = useParams<{ id: string }>()
    const {
        data: experiment,
        isLoading,
        isError,
    } = useExperiment(id ?? '')
    const {
        data: variants,
        isLoading: variantsLoading,
        isError: variantsError,
    } = useVariants(id ?? '')
    const [editingVariantId, setEditingVariantId] = useState<string | null>(null)
    const deleteVariant = useDeleteVariant()

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

    {deleteVariant.isError && (
        <p className="mt-4 text-sm text-pink-400">
            Could not delete variant. Please try again.
        </p>
    )}

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


                <div className="mt-10 border-t border-slate-800 pt-8">
                    <div className="flex items-center justify-between">
                        <div>
                            <h2 className="text-xl font-semibold text-white">
                                Variants
                            </h2>
                            <p className="mt-1 text-sm text-slate-400">
                                Manage the variations in this experiment.
                            </p>
                        </div>
                        <span className="rounded-full bg-slate-800 px-3 py-1 text-sm text-lime-400">
            {variants?.length ?? 0} variants
        </span>
                    </div>

                    {variantsLoading && (
                        <p className="mt-6 text-slate-400">Loading variants...</p>
                    )}

                    {variantsError && (
                        <p className="mt-6 text-pink-400">
                            Could not load variants.
                        </p>
                    )}

                    {!variantsLoading && !variantsError && variants?.length === 0 && (
                        <p className="mt-6 text-slate-400">
                            No variants yet. Create your first variant to get started.
                        </p>
                    )}

                    <CreateVariantForm experimentId={id ?? ''} />

                        {variants && variants.length > 0 && (
                            <>
                            <CreateVariantForm experimentId={id ?? ''} />
                        <div className="mt-6 grid gap-4 sm:grid-cols-2">

                            {variants.map((variant) => (
                                <article
                                    key={variant.id}
                                    className="rounded-lg border border-slate-800 bg-slate-950 p-5"
                                >
                                    {editingVariantId === variant.id ? (
                                        <EditVariantForm
                                            experimentId={id ?? ''}
                                            variant={variant}
                                            onCancel={() => setEditingVariantId(null)}
                                        />
                                    ) : (
                                        <>
                                            <div className="flex items-start justify-between">
                                                <h3 className="font-semibold text-white">
                                                    {variant.name}
                                                </h3>
                                                <span className="rounded-full bg-slate-800 px-2 py-1 text-xs text-lime-400">
                        {variant.trafficPercentage}%
                    </span>
                                            </div>

                                            <p className="mt-2 text-sm text-slate-400">
                                                {variant.description || 'No description'}
                                            </p>

                                            <div className="mt-4">
                                                <h4 className="text-xs font-semibold uppercase tracking-wide text-slate-500">
                                                    Configuration
                                                </h4>
                                                <pre className="mt-2 overflow-x-auto rounded-md bg-slate-900 p-3 text-xs text-slate-300">
                        {JSON.stringify(variant.configuration, null, 2)}
                    </pre>
                                            </div>


                                            <div className="mt-4 flex gap-3">
                                                <button
                                                    type="button"
                                                    onClick={() => setEditingVariantId(variant.id)}
                                                    className="rounded-md border border-slate-700 px-3 py-2 text-sm text-lime-400 hover:bg-slate-800"
                                                >
                                                    Edit variant
                                                </button>

                                                <button
                                                    type="button"
                                                    disabled={deleteVariant.isPending}
                                                    onClick={() => {
                                                        if (window.confirm(`Delete "${variant.name}"?`)) {
                                                            deleteVariant.mutate({
                                                                experimentId: id ?? '',
                                                                variantId: variant.id,
                                                            })
                                                        }
                                                    }}
                                                    className="rounded-md border border-pink-900 px-3 py-2 text-sm text-pink-400 hover:bg-pink-950 disabled:opacity-50"
                                                >
                                                    Delete variant
                                                </button>
                                            </div>

                                        </>
                                    )}
                                </article>
                            ))}
                        </div>

                            </>

                    )}

                </div>

            </section>
        </main>
    )
}
