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

export type TransactionType = 'TRANSFER' | 'DEPOSIT' | 'WITHDRAWAL';
