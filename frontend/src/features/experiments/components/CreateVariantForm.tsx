
import { useState } from 'react'
import { useCreateVariant } from '../hooks/useCreateVariant'

type CreateVariantFormProps = {
    experimentId: string
}

export function CreateVariantForm({
                                      experimentId,
                                  }: CreateVariantFormProps) {
    const createVariant = useCreateVariant()

    const [name, setName] = useState('')
    const [description, setDescription] = useState('')
    const [trafficPercentage, setTrafficPercentage] = useState('50')
    const [configuration, setConfiguration] = useState(
        '{\n  "buttonText": "Get Started",\n  "backgroundColor": "#22c55e"\n}'
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

        createVariant.mutate(
            {
                experimentId,
                name,
                description,
                configuration: parsedConfiguration,
                trafficPercentage: Number(trafficPercentage),
            },
            {
                onSuccess: () => {
                    setName('')
                    setDescription('')
                    setTrafficPercentage('50')
                },
            }
        )
    }

    return (
        <form
            onSubmit={handleSubmit}
            className="mt-6 rounded-lg border border-slate-800 bg-slate-950 p-5"
        >
            <h3 className="font-semibold text-white">
                Create a variant
            </h3>

            <div className="mt-4 grid gap-4 sm:grid-cols-2">
                <div>
                    <label className="text-sm text-slate-400">
                        Variant name
                    </label>
                    <input
                        required
                        maxLength={100}
                        value={name}
                        onChange={(event) => setName(event.target.value)}
                        placeholder="e.g. Green Button"
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
            </div>

            <div className="mt-4">
                <label className="text-sm text-slate-400">
                    Description
                </label>
                <input
                    value={description}
                    onChange={(event) =>
                        setDescription(event.target.value)
                    }
                    placeholder="Describe this variation"
                    className="mt-1 w-full rounded-md border border-slate-700 bg-slate-900 p-2 text-white"
                />
            </div>

            <div className="mt-4">
                <label className="text-sm text-slate-400">
                    Configuration (JSON)
                </label>
                <textarea
                    rows={5}
                    value={configuration}
                    onChange={(event) =>
                        setConfiguration(event.target.value)
                    }
                    className="mt-1 w-full rounded-md border border-slate-700 bg-slate-900 p-3 font-mono text-sm text-white"
                />
                {configurationError && (
                    <p className="mt-2 text-sm text-pink-400">
                        {configurationError}
                    </p>
                )}
            </div>

            {createVariant.isError && (
                <p className="mt-3 text-sm text-pink-400">
                    Could not create variant. Please try again.
                </p>
            )}

            {createVariant.isSuccess && (
                <p className="mt-3 text-sm text-lime-400">
                    Variant created successfully!
                </p>
            )}

            <button
                type="submit"
                disabled={createVariant.isPending}
                className="mt-5 rounded-md bg-lime-400 px-4 py-2 font-semibold text-slate-950 hover:bg-lime-300 disabled:opacity-50"
            >
                {createVariant.isPending ? 'Creating...' : 'Create variant'}
            </button>
        </form>
    )
}
