
import { useState } from 'react'
import type { FormEvent } from 'react'
import { useRunSimulation } from '../hooks/useRunSimulation'

type SimulationControlsProps = {
    experimentId: string
}

export function SimulationControls({
                                       experimentId,
                                   }: SimulationControlsProps) {
    const runSimulation = useRunSimulation()

    const [targetUsers, setTargetUsers] = useState('1000')
    const [validationError, setValidationError] = useState('')

    function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault()
        setValidationError('')

        const parsedTargetUsers = Number(targetUsers)

        if (
            !Number.isInteger(parsedTargetUsers) ||
            parsedTargetUsers < 1 ||
            parsedTargetUsers > 10000
        ) {
            setValidationError(
                'Enter a whole number between 1 and 10,000.'
            )
            return
        }

        runSimulation.mutate({
            experimentId,
            targetUsers: parsedTargetUsers,
        })
    }

    return (
        <section className="mt-8 rounded-xl border border-slate-800 bg-slate-950 p-6">
            {/* Section heading */}
            <div>
                <h3 className="text-lg font-semibold text-white">
                    Run simulation
                </h3>

                <p className="mt-1 text-sm text-slate-400">
                    Generate synthetic users and simulate their interactions
                    with your experiment variants.
                </p>
            </div>

            {/* Simulation form */}
            <form onSubmit={handleSubmit} className="mt-6">
                <div className="flex flex-col gap-4 sm:flex-row sm:items-end">
                    <div className="w-full sm:max-w-xs">
                        <label
                            htmlFor="targetUsers"
                            className="block text-sm font-medium text-slate-300"
                        >
                            Number of synthetic users
                        </label>

                        <input
                            id="targetUsers"
                            type="number"
                            required
                            min="1"
                            max="10000"
                            step="1"
                            value={targetUsers}
                            onChange={(event) => {
                                setTargetUsers(event.target.value)
                                setValidationError('')
                            }}
                            className="mt-2 w-full rounded-lg border border-slate-700 bg-slate-900 px-4 py-3 text-white outline-none transition focus:border-lime-400 focus:ring-1 focus:ring-lime-400"
                        />

                        <p className="mt-2 text-xs text-slate-500">
                            Choose between 1 and 10,000 users.
                        </p>
                    </div>

                    <button
                        type="submit"
                        disabled={runSimulation.isPending}
                        className="rounded-lg bg-lime-400 px-6 py-3 font-semibold text-slate-950 transition hover:bg-lime-300 disabled:cursor-not-allowed disabled:opacity-50 sm:min-w-48"
                    >
                        {runSimulation.isPending
                            ? 'Running simulation...'
                            : 'Run Simulation'}
                    </button>
                </div>

                {validationError && (
                    <p className="mt-3 text-sm text-pink-400">
                        {validationError}
                    </p>
                )}

                {runSimulation.isError && (
                    <p className="mt-3 text-sm text-pink-400">
                        Could not run simulation. Please try again.
                    </p>
                )}
            </form>

            {/* Successful simulation result */}
            {runSimulation.isSuccess && (
                <div className="mt-6 rounded-xl border border-lime-900 bg-slate-900 p-5">
                    <div className="flex flex-wrap items-center justify-between gap-3">
                        <h4 className="font-semibold text-lime-400">
                            Simulation completed!
                        </h4>

                        <span className="rounded-full bg-slate-800 px-3 py-1 text-xs font-medium text-lime-400">
                            {runSimulation.data.status}
                        </span>
                    </div>

                    <div className="mt-5 grid gap-4 sm:grid-cols-2">
                        <div className="rounded-lg bg-slate-950 p-4">
                            <p className="text-sm text-slate-400">
                                Users processed
                            </p>

                            <p className="mt-2 text-2xl font-bold text-white">
                                {runSimulation.data.usersProcessed.toLocaleString()}
                            </p>
                        </div>

                        <div className="rounded-lg bg-slate-950 p-4">
                            <p className="text-sm text-slate-400">
                                Target users
                            </p>

                            <p className="mt-2 text-2xl font-bold text-white">
                                {runSimulation.data.targetUsers.toLocaleString()}
                            </p>
                        </div>
                    </div>

                    <p className="mt-4 text-xs text-slate-500">
                        Completed at:{' '}
                        {runSimulation.data.completedAt
                            ? new Date(
                                runSimulation.data.completedAt
                            ).toLocaleString()
                            : 'Not available'}
                    </p>
                </div>
            )}
        </section>
    )
}
