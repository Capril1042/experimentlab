
import { useDemoSession } from './features/experiments/hooks/useDemoSession'
import { useExperiments } from './features/experiments/hooks/useExperiments'
import { ExperimentList } from './features/experiments/components/ExperimentList'
import { CreateExperimentForm } from './features/experiments/components/CreateExperimentForm'

function App() {
    const {
        data: session,
        isLoading: sessionLoading,
        isError: sessionError,
    } = useDemoSession()

    const {
        data: experiments = [],
        isLoading: experimentsLoading,
        isError: experimentsError,
    } = useExperiments()

    return (
        <main className="min-h-screen bg-slate-950 text-white p-8">
            <h1 className="text-4xl font-bold text-lime-400">
                ExperimentLab
            </h1>

            <div className="mt-6 rounded-lg bg-slate-900 p-4">
                <h2 className="text-lg font-semibold">
                    Demo Session
                </h2>

                {sessionLoading && (
                    <p className="text-slate-400">
                        Initializing demo session...
                    </p>
                )}

                {sessionError && (
                    <p className="text-pink-400">
                        Could not initialize demo session.
                    </p>
                )}

                {session && (
                    <p className="text-lime-400">
                        Demo session active: {session.active ? 'Yes' : 'No'}
                    </p>
                )}
            </div>

            <div className="mt-8">
                <CreateExperimentForm />
            </div>

            <section className="mt-8">
                <h2 className="text-2xl font-bold">
                    Your Experiments
                </h2>

                <ExperimentList
                    experiments={experiments}
                    isLoading={experimentsLoading}
                    isError={experimentsError}
                />
            </section>
        </main>
    )
}

export default App
