import { css } from "lit";

export const inputStyle = css`
    input{
        
        padding:0.7rem;

        border: 0px;
        border-radius:6px;

        background-color: var(--background-color);
    }

    input::placeholder{
        color: var(--color-secondary);
    }

    .input__group{
        display:flex;
        flex-direction:column;
        gap:0.5rem;
    }

    input[data-error] {
        border: 1px solid var(--color-danger);
        background-color: color-mix(
            in srgb,
            var(--color-danger) 65%,
            var(--background-color)
        );
        color: #E0FDF4;
    }

    input[data-error]::placeholder {
        color: var(--text-foreground);
    }

    .input__error{
        color: color-mix(
            in srgb,
            var(--color-danger) 65%,
            var(--background-color)
        );
    }

    .input__error::before{
        content: "*"
    }
`