import type { PageResponse } from '../types/transactions';
import type { ServiceResponse } from '../types/auth';
import type { UserResponse } from '../types/user';
import { request } from './http';

export const getUsers = async (page: number = 0, size: number = 25): Promise<ServiceResponse<PageResponse<UserResponse>>> => {
    try {
        const data = await request<PageResponse<UserResponse>>(
            `/admin/users?page=${page}&size=${size}`,
            { method: 'GET' }
        );
        return { ok: true, data };
    } catch (error) {
        const message = error instanceof Error ? error.message : 'Error desconocido';
        return { ok: false, error: message };
    }
}

export const deleteUser = async (id: number): Promise<ServiceResponse> => {
    try {
        await request(`/admin/users/${id}`, { method: 'DELETE' });
        return { ok: true };
    } catch (error) {
        const message = error instanceof Error ? error.message : 'Error desconocido';
        return { ok: false, error: message };
    }
}