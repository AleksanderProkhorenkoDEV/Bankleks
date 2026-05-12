import { css } from "lit";

export const authLayoutStyles = css`
    :host {
        display: block;
        height: 100%;
    }

    .auth {
        height: 100%;
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        align-items: center;
        overflow: hidden;
    }

    .auth__item {
        display: flex;
        align-items: center;
        justify-content: center;
        max-height: 100vh;
        overflow-y: auto;
        padding: 2rem 0;
    }

    @media (max-width: 768px) {
        .auth {
            grid-template-columns: 1fr;
        }

        .auth__item:first-child {
            display: none;
        }
    }
`