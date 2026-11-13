import { useSimulationResults } from '../hooks/useSimulationResults'
import { VariantPerformanceChart } from './VariantPerformanceChart'
import { ConversionRateChart } from './ConversionRateChart'

interface SimulationAnalyticsProps {
    experimentId: string
    simulationId: string
}

export function SimulationAnalytics({
                                        experimentId,
                                        simulationId,
                                    }: SimulationAnalyticsProps) {
    const {
        data: results,
        isLoading,
        isError,
    } = useSimulationResults(experimentId, simulationId)

    if (isLoading) {
        return (
            <section className="mt-8 border-t border-slate-800 pt-6">
                <p className="text-slate-400">Loading analytics...</p>
            </section>
        )
    }

    if (isError) {
        return (
            <section className="mt-8 border-t border-slate-800 pt-6">
                <p className="text-pink-400">
                    Could not load simulation analytics.
                </p>
            </section>
        )
    }

    if (!results || results.length === 0) {
        return (
            <section className="mt-8 border-t border-slate-800 pt-6">
                <h3 className="text-xl font-semibold text-white">
                    Simulation analytics
                </h3>
                <p className="mt-2 text-sm text-slate-400">
                    No variant results available for this simulation.
                </p>
            </section>
        )
    }

    const totals = results.reduce(
        (total, result) => ({
            assignedUsers: total.assignedUsers + result.assignedUsers,
            exposures: total.exposures + result.exposures,
            clicks: total.clicks + result.clicks,
            conversions: total.conversions + result.conversions,
        }),
        {
            assignedUsers: 0,
            exposures: 0,
            clicks: 0,
            conversions: 0,
        }
    )

    const metrics = [
        { label: 'Assigned users', value: totals.assignedUsers },
        { label: 'Exposures', value: totals.exposures },
        { label: 'Clicks', value: totals.clicks },
        { label: 'Conversions', value: totals.conversions },
    ]

    return (
        <section className="mt-8 border-t border-slate-800 pt-6">
            {/* Analytics heading */}
            <div>
                <h3 className="text-xl font-semibold text-white">
                    Simulation analytics
                </h3>
                <p className="mt-1 text-sm text-slate-400">
                    Performance breakdown by experiment variant.
                </p>
            </div>

            {/* Summary metrics */}
            <div className="mt-6 grid grid-cols-2 gap-3 lg:grid-cols-4">
                {metrics.map((metric) => (
                    <div
                        key={metric.label}
                        className="rounded-lg border border-slate-800 bg-slate-900 p-4"
                    >
                        <p className="text-sm text-slate-400">
                            {metric.label}
                        </p>
                        <p className="mt-2 text-2xl font-bold text-white">
                            {metric.value.toLocaleString()}
                        </p>
                    </div>
                ))}
            </div>

            {/* Performance charts */}
            <div className="mt-6 grid gap-4 xl:grid-cols-2">
                <div className="min-w-0">
                    <VariantPerformanceChart results={results} />
                </div>

                <div className="min-w-0">
                    <ConversionRateChart results={results} />
                </div>
            </div>

            {/* Variant comparison */}
            <div className="mt-8">
                <div className="mb-4">
                    <h4 className="text-lg font-semibold text-white">
                        Variant comparison
                    </h4>
                    <p className="mt-1 text-sm text-slate-400">
                        Compare engagement and conversion performance.
                    </p>
                </div>

                <div className="overflow-x-auto rounded-lg border border-slate-800">
                    <table className="w-full min-w-[650px] text-left">
                        <thead className="bg-slate-900">
                        <tr className="text-sm text-slate-400">
                            <th className="p-4 font-medium">Variant</th>
                            <th className="p-4 font-medium">CTR</th>
                            <th className="p-4 font-medium">Conversion rate</th>
                            <th className="p-4 font-medium">Click to conversion</th>
                            <th className="p-4 font-medium">Conversions</th>
                        </tr>
                        </thead>

                        <tbody>
                        {results.map((result) => (
                            <tr
                                key={result.variantId}
                                className="border-t border-slate-800 text-sm transition hover:bg-slate-900/60"
                            >
                                <td className="p-4 font-semibold text-white">
                                    {result.variantName}
                                </td>
                                <td className="p-4 font-medium text-lime-400">
                                    {result.clickThroughRate.toFixed(2)}%
                                </td>
                                <td className="p-4 text-slate-200">
                                    {result.conversionRate.toFixed(2)}%
                                </td>
                                <td className="p-4 text-slate-200">
                                    {result.clickToConversionRate.toFixed(2)}%
                                </td>
                                <td className="p-4 text-slate-200">
                                    {result.conversions.toLocaleString()}
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </section>
    )
}
