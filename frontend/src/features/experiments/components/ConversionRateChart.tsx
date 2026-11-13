import {
    BarChart,
    Bar,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    ResponsiveContainer,
    LabelList,
} from 'recharts'

import type { VariantSimulationResult } from '../types/simulation'

interface ConversionRateChartProps {
    results: VariantSimulationResult[]
}

export function ConversionRateChart({
                                        results,
                                    }: ConversionRateChartProps) {
    const chartData = results.map((result) => ({
        variant: result.variantName,
        conversionRate: result.conversionRate,
    }))

    const maxConversionRate = Math.max(
        ...chartData.map((item) => item.conversionRate),
        0
    )

    const yAxisMax = Math.max(
        10,
        Math.ceil(maxConversionRate / 5) * 5
    )

    return (
        <section className="flex h-full min-w-0 flex-col rounded-xl border border-slate-800 bg-slate-950 p-5">
            <div>
                <h3 className="text-lg font-semibold text-white">
                    Conversion rate by variant
                </h3>

                <p className="mt-1 text-sm text-slate-400">
                    Percentage of exposed users who completed a conversion.
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
                            domain={[0, yAxisMax]}
                            tickCount={5}
                            tickFormatter={(value: number) => `${value}%`}
                        />

                        <Tooltip
                            formatter={(value) => [
                                `${Number(value).toFixed(2)}%`,
                                'Conversion rate',
                            ]}
                            contentStyle={{
                                backgroundColor: '#0f172a',
                                border: '1px solid #334155',
                                borderRadius: '8px',
                                color: '#fff',
                            }}
                        />

                        <Bar
                            dataKey="conversionRate"
                            name="Conversion rate"
                            fill="#a3e635"
                            radius={[5, 5, 0, 0]}
                        >
                            <LabelList
                                dataKey="conversionRate"
                                position="top"
                                formatter={(value) =>
                                    `${Number(value).toFixed(2)}%`
                                }
                                fill="#ffffff"
                                fontSize={13}
                            />
                        </Bar>
                    </BarChart>
                </ResponsiveContainer>
            </div>
        </section>
    )
}
