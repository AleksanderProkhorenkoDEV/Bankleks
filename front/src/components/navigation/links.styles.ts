import { css } from "lit";

export const linksStyles = css`
    a {
        text-decoration: none;
        transition: opacity 0.2s;
    }

    a:hover {
        opacity: 0.7;
        text-decoration: underline;
    }

    a.light {
        color: var(--color-secondary);
    }

    a.dark {
        color: var(--text-foreground);
    }
`