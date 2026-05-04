import { css } from "lit";

export const resumePage = css`
        :host {
        display: block;
        height: 100%;
        overflow-y: auto;
        background: var(--background-color);
        font-family: var(--font-main);
    }

    .page {
        max-width: 900px;
        margin: 0 auto;
        padding: 2.5rem 1.5rem;
        display: flex;
        flex-direction: column;
        gap: 2rem;
        box-sizing: border-box;
    }

    h1 {
        font-size: clamp(1.1rem, 4vw, 1.4rem);
        font-weight: 700;
        color: var(--text-primary);
        margin: 0;
    }

    .charts-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 1.25rem;
        min-width: 0;
    }

    .chart-card {
        background: white;
        border-radius: 16px;
        padding: 1.5rem;
        border: 1px solid rgba(19, 78, 74, 0.15);
        display: flex;
        flex-direction: column;
        gap: 1.25rem;
        min-width: 0;      /* clave para evitar desbordamiento en grid */
        overflow: hidden;
    }

    .chart-title {
        font-size: 0.75rem;
        font-weight: 700;
        text-transform: uppercase;
        letter-spacing: 0.08em;
        color: var(--color-secondary);
        margin: 0;
    }

    .loading {
        display: flex;
        align-items: center;
        justify-content: center;
        height: 200px;
        color: var(--color-secondary);
        font-size: 0.875rem;
    }

    .error {
        color: var(--color-danger);
        font-size: 0.875rem;
        text-align: center;
        padding: 2rem;
    }

    @media (max-width: 600px) {
        .page {
            padding: 1.5rem 1rem;
        }

        .charts-grid {
            grid-template-columns: 1fr;
        }
    }
`;