import type { AccountResponse } from "../types/account";
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