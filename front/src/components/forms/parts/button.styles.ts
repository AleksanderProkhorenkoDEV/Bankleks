import { css } from "lit";

export const buttonStyles = css` 
    button {
        width:100%;

        text-transform:uppercase;
        letter-spacing:2px;
        font-family: var(--font-main);
        font-weight:600;

        padding: .7rem;
        
        cursor: pointer;
        
        border-radius: 8px;
        border: none;

        transition: background-color .1s ease-in-out;
    }

    :host([variant="primary"]) button {
        background: var(--color-tertiary);
        color: var(--text-foreground);
    }

    :host([variant="primary"]) button:hover{
        background: color-mix(
            in srgb,
            var(--color-tertiary) 85%,
            var(--background-color)
        )
    }

    :host([variant="secondary"]) button {
        background: transparent;
        color: var(--text-foreground);

        border: 1px solid var(--text-foreground);
    }

    :host([variant="danger"]) button {
        background: var(--color-danger);
        color: white;
    }

    :host([variant="danger"]) button:hover {
        background: color-mix(
            in srgb,
            var(--color-danger) 85%,
            var(--background-color)
        )
    }

    button:disabled {
        opacity: 0.5;
        cursor: not-allowed;
    }
`