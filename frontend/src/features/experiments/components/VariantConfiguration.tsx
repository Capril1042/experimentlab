type VariantConfigurationProps = {
    configuration: Record<string, unknown>
}

function formatLabel(key: string): string {
    return key
        .replace(/([a-z0-9])([A-Z])/g, '$1 $2')
        .replace(/[_-]/g, ' ')
        .replace(/\b\w/g, (character) => character.toUpperCase())
}

function formatValue(value: unknown): string {
    if (value === null) return 'null'

    if (typeof value === 'object') {
        return JSON.stringify(value, null, 2)
    }

    return String(value)
}

export function VariantConfiguration({
                                         configuration,
                                     }: VariantConfigurationProps) {
    const entries = Object.entries(configuration)

    if (entries.length === 0) {
        return (
            <p className="text-sm text-slate-500">
                No configuration defined.
            </p>
        )
    }

    return (
        <div className="grid gap-3 sm:grid-cols-2">
            {entries.map(([key, value]) => (
                <div
                    key={key}
                    className="rounded-lg border border-slate-800 bg-slate-900 p-3"
                >
                    <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">
                        {formatLabel(key)}
                    </p>

                    <p className="mt-2 break-words text-sm text-slate-200">
                        {formatValue(value)}
                    </p>
                </div>
            ))}
        </div>
    )
}
