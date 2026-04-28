import { authStore } from "../store/auth";

const BASE_URL = import.meta.env.VITE_API_URL;


export const request = async <T>(endpoint: string, options: RequestInit = {}): Promise<T> => {
    const { jwt } = authStore.getState();

    const response = await fetch(`${BASE_URL}${endpoint}`, {
        ...options,
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json',
            ...(jwt ? { 'Authorization': `Bearer ${jwt}` } : {}),
            ...options.headers
        }
    });

    if (!response.ok) throw await response.json();
    return response.json();
}