import { css } from "lit";

export const registerStyles = css`
    :host{
        display:block;

        height:100%;
    }

    .register{
        height:100%;

        display:grid;
        grid-template-columns: repeat(2,1fr);
    }

    .register__item{
        display: flex;
        align-items:center;
        justify-content:center;
    }
`