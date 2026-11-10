
import { useState } from 'react'
import type { Variant } from '../types/variant'
import { useUpdateVariant } from '../hooks/useUpdateVariant'

type EditVariantFormProps = {
    experimentId: string
    variant: Variant
    onCancel: () => void
}

export function EditVariantForm({
                                    experimentId,
                                    variant,
                                    onCancel,
                                }: EditVariantFormProps) {
    const updateVariant = useUpdateVariant()

    const [name, setName] = useState(variant.name)
    const [description, setDescription] = useState(
        variant.description ?? ''
    )
    const [trafficPercentage, setTrafficPercentage] = useState(
        String(variant.trafficPercentage)
    )
    const [configuration, setConfiguration] = useState(
        JSON.stringify(variant.configuration, null, 2)
    )
    const [configurationError, setConfigurationError] = useState('')

    function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
        event.preventDefault()
        setConfigurationError('')

        let parsedConfiguration: Record<string, unknown>

        try {
            const parsed: unknown = JSON.parse(configuration)

            if (
                typeof parsed !== 'object' ||
                parsed === null ||
                Array.isArray(parsed)
            ) {
                setConfigurationError(
                    'Configuration must be a JSON object.'
                )
                return
            }

            parsedConfiguration = parsed as Record<string, unknown>
        } catch {
            setConfigurationError('Please enter valid JSON.')
            return
        }

        updateVariant.mutate(
            {
                experimentId,
                variantId: variant.id,
                name,
                description,
                configuration: parsedConfiguration,
                trafficPercentage: Number(trafficPercentage),
            },
            {
                onSuccess: onCancel,
            }
        )
    }

    return (
        <form onSubmit={handleSubmit} className="mt-4 space-y-4">
            <div>
                <label className="text-sm text-slate-400">Name</label>
                <input
                    required
                    maxLength={100}
                    value={name}
                    onChange={(event) => setName(event.target.value)}
                    className="mt-1 w-full rounded-md border border-slate-700 bg-slate-900 p-2 text-white"
                />
            </div>

            <div>
                <label className="text-sm text-slate-400">
                    Description
                </label>
                <input
                    value={description}
                    onChange={(event) => setDescription(event.target.value)}
                    className="mt-1 w-full rounded-md border border-slate-700 bg-slate-900 p-2 text-white"
                />
            </div>

            <div>
                <label className="text-sm text-slate-400">
                    Traffic allocation (%)
                </label>
                <input
                    required
                    type="number"
                    min="0.01"
                    max="100"
                    step="0.01"
                    value={trafficPercentage}
                    onChange={(event) =>
                        setTrafficPercentage(event.target.value)
                    }
                    className="mt-1 w-full rounded-md border border-slate-700 bg-slate-900 p-2 text-white"
                />
            </div>

            <div>
                <label className="text-sm text-slate-400">
                    Configuration (JSON)
                </label>
                <textarea
                    rows={6}
                    value={configuration}
                    onChange={(event) => setConfiguration(event.target.value)}
                    className="mt-1 w-full rounded-md border border-slate-700 bg-slate-900 p-3 font-mono text-sm text-white"
                />
                {configurationError && (
                    <p className="mt-2 text-sm text-pink-400">
                        {configurationError}
                    </p>
                )}
            </div>

            {updateVariant.isError && (
                <p className="text-sm text-pink-400">
                    Could not update variant. Please try again.
                </p>
            )}

            <div className="flex gap-3">
                <button
                    type="submit"
                    disabled={updateVariant.isPending}
                    className="rounded-md bg-lime-400 px-4 py-2 font-semibold text-slate-950 hover:bg-lime-300 disabled:opacity-50"
                >
                    {updateVariant.isPending ? 'Saving...' : 'Save changes'}
                </button>

                <button
                    type="button"
                    onClick={onCancel}
                    className="rounded-md border border-slate-700 px-4 py-2 text-slate-300 hover:bg-slate-800"
                >
                    Cancel
                </button>
            </div>
        </form>
    )
}
