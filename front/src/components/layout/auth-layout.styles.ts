import { css } from "lit";

export const authLayoutStyles = css`
    :host{
        display:block;

        height:100%;
    }

    .auth{
        height:100%;

        display:grid;
        grid-template-columns: repeat(2,1fr);
    }

    .auth__item{
        display: flex;
        align-items:center;
        justify-content:center;
    }
`