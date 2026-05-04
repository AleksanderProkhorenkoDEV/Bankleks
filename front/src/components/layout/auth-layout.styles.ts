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
    }

    .auth__item {
        display: flex;
        align-items: center;
        justify-content: center;
    }

    /* Móvil: ocultar el primer auth__item (imagen) */
    @media (max-width: 768px) {
        .auth {
            grid-template-columns: 1fr;
        }

        .auth__item:first-child {
            display: none;
        }
    }
`