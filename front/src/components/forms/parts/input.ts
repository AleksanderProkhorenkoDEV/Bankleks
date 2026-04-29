import { customElement, property, state } from "lit/decorators.js";
import { html, LitElement, nothing } from "lit";
import { inputStyle } from "./input.style";

type InputType = "text" | "number" | "email" | "hidden" | "password";

@customElement("input-form")
export class InputForm extends LitElement {

    @property({ type: String }) name: string = ''
    @property({ type: String }) label: string = ''
    @property({ type: String }) type: InputType = "text"
    @property({ type: String }) placeholder: string = ''
    @property({ type: Number }) step: number = 1

    @state() private _error: string = '';
    @state() private _value: string = '';

    private handleInput(e: Event) {
        const input = e.target as HTMLInputElement;
        this._value = input.value
        this._error = '';

        this.dispatchEvent(new CustomEvent("input-change", {
            detail: { name: this.name, value: this._value },
            bubbles: true,
            composed: true
        }))
    }

    setError(message: string) {
        this._error = message;
    }

    static styles? = [
        inputStyle,
    ]

    render() {
        return html`
            <div class="input__group">
                <label for=${this.name}>${this.label}</label>
                <input 
                    id=${this.name}
                    type=${this.type}
                    placeholder=${this.placeholder}
                    value=${this._value}
                    step=${this.step}
                    ?data-error=${!!this._error}
                    @input=${this.handleInput}
                />
                ${this._error ? html`<span class="input__error">${this._error}</span>` : nothing
            }
            </div>
        `;
    }
}

declare global {
    interface HTMLElementTagNameMap {
        "input-form": InputForm
    }
}