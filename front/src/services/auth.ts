import type { RegisterResponse, ServiceResponse, SignInBody, SignInResponse } from "../types/auth";
import type { RegisterBody } from "../types";
import { authStore } from "../store/auth";
import { request } from "./http";

let _refreshTimer: number | null = null;

const stopRefreshTimer = () => {
    if (_refreshTimer) {
        clearInterval(_refreshTimer);
        _refreshTimer = null;
    }
}

const startRefreshTimer = () => {
    stopRefreshTimer();
    _refreshTimer = window.setInterval(async () => {
        await refresh();
    }, 14 * 60 * 1000);
}


export const refresh = async (): Promise<void> => {
    try {
        const data = await request<SignInResponse>('/auth/refresh', { method: 'POST', body: JSON.stringify({}) });
        authStore.setState({ jwt: data.token, user: { name: data.userName, role: data.rol } });
    } catch {
        authStore.setState({ jwt: null, user: null });
        stopRefreshTimer();
        window.dispatchEvent(new CustomEvent('session-expired'));
    }
}

export const initialize = async (): Promise<void> => {
    try {
        const data = await request<SignInResponse>('/auth/refresh', { method: 'POST', body: JSON.stringify({}) });
        authStore.setState({ jwt: data.token, user: { name: data.userName, role: data.rol } });
        startRefreshTimer();
    } catch {
        authStore.setState({ jwt: null, user: null });
    }
}

export const signIn = async (body: SignInBody): Promise<ServiceResponse> => {
    try {
        const data = await request<SignInResponse>('/auth/login', { method: 'POST', body: JSON.stringify(body) });
        console.log('DATA DE LA REQUEST', data);
        authStore.setState({ jwt: data.token, user: { name: data.userName, role: data.rol } });
        startRefreshTimer();
        return { ok: true };
    } catch (error) {
        const message = error instanceof Error ? error.message : 'Error desconocido';
        return { ok: false, error: message };
    }
}

export const register = async (body: RegisterBody): Promise<ServiceResponse> => {
    try {
        await request<RegisterResponse>('/auth/register', { method: 'POST', body: JSON.stringify(body) });
        return { ok: true };
    } catch (error) {
        const message = error instanceof Error ? error.message : 'Error desconocido';
        return { ok: false, error: message };
    }
}



