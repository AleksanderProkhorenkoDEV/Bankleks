import type { UserSummary } from "./user";

export interface AccountResponse {
    balance: number,
    accountNumber: string,
    userSummaryDTO: UserSummary
}