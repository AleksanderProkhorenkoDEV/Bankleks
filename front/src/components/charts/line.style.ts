import { css } from "lit";

export const lineStyle = css`
    :host { 
        display: block; 
        font-family: var(--font-main);
        width: 100%;
    }

    .chart-container {
        width: 100%;
        height: 220px;
        min-width: 0;
    }

    .empty {
        height: 220px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 0.875rem;
        color: var(--color-secondary);
    }

    @media (max-width: 480px) {
        .chart-container {
            height: 180px;
        }
    }
`;