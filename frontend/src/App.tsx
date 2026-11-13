import { Routes, Route } from 'react-router-dom'
import { useDemoSession } from './features/experiments/hooks/useDemoSession'
import { useExperiments } from './features/experiments/hooks/useExperiments'
import { ExperimentList } from './features/experiments/components/ExperimentList'
import { CreateExperimentForm } from './features/experiments/components/CreateExperimentForm'
import { ExperimentDetailsPage } from './features/experiments/pages/ExperimentDetailsPage'

function DashboardPage() {
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
        <main className="min-h-screen bg-slate-950 p-8 text-white">
            <div className="flex flex-wrap items-center justify-between gap-4">
                <h1 className="text-4xl font-bold text-lime-400">
                    ExperimentLab
                </h1>

                <div className="flex items-center gap-2 rounded-full bg-slate-900 px-4 py-2">
        <span
            className={`h-2 w-2 rounded-full ${
                sessionLoading
                    ? 'bg-slate-400'
                    : sessionError || !session?.active
                        ? 'bg-pink-400'
                        : 'bg-lime-400'
            }`}
        />

                    <span className="text-sm text-slate-300">
            {sessionLoading
                ? 'Initializing session'
                : sessionError
                    ? 'Session unavailable'
                    : session?.active
                        ? 'Demo session active'
                        : 'Demo session inactive'}
        </span>
                </div>
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

function App() {
    return (
        <Routes>
            <Route path="/" element={<DashboardPage />} />
            <Route path="/experiments/:id" element={<ExperimentDetailsPage />} />
        </Routes>
    )
}

export default App
