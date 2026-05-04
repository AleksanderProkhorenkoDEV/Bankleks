import { css } from "lit";

export const errorStyle = css`
        :host {
            display: flex;
            align-items: center;
            justify-content: center;
            height: 100%;
            background: var(--background-color);
            font-family: var(--font-main);
            padding: 1.5rem;
            box-sizing: border-box;
        }

        .container {
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 1rem;
            text-align: center;
            max-width: 400px;
            width: 100%;
        }

        .icon {
            width: clamp(48px, 10vw, 64px);
            height: clamp(48px, 10vw, 64px);
            border-radius: 50%;
            background: color-mix(in srgb, var(--color-danger) 12%, var(--background-color));
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: clamp(1.5rem, 5vw, 2rem);
        }

        .title {
            font-size: clamp(1rem, 3vw, 1.25rem);
            font-weight: 700;
            color: var(--text-primary);
            margin: 0;
        }

        .message {
            font-size: clamp(0.8rem, 2.5vw, 0.875rem);
            color: var(--color-secondary);
            margin: 0;
            line-height: 1.5;
        }

        .actions {
            display: flex;
            gap: 0.75rem;
            flex-wrap: wrap;
            justify-content: center;
            margin-top: 0.5rem;
        }
    `;