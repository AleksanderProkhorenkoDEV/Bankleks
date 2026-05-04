import { css } from "lit";

export const donutStyle = css`
        :host { display: block; font-family: var(--font-main); }

        .wrapper {
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 1rem;
        }

        .chart-container {
            width: 220px;
            height: 220px;
        }

        .legend {
            display: flex;
            gap: 1.5rem;
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