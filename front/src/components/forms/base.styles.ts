import { css } from "lit";

export const baseStyles = css` 

    :host{
        display:block;
        width:65%;
    }

    form{
       
        display:flex;
        flex-direction:column;
        gap:2rem;
        
        padding: 2rem;
        
        border-radius: 8px;
        color:var(--text-foreground);
        background-color: var(--color-secondary);

        box-shadow: 5px 12px 20px 5px rgba(0,0,0,0.2);
        
    }

`