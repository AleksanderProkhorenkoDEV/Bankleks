import { customElement, property } from "lit/decorators.js";
import { buttonStyles } from "./button.styles";
import { html, LitElement } from "lit";

@customElement("button-form")
export class ButtonForm extends LitElement {


    @property() variant: 'primary' | 'secondary' | 'danger' | 'update' = 'primary';
    @property() type: 'submit' | 'button' | 'reset' = 'button';
    @property({ type: Boolean }) disabled: boolean = false;

    private _handleClick = () => {
        if (this.type === 'submit') {
            const form = this.closest('form');
            form?.requestSubmit();
        }
    }

    static styles = [
        buttonStyles,
    ]

    render() {
        return html`
            <button 
                type=${this.type}
                ?disabled=${this.disabled}
                @click=${this._handleClick}
            >
                <slot></slot>
            </button>
        `
    }
}

declare global {
    interface HTMLElementTagNameMap {
        "button-form": ButtonForm
    }
}