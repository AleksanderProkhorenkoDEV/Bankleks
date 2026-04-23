import { css } from "lit";

export const navbarStyles = css`
    :host{
        display:block;
        
        width: 100svw;
        height: 10svh;

    }

    header{
        height:100%;

        display:flex;
        justify-content: space-around;
        align-items:center;

        border-bottom: 1px solid #1F2937;
    }

    h1{
        padding:0;
        margin:0;
    }

    nav{
        display: flex;
        gap: 2rem;
    }
`