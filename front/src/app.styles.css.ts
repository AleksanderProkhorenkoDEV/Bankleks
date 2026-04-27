import { css } from "lit";

export const appStyles = css` 
    :host{
        display:block;

        height: 100svh;
        
        box-sizing:border-box;
    }

    main{
        height: calc(100svh - 10.70svh);
        overflow-y:auto;
    }
`