import { css } from "lit";

export const navLinkStyles = css`

    :host{
        font-weight: 600;
    }

    a{
        text-decoration: none;
        color: var(--text-primary);
        transition: color 0.2s ease-in-out;

    }

    a:hover{
        color:var(--color-secondary);
    }

    .panel{
        padding:0.6rem;
        
        background-color: var(--color-secondary);
        border-radius: 4px;

        color:var(--text-foreground);

        transition: all .2s ease-in-out;
    }

    .panel:hover{
        background-color: color-mix(in srgb,  var(--color-secondary), transparent 70%);
    }
`