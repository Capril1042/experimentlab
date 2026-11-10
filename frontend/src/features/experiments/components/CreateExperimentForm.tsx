
import { useState } from 'react'
import { useCreateExperiment } from '../hooks/useCreateExperiment'
import type { CreateExperimentRequest } from '../types/createExperiment'

const initialForm: CreateExperimentRequest = {
    name: '',
    description: '',
    hypothesis: '',
    primaryMetric: 'CONVERSION_RATE',
}

export function CreateExperimentForm() {
    const [form, setForm] = useState<CreateExperimentRequest>(initialForm)
    const createExperiment = useCreateExperiment()

    function handleChange(
        event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
    ) {
        const { name, value } = event.target

        setForm((current) => ({
            ...current,
            [name]: value,
        }))
    }

    function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
        event.preventDefault()

        createExperiment.mutate(form, {
            onSuccess: () => {
                setForm(initialForm)
            },
        })
    }

    return (
        <section className="rounded-xl border border-slate-800 bg-slate-900 p-6">
            <h2 className="text-xl font-semibold text-white">
                Create an experiment
            </h2>

            <p className="mt-1 text-sm text-slate-400">
                Define what you want to test and how you'll measure success.
            </p>

            <form onSubmit={handleSubmit} className="mt-6 space-y-4">
                <div>
                    <label htmlFor="name" className="mb-1 block text-sm text-slate-300">
                        Experiment name
                    </label>
                    <input
                        id="name"
                        name="name"
                        value={form.name}
                        onChange={handleChange}
                        required
                        maxLength={150}
                        className="w-full rounded-lg border border-slate-700 bg-slate-950 px-3 py-2 text-white outline-none focus:border-lime-400"
                        placeholder="e.g. Homepage headline test"
                    />
                </div>

                <div>
                    <label htmlFor="description" className="mb-1 block text-sm text-slate-300">
                        Description
                    </label>
                    <textarea
                        id="description"
                        name="description"
                        value={form.description}
                        onChange={handleChange}
                        required
                        rows={3}
                        className="w-full rounded-lg border border-slate-700 bg-slate-950 px-3 py-2 text-white outline-none focus:border-lime-400"
                        placeholder="What are you testing?"
                    />
                </div>

                <div>
                    <label htmlFor="hypothesis" className="mb-1 block text-sm text-slate-300">
                        Hypothesis
                    </label>
                    <textarea
                        id="hypothesis"
                        name="hypothesis"
                        value={form.hypothesis}
                        onChange={handleChange}
                        required
                        rows={3}
                        className="w-full rounded-lg border border-slate-700 bg-slate-950 px-3 py-2 text-white outline-none focus:border-lime-400"
                        placeholder="We believe that changing X will improve Y because..."
                    />
                </div>

                <div>
                    <label htmlFor="primaryMetric" className="mb-1 block text-sm text-slate-300">
                        Primary metric
                    </label>
                    <select
                        id="primaryMetric"
                        name="primaryMetric"
                        value={form.primaryMetric}
                        onChange={handleChange}
                        className="w-full rounded-lg border border-slate-700 bg-slate-950 px-3 py-2 text-white outline-none focus:border-lime-400"
                    >
                        <option value="CONVERSION_RATE">Conversion rate</option>
                        <option value="CLICK_THROUGH_RATE">Click-through rate</option>
                        <option value="CLICKS">Clicks</option>
                        <option value="CONVERSIONS">Conversions</option>
                    </select>
                </div>

                {createExperiment.isError && (
                    <p role="alert" className="text-sm text-pink-400">
                        Could not create experiment. Please try again.
                    </p>
                )}

                {createExperiment.isSuccess && (
                    <p role="status" className="text-sm text-lime-400">
                        Experiment created successfully!
                    </p>
                )}

                <button
                    type="submit"
                    disabled={createExperiment.isPending}
                    className="rounded-lg bg-lime-400 px-5 py-2.5 font-semibold text-slate-950 transition hover:bg-lime-300 disabled:cursor-not-allowed disabled:opacity-50"
                >
                    {createExperiment.isPending ? 'Creating...' : 'Create experiment'}
                </button>
            </form>
        </section>
    )
}
