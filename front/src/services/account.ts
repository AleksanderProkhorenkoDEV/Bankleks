import type { AccountFormData, AccountResponse, AccountStats, TimezoneType } from "../types/account";
import type { ServiceResponse } from "../types/auth";
import { authStore } from "../store/auth"
import { request } from "./http";

export const getAccount = async (): Promise<ServiceResponse<AccountResponse>> => {
    const { user } = authStore.getState();
    try {
        const data = await request<AccountResponse>(`/accounts/${user?.id}/all-data`, { method: 'GET' })
        return { ok: true, data };
    } catch (error) {
        const message = error instanceof Error ? error.message : 'Error desconocido';
        return { ok: false, error: message }
    }

}


export const getAccountStats = async (): Promise<ServiceResponse<AccountStats>> => {
    try {
        const data = await request<AccountStats>('/accounts/stats', { method: 'GET' });
        return { ok: true, data };
    } catch (error) {
        const message = error instanceof Error ? error.message : 'Error desconocido';
        return { ok: false, error: message };
    }
}

export const getTimezone = async (): Promise<ServiceResponse<TimezoneType>> => {
    const { user } = authStore.getState();
    try {
        const data = await request<AccountFormData>(`/accounts/${user?.id}/timezone`, { method: 'GET' });
        return { ok: true, data: data.timezone };
    } catch (error) {
        const message = error instanceof Error ? error.message : 'Error desconocido';
        return { ok: false, error: message };
    }
}

export const updateTimezone = async (timezone: TimezoneType): Promise<ServiceResponse<void>> => {
    const { user } = authStore.getState();
    try {
        await request<AccountFormData>(`/accounts/${user?.id}/timezone`, {
            method: 'PATCH',
            body: JSON.stringify({ timezone })
        });
        return { ok: true };
    } catch (error) {
        const message = error instanceof Error ? error.message : 'Error desconocido';
        return { ok: false, error: message };
    }
}
