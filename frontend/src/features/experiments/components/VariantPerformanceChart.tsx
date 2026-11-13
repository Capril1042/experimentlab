import {
    BarChart,
    Bar,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
    ResponsiveContainer,
} from 'recharts'

import type { VariantSimulationResult } from '../types/simulation'

interface VariantPerformanceChartProps {
    results: VariantSimulationResult[]
}

export function VariantPerformanceChart({
                                            results,
                                        }: VariantPerformanceChartProps) {
    const chartData = results.map((result) => ({
        variant: result.variantName,
        clicks: result.clicks,
        conversions: result.conversions,
    }))

    return (
        <section className="flex h-full min-w-0 flex-col rounded-xl border border-slate-800 bg-slate-950 p-5">
            <div>
                <h3 className="text-lg font-semibold text-white">
                    Variant performance
                </h3>

                <p className="mt-1 text-sm text-slate-400">
                    Compare clicks and conversions across variants.
                </p>
            </div>

            <div className="mt-5 min-h-[280px] flex-1">
                <ResponsiveContainer width="100%" height="100%">
                    <BarChart
                        data={chartData}
                        margin={{ top: 10, right: 10, left: 0, bottom: 5 }}
                    >
                        <CartesianGrid
                            strokeDasharray="3 3"
                            stroke="#334155"
                            vertical={false}
                        />

                        <XAxis
                            dataKey="variant"
                            stroke="#94a3b8"
                            tickLine={false}
                            axisLine={false}
                        />

                        <YAxis
                            stroke="#94a3b8"
                            tickLine={false}
                            axisLine={false}
                        />

                        <Tooltip
                            contentStyle={{
                                backgroundColor: '#0f172a',
                                border: '1px solid #334155',
                                borderRadius: '8px',
                                color: '#fff',
                            }}
                        />

                        <Legend />

                        <Bar
                            dataKey="clicks"
                            name="Clicks"
                            fill="#a3e635"
                            radius={[5, 5, 0, 0]}
                        />

                        <Bar
                            dataKey="conversions"
                            name="Conversions"
                            fill="#ec4899"
                            radius={[5, 5, 0, 0]}
                        />
                    </BarChart>
                </ResponsiveContainer>
            </div>
        </section>
    )
}
