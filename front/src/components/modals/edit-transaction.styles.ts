import { css } from "lit";

export const modalStyles = css`
    .overlay {
        position: fixed;
        inset: 0;
        background: rgba(0, 0, 0, 0.4);
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 100;
        backdrop-filter: blur(2px);
    }

    .modal {
        background: white;
        border-radius: 16px;
        padding: 2rem;
        width: 100%;
        max-width: 420px;
        display: flex;
        flex-direction: column;
        gap: 1.25rem;
        font-family: var(--font-main);
        box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
    }

    .modal-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .modal-header h2 {
        font-size: 1rem;
        font-weight: 700;
        color: var(--text-primary);
        margin: 0;
    }

    .close-btn {
        background: none;
        border: none;
        font-size: 1.2rem;
        cursor: pointer;
        color: var(--color-secondary);
        padding: 0.25rem;
        line-height: 1;
        transition: opacity 0.15s;
    }

    .close-btn:hover {
        opacity: 0.6;
    }

    .modal-footer {
        display: flex;
        justify-content: flex-end;
        gap: 0.75rem;
        margin-top: 0.5rem;
    }
`;