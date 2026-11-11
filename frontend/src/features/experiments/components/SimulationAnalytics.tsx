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
            <section className="mt-8">
                <p className="text-slate-400">Loading analytics...</p>
            </section>
        )
    }

    if (isError) {
        return (
            <section className="mt-8">
                <p className="text-pink-400">
                    Could not load simulation analytics.
                </p>
            </section>
        )
    }

    if (!results || results.length === 0) {
        return (
            <section className="mt-8 rounded-xl border border-slate-800 bg-slate-950 p-5">
                <h3 className="text-lg font-semibold text-white">
                    Analytics
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
        <section className="mt-8 rounded-xl border border-slate-800 bg-slate-950 p-5">
            <div>
                <h3 className="text-xl font-semibold text-white">
                    Simulation analytics
                </h3>
                <p className="mt-1 text-sm text-slate-400">
                    Performance breakdown by experiment variant.
                </p>
            </div>

            <div className="mt-6 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
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

            <VariantPerformanceChart results={results} />
            <ConversionRateChart results={results} />

            <div className="mt-8 overflow-x-auto">
                <table className="w-full min-w-[650px] text-left">
                    <thead>
                    <tr className="border-b border-slate-800 text-sm text-slate-400">
                        <th className="p-3">Variant</th>
                        <th className="p-3">CTR</th>
                        <th className="p-3">Conversion rate</th>
                        <th className="p-3">Click to conversion</th>
                        <th className="p-3">Conversions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {results.map((result) => (
                        <tr
                            key={result.variantId}
                            className="border-b border-slate-800 text-sm"
                        >
                            <td className="p-3 font-semibold text-white">
                                {result.variantName}
                            </td>
                            <td className="p-3 text-lime-400">
                                {result.clickThroughRate.toFixed(2)}%
                            </td>
                            <td className="p-3 text-white">
                                {result.conversionRate.toFixed(2)}%
                            </td>
                            <td className="p-3 text-white">
                                {result.clickToConversionRate.toFixed(2)}%
                            </td>
                            <td className="p-3 text-white">
                                {result.conversions.toLocaleString()}
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </section>
    )
}
