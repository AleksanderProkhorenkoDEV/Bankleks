import type { ServiceResponse } from "../types/auth";
import type { TransactionBody } from "../types/transactions";
import { request } from "./http";

interface TransactionResponse {
    message: string;
    status: number;
}

export const createTransaction = async (body: TransactionBody): Promise<ServiceResponse> => {
    try {
        await request<TransactionResponse>('/transaction/create', {
            method: 'POST',
            body: JSON.stringify(body)
        });
        return { ok: true };
    } catch (error) {
        const message = error instanceof Error ? error.message : 'Error al realizar la transacción';
        return { ok: false, error: message };
    }
}