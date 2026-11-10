
import { useState } from 'react'
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

    function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
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
        <section className="mt-8 rounded-xl border border-slate-800 bg-slate-950 p-5">
            <h3 className="text-lg font-semibold text-white">
                Run simulation
            </h3>

            <p className="mt-1 text-sm text-slate-400">
                Generate synthetic users and simulate their interactions
                with your experiment variants.
            </p>

            <form onSubmit={handleSubmit} className="mt-5">
                <div>
                    <label
                        htmlFor="targetUsers"
                        className="text-sm text-slate-400"
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
                        className="mt-2 w-full max-w-xs rounded-md border border-slate-700 bg-slate-900 p-3 text-white"
                    />

                    <p className="mt-2 text-xs text-slate-500">
                        Choose between 1 and 10,000 users.
                    </p>
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

                <button
                    type="submit"
                    disabled={runSimulation.isPending}
                    className="mt-5 rounded-md bg-lime-400 px-5 py-3 font-semibold text-slate-950 hover:bg-lime-300 disabled:cursor-not-allowed disabled:opacity-50"
                >
                    {runSimulation.isPending
                        ? 'Running simulation...'
                        : 'Run Simulation'}
                </button>
            </form>

            {runSimulation.isSuccess && (
                <div className="mt-6 rounded-lg border border-lime-900 bg-slate-900 p-4">
                    <h4 className="font-semibold text-lime-400">
                        Simulation completed!
                    </h4>

                    <div className="mt-4 grid gap-4 sm:grid-cols-2">
                        <div>
                            <p className="text-sm text-slate-400">
                                Users processed
                            </p>
                            <p className="mt-1 text-2xl font-bold text-white">
                                {runSimulation.data.usersProcessed.toLocaleString()}
                            </p>
                        </div>

                        <div>
                            <p className="text-sm text-slate-400">
                                Simulation status
                            </p>
                            <p className="mt-1 text-lg font-semibold text-lime-400">
                                {runSimulation.data.status}
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
