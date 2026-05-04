import { css } from "lit";

export const loadingStyles = css`
        :host {
            display: flex;
            align-items: center;
            justify-content: center;
            height: 100%;
            background: var(--background-color);
            font-family: var(--font-main);
        }

        .container {
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 1.5rem;
        }

        .spinner {
            width: 48px;
            height: 48px;
            border: 4px solid rgba(15, 118, 110, 0.15);
            border-top-color: var(--color-tertiary);
            border-radius: 50%;
            animation: spin 0.8s linear infinite;
        }

        .text {
            font-size: 0.875rem;
            font-weight: 600;
            color: var(--color-secondary);
            letter-spacing: 0.05em;
        }

        @keyframes spin {
            to { transform: rotate(360deg); }
        }

        @media (max-width: 480px) {
            .spinner {
                width: 36px;
                height: 36px;
            }
        }
    `;