import { Link, useParams } from 'react-router-dom'
import { useEffect, useState } from 'react'
import { useExperiment } from '../hooks/useExperiment'
import { useVariants } from '../hooks/useVariants'
import { useDeleteVariant } from '../hooks/useDeleteVariant'
import { useStartExperiment } from '../hooks/useStartExperiment'

import { CreateVariantForm } from '../components/CreateVariantForm'
import { EditVariantForm } from '../components/EditVariantForm'
import { SimulationControls } from '../components/SimulationControls'
import { SimulationHistory } from '../components/SimulationHistory'
import { SimulationAnalytics } from '../components/SimulationAnalytics'
import { VariantConfiguration } from '../components/VariantConfiguration'
import { useSimulations } from '../hooks/useSimulations'

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

    const { data: simulations } = useSimulations(id ?? '')

    const [selectedSimulationId, setSelectedSimulationId] =
        useState<string | null>(null)

    const [lastLatestCompletedId, setLastLatestCompletedId] =
        useState<string | null>(null)

    const [editingVariantId, setEditingVariantId] =
        useState<string | null>(null)

    const deleteVariant = useDeleteVariant()
    const startExperiment = useStartExperiment()

    const totalTraffic = (variants ?? []).reduce(
        (total, variant) => total + variant.trafficPercentage,
        0
    )

    const trafficIsValid = Math.abs(totalTraffic - 100) < 0.001
    const isDraft = experiment?.status === 'DRAFT'
    const isConfigurable = isDraft

    const canStartExperiment =
        isDraft &&
        trafficIsValid &&
        !variantsLoading &&
        !variantsError &&
        Boolean(variants && variants.length > 0)

    const latestCompletedSimulation = simulations?.find(
        (simulation) => simulation.status === 'COMPLETED'
    )

    useEffect(() => {
        if (
            latestCompletedSimulation &&
            latestCompletedSimulation.id !== lastLatestCompletedId
        ) {
            setSelectedSimulationId(latestCompletedSimulation.id)
            setLastLatestCompletedId(latestCompletedSimulation.id)
        }
    }, [latestCompletedSimulation?.id, lastLatestCompletedId])

    if (isLoading) {
        return (
            <main className="min-h-screen bg-slate-950 px-4 py-6 text-white sm:px-6 lg:px-8">
                <p className="text-slate-400">Loading experiment...</p>
            </main>
        )
    }

    if (isError || !experiment) {
        return (
            <main className="min-h-screen bg-slate-950 px-4 py-6 text-white sm:px-6 lg:px-8">
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
        <main className="min-h-screen bg-slate-950 px-4 py-6 text-white sm:px-6 lg:px-8">
            <div className="mx-auto max-w-7xl">
                <Link
                    to="/"
                    className="inline-flex items-center text-sm text-lime-400 transition hover:text-lime-300"
                >
                    ← Back to experiments
                </Link>

                {/* Experiment overview */}
                <section className="mt-6 rounded-xl border border-slate-800 bg-slate-900 p-5 sm:p-6">
                    <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
                        <div className="min-w-0">
                            <h1 className="text-2xl font-bold tracking-tight text-white sm:text-3xl">
                                {experiment.name}
                            </h1>

                            {experiment.description && (
                                <p className="mt-2 text-sm text-slate-400 sm:text-base">
                                    {experiment.description}
                                </p>
                            )}
                        </div>

                        <span className="w-fit shrink-0 rounded-full bg-slate-800 px-3 py-1 text-xs font-semibold tracking-wide text-orange-400">
                            {experiment.status}
                        </span>
                    </div>

                    <div className="mt-6 grid gap-3 md:grid-cols-2">
                        <div className="rounded-lg border border-slate-800 bg-slate-950/70 p-4">
                            <h2 className="text-xs font-semibold uppercase tracking-wider text-slate-500">
                                Hypothesis
                            </h2>
                            <p className="mt-2 text-sm leading-relaxed text-slate-200">
                                {experiment.hypothesis}
                            </p>
                        </div>

                        <div className="rounded-lg border border-slate-800 bg-slate-950/70 p-4">
                            <h2 className="text-xs font-semibold uppercase tracking-wider text-slate-500">
                                Primary metric
                            </h2>
                            <p className="mt-2 text-base font-semibold capitalize text-lime-400">
                                {experiment.primaryMetric.replaceAll('_', ' ')}
                            </p>
                        </div>
                    </div>

                    <div className="mt-4 flex flex-wrap gap-x-8 gap-y-2 border-t border-slate-800 pt-4">
                        <div>
                            <p className="text-xs text-slate-500">Created</p>
                            <p className="mt-1 text-sm text-slate-300">
                                {new Date(experiment.createdAt).toLocaleString()}
                            </p>
                        </div>

                        <div>
                            <p className="text-xs text-slate-500">Last updated</p>
                            <p className="mt-1 text-sm text-slate-300">
                                {new Date(experiment.updatedAt).toLocaleString()}
                            </p>
                        </div>
                    </div>
                </section>

                {/* Variants */}
                <section className="mt-8">
                    <div className="flex items-center justify-between gap-4">
                        <div>
                            <h2 className="text-xl font-semibold text-white">
                                Variants
                            </h2>
                            <p className="mt-1 text-sm text-slate-400">
                                Manage the variations in this experiment.
                            </p>
                        </div>

                        <span className="shrink-0 rounded-full bg-slate-800 px-3 py-1 text-sm text-lime-400">
                            {variants?.length ?? 0} variants
                        </span>
                    </div>

                    {variantsLoading && (
                        <p className="mt-5 text-sm text-slate-400">
                            Loading variants...
                        </p>
                    )}

                    {variantsError && (
                        <p className="mt-5 text-sm text-pink-400">
                            Could not load variants.
                        </p>
                    )}

                    {!variantsLoading &&
                        !variantsError &&
                        variants?.length === 0 && (
                            <p className="mt-5 text-sm text-slate-400">
                                No variants yet. Create your first variant to get started.
                            </p>
                        )}

                    {isConfigurable && (
                        <div className="mt-5">
                            <CreateVariantForm experimentId={id ?? ''} />
                        </div>
                    )}

                    {variants && variants.length > 0 && (
                        <div className="mt-5 grid gap-4 sm:grid-cols-2">
                            {variants.map((variant) => (
                                <article
                                    key={variant.id}
                                    className="rounded-xl border border-slate-800 bg-slate-950 p-5"
                                >
                                    {isConfigurable &&
                                    editingVariantId === variant.id ? (
                                        <EditVariantForm
                                            experimentId={id ?? ''}
                                            variant={variant}
                                            onCancel={() =>
                                                setEditingVariantId(null)
                                            }
                                        />
                                    ) : (
                                        <>
                                            <div className="flex items-start justify-between gap-3">
                                                <h3 className="font-semibold text-white">
                                                    {variant.name}
                                                </h3>
                                                <span className="shrink-0 rounded-full bg-slate-800 px-2.5 py-1 text-xs font-semibold text-lime-400">
                                                    {variant.trafficPercentage}%
                                                </span>
                                            </div>

                                            <p className="mt-2 text-sm text-slate-400">
                                                {variant.description || 'No description'}
                                            </p>

                                            <div className="mt-4">
                                                <h4 className="mb-3 text-xs font-semibold uppercase tracking-wide text-slate-500">
                                                    Configuration
                                                </h4>
                                                <VariantConfiguration
                                                    configuration={variant.configuration}
                                                />
                                            </div>

                                            {isConfigurable && (
                                                <div className="mt-4 flex flex-wrap gap-2">
                                                    <button
                                                        type="button"
                                                        onClick={() =>
                                                            setEditingVariantId(variant.id)
                                                        }
                                                        className="rounded-md border border-slate-700 px-3 py-2 text-sm text-lime-400 transition hover:bg-slate-800"
                                                    >
                                                        Edit variant
                                                    </button>

                                                    <button
                                                        type="button"
                                                        disabled={deleteVariant.isPending}
                                                        onClick={() => {
                                                            if (
                                                                window.confirm(
                                                                    `Delete "${variant.name}"?`
                                                                )
                                                            ) {
                                                                deleteVariant.mutate({
                                                                    experimentId: id ?? '',
                                                                    variantId: variant.id,
                                                                })
                                                            }
                                                        }}
                                                        className="rounded-md border border-pink-900 px-3 py-2 text-sm text-pink-400 transition hover:bg-pink-950 disabled:opacity-50"
                                                    >
                                                        Delete variant
                                                    </button>
                                                </div>
                                            )}
                                        </>
                                    )}
                                </article>
                            ))}
                        </div>
                    )}

                    {/* Traffic allocation */}
                    <div className="mt-5 rounded-xl border border-slate-800 bg-slate-950 p-5">
                        <div className="flex items-center justify-between gap-4">
                            <div>
                                <h3 className="font-semibold text-white">
                                    Traffic allocation
                                </h3>
                                <p className="mt-1 text-sm text-slate-400">
                                    Distribution across experiment variants
                                </p>
                            </div>

                            <span
                                className={`text-lg font-bold ${
                                    trafficIsValid
                                        ? 'text-lime-400'
                                        : 'text-orange-400'
                                }`}
                            >
                                {totalTraffic.toFixed(1)}%
                            </span>
                        </div>

                        <div className="mt-4 h-2.5 overflow-hidden rounded-full bg-slate-800">
                            <div
                                className={`h-full rounded-full transition-all ${
                                    trafficIsValid
                                        ? 'bg-lime-400'
                                        : 'bg-orange-400'
                                }`}
                                style={{
                                    width: `${Math.min(Math.max(totalTraffic, 0), 100)}%`,
                                }}
                            />
                        </div>

                        <p className="mt-3 text-sm text-slate-400">
                            {!variants || variants.length === 0
                                ? 'Create variants to allocate traffic.'
                                : trafficIsValid
                                    ? 'Traffic allocation is valid. Ready to start.'
                                    : totalTraffic > 100
                                        ? `Reduce allocation by ${(totalTraffic - 100).toFixed(1)}%.`
                                        : `Allocate ${(100 - totalTraffic).toFixed(1)}% more traffic to reach 100%.`}
                        </p>
                    </div>

                    {/* Start experiment */}
                    {isDraft && (
                        <div className="mt-5">
                            <button
                                type="button"
                                disabled={!canStartExperiment || startExperiment.isPending}
                                onClick={() => startExperiment.mutate(id ?? '')}
                                className="rounded-lg bg-lime-400 px-5 py-3 font-semibold text-slate-950 transition hover:bg-lime-300 disabled:cursor-not-allowed disabled:opacity-40"
                            >
                                {startExperiment.isPending
                                    ? 'Starting experiment...'
                                    : 'Start Experiment'}
                            </button>

                            {!trafficIsValid && variants && variants.length > 0 && (
                                <p className="mt-2 text-sm text-orange-400">
                                    Traffic must total exactly 100% before starting.
                                </p>
                            )}

                            {startExperiment.isError && (
                                <p className="mt-3 text-sm text-pink-400">
                                    Could not start experiment. Please check the traffic allocation and try again.
                                </p>
                            )}
                        </div>
                    )}

                    {/* Configuration lock */}
                    {!isConfigurable && (
                        <div className="mt-5 rounded-lg border border-lime-900 bg-slate-950 p-4">
                            <p className="font-semibold text-lime-400">
                                Configuration locked
                            </p>
                            <p className="mt-1 text-sm text-slate-400">
                                This experiment has started. Variant creation,
                                editing, and deletion are disabled.
                            </p>
                        </div>
                    )}

                    {/* Simulation controls */}
                    {experiment.status === 'RUNNING' && (
                        <SimulationControls experimentId={id ?? ''} />
                    )}

                    {/* Simulation history and analytics */}
                    {experiment.status === 'RUNNING' && (
                        <>
                            <SimulationHistory
                                experimentId={id ?? ''}
                                selectedSimulationId={selectedSimulationId}
                                onSelectSimulation={setSelectedSimulationId}
                            />

                            {selectedSimulationId && (
                                <SimulationAnalytics
                                    experimentId={id ?? ''}
                                    simulationId={selectedSimulationId}
                                />
                            )}
                        </>
                    )}

                    {deleteVariant.isError && (
                        <p className="mt-4 text-sm text-pink-400">
                            Could not delete variant. Please try again.
                        </p>
                    )}
                </section>
            </div>
        </main>
    )
}
