import { css } from "lit";

export const accountStyles = css`
    :host {
        display: block;
        overflow-y: auto;
        height: 100%;
        background-color: var(--background-color);
        font-family: var(--font-main);
        color: var(--text-primary);
        box-sizing: border-box;
    }

    .account {
        max-width: 900px;
        margin: 0 auto;
        padding: 2.5rem 1.5rem;
        display: flex;
        flex-direction: column;
        gap: 2rem;
        box-sizing: border-box;
    }

    /* HEADER */
    .header h1 {
        font-size: clamp(1.2rem, 4vw, 1.6rem);
        font-weight: 700;
        margin: 0 0 0.25rem;
        color: var(--text-primary);
    }

    .header p {
        font-size: clamp(0.8rem, 2.5vw, 0.875rem);
        color: var(--color-secondary);
        margin: 0;
    }

    /* GRID SUPERIOR */
    .grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 1.25rem;
        min-width: 0;
    }

    /* TARJETA BALANCE */
    .balance-card {
        background: #fff;
        border-radius: 16px;
        padding: 1.5rem;
        border: 1px solid rgba(19, 78, 74, 0.15);
        display: flex;
        flex-direction: column;
        gap: 0.75rem;
        min-width: 0;
    }

    .balance-card-header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        flex-wrap: wrap;
        gap: 0.5rem;
    }

    .balance-label {
        font-size: clamp(0.6rem, 1.5vw, 0.7rem);
        font-weight: 700;
        letter-spacing: 0.08em;
        color: var(--color-secondary);
        text-transform: uppercase;
        display: flex;
        align-items: center;
        gap: 0.4rem;
    }

    .balance-icon {
        width: 36px;
        height: 36px;
        border-radius: 50%;
        background: var(--background-color);
        display: flex;
        align-items: center;
        justify-content: center;
        color: var(--color-tertiary);
        font-size: 1rem;
        flex-shrink: 0;
    }

    .balance-amount {
        font-size: clamp(1.5rem, 5vw, 2.2rem);
        font-weight: 800;
        color: var(--text-primary);
        margin: 0;
        line-height: 1;
        word-break: break-all;
    }

    .balance-divider {
        border: none;
        border-top: 1px solid rgba(19, 78, 74, 0.1);
        margin: 0;
    }

    .balance-footer {
        display: flex;
        align-items: center;
        gap: 0.4rem;
        font-size: clamp(0.7rem, 2vw, 0.78rem);
        color: var(--color-secondary);
        flex-wrap: wrap;
    }

    .dot {
        width: 8px;
        height: 8px;
        border-radius: 50%;
        background: var(--color-tertiary);
        flex-shrink: 0;
    }

    /* TARJETA BANCARIA */
    .bank-card {
        background: var(--color-secondary);
        border-radius: 16px;
        padding: 1.5rem;
        color: var(--text-foreground);
        display: flex;
        flex-direction: column;
        gap: 1rem;
        position: relative;
        overflow: hidden;
        min-width: 0;
    }

    .bank-card::before {
        content: '';
        position: absolute;
        width: 180px;
        height: 180px;
        border-radius: 50%;
        background: rgba(255,255,255,0.05);
        top: -60px;
        right: -40px;
    }

    .bank-card-header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        flex-wrap: wrap;
        gap: 0.5rem;
    }

    .bank-card-title {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        font-size: clamp(0.65rem, 1.5vw, 0.75rem);
        font-weight: 700;
        letter-spacing: 0.08em;
        text-transform: uppercase;
        opacity: 0.9;
    }

    .show-btn {
        background: rgba(255,255,255,0.12);
        border: 1px solid rgba(255,255,255,0.2);
        border-radius: 20px;
        color: var(--text-foreground);
        font-size: 0.75rem;
        padding: 0.3rem 0.75rem;
        cursor: pointer;
        display: flex;
        align-items: center;
        gap: 0.35rem;
        font-family: var(--font-main);
        white-space: nowrap;
    }

    .card-number-label {
        font-size: 0.65rem;
        letter-spacing: 0.08em;
        text-transform: uppercase;
        opacity: 0.6;
        margin: 0;
    }

    .card-number {
        font-size: clamp(0.85rem, 2.5vw, 1.1rem);
        font-weight: 600;
        letter-spacing: 0.15em;
        margin: 0.2rem 0 0;
        word-break: break-all;
    }

    .bank-card-footer {
        display: flex;
        justify-content: space-between;
        align-items: flex-end;
        flex-wrap: wrap;
        gap: 0.5rem;
    }

    .card-meta-label {
        font-size: 0.6rem;
        text-transform: uppercase;
        letter-spacing: 0.08em;
        opacity: 0.6;
        margin: 0 0 0.2rem;
    }

    .card-meta-value {
        font-size: clamp(0.75rem, 2vw, 0.85rem);
        font-weight: 700;
        margin: 0;
        text-transform: uppercase;
    }

    /* TARJETA INFO */
    .info-card {
        background: #fff;
        border-radius: 16px;
        padding: 1.5rem;
        border: 1px solid rgba(19, 78, 74, 0.15);
        max-width: 520px;
        margin: 0 auto;
        width: 100%;
        box-sizing: border-box;
    }

    .info-card-title {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        font-size: clamp(0.875rem, 3vw, 1rem);
        font-weight: 700;
        color: var(--text-primary);
        margin: 0 0 1.25rem;
    }

    .info-row {
        background: var(--background-color);
        border-radius: 10px;
        padding: 0.75rem 1rem;
        display: flex;
        align-items: center;
        gap: 0.75rem;
        margin-bottom: 0.75rem;
    }

    .info-row-icon {
        width: 32px;
        height: 32px;
        border-radius: 50%;
        background: rgba(15, 118, 110, 0.1);
        display: flex;
        align-items: center;
        justify-content: center;
        color: var(--color-tertiary);
        flex-shrink: 0;
    }

    .info-row-label {
        font-size: 0.65rem;
        text-transform: uppercase;
        letter-spacing: 0.06em;
        color: var(--color-secondary);
        margin: 0 0 0.1rem;
        font-weight: 600;
    }

    .info-row-value {
        font-size: clamp(0.8rem, 2.5vw, 0.875rem);
        font-weight: 600;
        color: var(--text-primary);
        margin: 0;
        word-break: break-all;
    }

    .account-type-badge {
        background: var(--background-color);
        border-radius: 10px;
        padding: 0.75rem 1rem;
        text-align: center;
        font-size: clamp(0.8rem, 2.5vw, 0.875rem);
        font-weight: 600;
        color: var(--color-tertiary);
        margin-top: 0.25rem;
    }

    /* RESPONSIVE */
    @media (max-width: 600px) {
        .account {
            padding: 1.5rem 1rem;
            gap: 1.25rem;
        }

        .grid {
            grid-template-columns: 1fr;
        }

        .info-card {
            max-width: 100%;
        }
    }

    @media (max-width: 360px) {
        .balance-amount {
            font-size: 1.3rem;
        }

        .card-number {
            font-size: 0.8rem;
            letter-spacing: 0.08em;
        }
    }
`