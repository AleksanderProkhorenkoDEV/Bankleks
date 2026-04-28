import { css } from "lit";

export const toastStyles = css` 

    :host {
        display: block;
        position: absolute;
        bottom: 10%;
        right: 7%;
        pointer-events: none;
    }

    .container{      
        padding:0.5rem 0.7rem;
        
        border-radius:8px;
        box-shadow: 3px 3px 3px rgba(0,0,0,0.2);
        
        pointer-events: all;
        
        animation: displayHidden 4s ease-in-out;
    }

    .content{
        font-size:14px;
    }

    :host([variant="success"]) .container {
        background-color: var(--color-tertiary);
        color: var(--text-foreground);
    }

    :host([variant="error"]) .container {
        background-color: var(--color-danger);
        color: var(--text-foreground);
    }

    @keyframes displayHidden {
        0%   { opacity: 0; transform: translateY(10px); }
        15%  { opacity: 1; transform: translateY(0); }
        75%  { opacity: 1; transform: translateY(0); }
        100% { opacity: 0; transform: translateY(10px); }
    }
`