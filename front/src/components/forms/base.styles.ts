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
        background-color: var(--color-primary);
        color: white;
        cursor: pointer;
        white-space: nowrap;
    }

    .date-tags {
        display: flex;
        flex-wrap: wrap;
        gap: 0.5rem;
    }

    .date-tag {
        display: flex;
        align-items: center;
        gap: 0.4rem;
        padding: 0.3rem 0.75rem;
        border-radius: 20px;
        background-color: var(--color-primary);
        color: white;
        font-size: 0.85rem;
    }

    .date-tag button {
        background: none;
        border: none;
        color: white;
        cursor: pointer;
        padding: 0;
        font-size: 0.8rem;
        line-height: 1;
    }


`