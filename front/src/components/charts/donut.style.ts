import { css } from "lit";

export const donutStyle = css`
    :host { 
        display: block; 
        font-family: var(--font-main);
        width: 100%;
    }

    .wrapper {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 1rem;
        width: 100%;
    }

    .chart-container {
        width: 100%;
        aspect-ratio: 1;
        max-width: 220px;
    }

    .legend {
        display: flex;
        gap: 1.5rem;
        flex-wrap: wrap;
        justify-content: center;
    }

    .legend-item {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        font-size: 0.8rem;
        color: var(--text-primary);
    }

    .legend-dot {
        width: 10px;
        height: 10px;
        border-radius: 50%;
        flex-shrink: 0;
    }

    .legend-value {
        font-weight: 700;
        font-size: 0.75rem;
    }
`;