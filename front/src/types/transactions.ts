export interface TransactionFormData {
    concept: string;
    amount: string;
    originIban: string;
    destinationIban: string;
    type: TransactionType;
}


export interface TransactionBody {
    concept: string;
    amount: number;
    transactionType: TransactionType;
    originIban?: string;
    destinationIban?: string;
}

export interface TransactionResponse {
    id: number;
    concept: string;
    transactionType: string;
    amount: number;
    transactionDate: string; 
}

export interface PageResponse<T> {
    content: T[];
    currentPage: number;
    totalPages: number;
    totalElements: number;
}

export type TransactionType = 'TRANSFER' | 'DEPOSIT' | 'WITHDRAWAL';
