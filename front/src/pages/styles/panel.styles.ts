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
        max-width: 900px;
        margin: 0 auto;
        padding: 2.5rem 1.5rem;
    }

    h1 {
        font-size: 1.4rem;
        font-weight: 700;
        color: var(--text-primary);
        margin: 0 0 2rem;
    }
`;