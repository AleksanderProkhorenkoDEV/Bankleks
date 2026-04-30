import type { ServiceResponse } from "../types/auth";
import type { PageResponse, TransactionBody, TransactionResponse } from "../types/transactions";
import { request } from "./http";

interface GlobalResponse {
    message: string;
    status: number;
}

export const createTransaction = async (body: TransactionBody): Promise<ServiceResponse> => {
    try {
        await request<GlobalResponse>('/transaction/create', {
            method: 'POST',
            body: JSON.stringify(body)
        });
        return { ok: true };
    } catch (error) {
        const message = error instanceof Error ? error.message : 'Error al realizar la transacción';
        return { ok: false, error: message };
    }
}


export const getTransactions = async (page: number = 0, size: number = 25): Promise<ServiceResponse<PageResponse<TransactionResponse>>> => {
    try {
        const data = await request<PageResponse<TransactionResponse>>(
            `/transaction?page=${page}&size=${size}`,
            { method: 'GET' }
        );
        return { ok: true, data };
    } catch (error) {
        const message = error instanceof Error ? error.message : 'Error desconocido';
        return { ok: false, error: message };
    }
}