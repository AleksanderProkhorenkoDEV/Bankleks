import { css } from "lit";

export const selectStyles = css` 
    select {
        padding: 0.7rem;
        border: 0px;
        border-radius: 6px;
        background-color: var(--background-color);
        color: var(--text-primary);
        font-family: var(--font-main);
        font-size: 1rem;
        width: 100%;
        cursor: pointer;
        appearance: none;
        background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='%23134E4A' stroke-width='2'%3E%3Cpolyline points='6 9 12 15 18 9'%3E%3C/polyline%3E%3C/svg%3E");
        background-repeat: no-repeat;
        background-position: right 0.75rem center;
        padding-right: 2rem;
    }

    select:focus {
        outline: none;
        border: 1px solid var(--color-tertiary);
    }

    select[data-error] {
        border: 1px solid var(--color-danger);
        background-color: color-mix(
            in srgb,
            var(--color-danger) 65%,
            var(--background-color)
        );
        color: #E0FDF4;
    }

    .select__group {
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
    }

    label {
        font-size: 0.875rem;
        font-family: var(--font-main);
    }

    .select__error {
        color: color-mix(
            in srgb,
            var(--color-danger) 65%,
            var(--background-color)
        );
    }

    .select__error::before {
        content: "*"
    }
`