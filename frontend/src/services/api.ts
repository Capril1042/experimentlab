import type { DemoSessionResponse } from '../features/experiments/types/demoSession'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL

export async function get<T>(endpoint: string): Promise<T> {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        credentials: 'include',
    })

    if (!response.ok) {
        throw new Error(`API request failed: ${response.status}`)
    }

    return response.json() as Promise<T>
}

export async function initializeDemoSession(): Promise<DemoSessionResponse> {
    return get<DemoSessionResponse>('/api/demo/session')
}

export async function post<TRequest, TResponse>(
    endpoint: string,
    body: TRequest
): Promise<TResponse> {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        method: 'POST',
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(body),
    })

    if (!response.ok) {
        throw new Error(`API request failed: ${response.status}`)
    }

    return response.json() as Promise<TResponse>
}

export async function put<TRequest, TResponse>(
    endpoint: string,
    body: TRequest
): Promise<TResponse> {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        method: 'PUT',
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(body),
    })

    if (!response.ok) {
        throw new Error(`API request failed: ${response.status}`)
    }

    return response.json() as Promise<TResponse>
}

export async function del(endpoint: string): Promise<void> {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        method: 'DELETE',
        credentials: 'include',
    })

    if (!response.ok) {
        throw new Error(`API request failed: ${response.status}`)
    }
}
