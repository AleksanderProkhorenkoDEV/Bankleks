import { css } from "lit";

export const tableStyles = css` 
    :host {
        display: block;
        font-family: var(--font-main);
    }

    .table-wrapper {
        border-radius: 12px;
        overflow-x: auto;
        background: white;
    }

    table {
        width: 100%;
        border-collapse: collapse;
    }

    thead tr {
        background: var(--color-secondary);
    }

    thead th {
        padding: 1rem 1.25rem;
        text-align: left;
        font-size: 0.8rem;
        font-weight: 600;
        color: var(--text-foreground);
        letter-spacing: 0.03em;
        border-bottom: 2px solid var(--background-color);
    }

    tbody tr {
        transition: background 0.15s;
    }

    tbody tr:nth-child(odd) {
        background: white;
    }

    tbody tr:nth-child(even) {
        background: var(--background-color);
    }

    tbody tr:nth-child(even) td {
        color: var(--color-border);
    }

    tbody tr:hover {
        filter: brightness(0.96);
    }

    tbody td {
        padding: 0.85rem 1.25rem;
        font-size: 0.875rem;
        color: var(--color-border);
        border-bottom: 1px solid var(--background-color);
    }

    tbody tr:last-child td {
        border-bottom: none;
    }

    .actions {
        display: flex;
        gap: 0.5rem;
    }


    /* PAGINATION */
    .pagination {
        display: flex;
        align-items: center;
        justify-content: flex-end;
        gap: 0.75rem;
        padding: 1rem 0.5rem 0;
        font-size: 0.8rem;
        color: var(--color-secondary);
        font-family: var(--font-main);
    }

    .pagination button {
        background: none;
        border: none;
        cursor: pointer;
        font-size: 0.8rem;
        color: var(--color-secondary);
        padding: 0.25rem 0.5rem;
        border-radius: 4px;
        transition: background 0.15s;
    }

    .pagination button:hover:not(:disabled) {
        background: var(--background-color);
    }

    .pagination button:disabled {
        opacity: 0.4;
        cursor: not-allowed;
    }

    .empty {
        text-align: center;
        padding: 3rem;
        color: var(--color-secondary);
        font-size: 0.875rem;
    }

    @media (max-width: 600px) {
    .table-wrapper {
        overflow-x: auto;
    }

    table {
        min-width: 700px; /* o ajusta según tus columnas */
    }
}
`