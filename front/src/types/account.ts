import type { UserSummary } from "./user";

export interface AccountResponse {
    balance: number,
    accountNumber: string,
    userSummaryDTO: UserSummary
}

export interface BalancePoint {
    date: string;
    balance: number;
}

export interface AccountStats {
    totalIncome: number;
    totalExpense: number;
    balancePointDTO: BalancePoint[];
}

export interface AccountFormData {
    timezone: TimezoneType
}

export type TimezoneType = 'UTC' |
    'Europe/Madrid' |
    'Europe/London' |
    'America/New_York' |
    'America/Los_Angeles' |
    'Asia/Tokyo';