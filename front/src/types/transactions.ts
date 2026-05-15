export interface TransactionFormData {
    concept: string;
    amount: string;
    originIban: string;
    destinationIban: string;
    type: TransactionType;
    isScheduled: boolean;
    scheduledDates: string[];
    scheduledTime: string;
    targetTimezone: string;
    scheduledMode: ScheduledMode;
    recurrence: RecurrenceType | null;
    recurrenceEndDate: string;
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

export interface ScheduledTransactionBody {
    concept: string;
    amount: number;
    originIban: string;
    destinationIban: string;
    targetTimezone: string;
    scheduledTime: string;
    scheduledDates: string[];
    recurrence?: RecurrenceType;
    recurrenceEndDate?: string;
}

export type ScheduledMode = 'dates' | 'recurrent';
export type RecurrenceType = 'BEGINNING_OF_MONTH' | 'MIDDLE_OF_MONTH' | 'END_OF_MONTH';