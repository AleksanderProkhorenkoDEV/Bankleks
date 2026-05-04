import { css } from "lit";

export const notFoundStyles = css`
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
            gap: 1rem;
            text-align: center;
            padding: 2rem;
        }

        .code {
            font-size: 6rem;
            font-weight: 800;
            color: var(--color-tertiary);
            line-height: 1;
            margin: 0;
        }

        .title {
            font-size: 1.25rem;
            font-weight: 700;
            color: var(--text-primary);
            margin: 0;
        }

        .description {
            font-size: 0.875rem;
            color: var(--color-secondary);
            margin: 0;
        }
    `;