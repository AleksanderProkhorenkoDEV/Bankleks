import { css } from "lit";

export const baseStyles = css`
    :host {
        display: block;
        width: 65%;
        max-height: 90vh;
        overflow-y: auto;
    }

    form {
        display: flex;
        flex-direction: column;
        gap: 2rem;
        padding: 2rem;
        border-radius: 8px;
        color: var(--text-foreground);
        background-color: var(--color-secondary);
        box-shadow: 5px 12px 20px 5px rgba(0,0,0,0.2);
    }

    .scheduled-toggle {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        cursor: pointer;
        font-size: 0.9rem;
    }

    .scheduled-summary {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0.75rem 1rem;
        border-radius: 8px;
        background: var(--color-primary-soft, rgba(0,0,0,0.05));
        cursor: pointer;
        font-size: 0.9rem;
    }

    .edit-link {
        color: var(--color-primary);
        font-size: 0.85rem;
        text-decoration: underline;
    }

    .mode-tabs {
        display: flex;
        gap: 0.5rem;
    }

    .mode-tab {
        flex: 1;
        padding: 0.5rem;
        border: 1px solid var(--color-border, #ddd);
        border-radius: 6px;
        background: none;
        cursor: pointer;
        font-size: 0.9rem;
        transition: all 0.15s;
    }

    .mode-tab.active {
        background: var(--color-primary);
        color: white;
        border-color: var(--color-primary);
    }

    .date-picker {
        display: flex;
        align-items: flex-end;
        gap: 0.75rem;
    }

    .date-picker input-form {
        flex: 1;
    }

    .date-add-btn {
        height: 40px;
        padding: 0 1rem;
        border: none;
        border-radius: 6px;
        background: var(--color-tertiary);
        color: white;
        cursor: pointer;
        white-space: nowrap;
        font-size: 0.85rem;
    }

    .date-add-btn.confirm {
        background: var(--color-tertiary);
        color: white;
    }

    .date-add-btn.cancel {
        background-color: var(--color-danger);
        color: white;
    }

    .date-tags {
        display: flex;
        flex-wrap: wrap;
        gap: 0.5rem;
        max-height: 120px;
        overflow-y: auto;
    }

    .date-tag {
        display: flex;
        align-items: center;
        gap: 0.4rem;
        padding: 0.3rem 0.75rem;
        border-radius: 20px;
        background: var(--color-primary);
        
        font-size: 0.85rem;
    }

    .date-tag button {
        background: none;
        border: none;
        color: white;
        cursor: pointer;
        padding: 0;
        font-size: 0.8rem;
    }

    .range-label {
        margin: 0;
        font-size: 0.85rem;
        color: var(--color-text-secondary, #888);
    }
`;