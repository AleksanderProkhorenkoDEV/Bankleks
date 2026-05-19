import { css } from "lit";

export const panelStyles = css`
      :host {
        display: block;
        height: 100%;
        overflow-y: auto;
        background: var(--background-color);
        font-family: var(--font-main);
    }

    .page {
        max-width: 1100px;
        margin: 0 auto;
        padding: 2.5rem 1.5rem;
    }

    h1 {
        font-size: 1.4rem;
        font-weight: 700;
        color: var(--text-primary);
        margin: 0 0 2rem;
    }

    .section-header {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        margin: 2.5rem 0 1rem;
    }

    .section-header h2 {
        font-size: 1.1rem;
        font-weight: 700;
        color: var(--text-primary);
        margin: 0;
    }

    .badge-failed {
        background: var(--color-danger, #ef4444);
        color: #fff;
        font-size: 0.7rem;
        font-weight: 700;
        padding: 0.2rem 0.55rem;
        border-radius: 999px;
    }

    .empty-state {
        color: var(--text-secondary);
        font-size: 0.9rem;
        padding: 1.5rem 0;
        margin: 0;
    }

    .retry-btn {
        display: inline-flex;
        align-items: center;
        gap: 0.35rem;
        padding: 0.35rem 0.75rem;
        font-size: 0.78rem;
        font-weight: 600;
        font-family: var(--font-main);
        color: var(--color-danger, #ef4444);
        background: transparent;
        border: 1.5px solid var(--color-danger, #ef4444);
        border-radius: 6px;
        cursor: pointer;
        transition: background 0.15s, color 0.15s;
        white-space: nowrap;
    }

    .retry-btn:hover:not(:disabled) {
        background: var(--color-danger, #ef4444);
        color: #fff;
    }

    .retry-btn:disabled {
        opacity: 0.45;
        cursor: not-allowed;
    }

    .retry-btn.loading {
        opacity: 0.7;
    }
`;