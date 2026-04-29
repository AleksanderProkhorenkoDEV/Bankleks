import { html, LitElement, nothing } from "lit";
import { customElement, property, state } from "lit/decorators.js";
import { selectStyles } from "./select.styles";

export interface SelectOption {
    value: string;
    label: string;
}


@customElement("select-form")
export class SelectForm extends LitElement {

    @property({ type: String }) name: string = '';
    @property({ type: String }) label: string = '';
    @property({ type: Array }) options: SelectOption[] = [];

    @state() private _error: string = '';
    @state() private _value: string = '';


    setError(message: string) {
        this._error = message;
    }

    private _handleChange(e: Event) {
        const select = e.target as HTMLSelectElement;
        this._value = select.value;
        this._error = '';

        this.dispatchEvent(new CustomEvent('input-change', {
            detail: { name: this.name, value: this._value },
            bubbles: true,
            composed: true
        }));
    }

    static styles = [
        selectStyles
    ]

    render() {
        return html`
            <div class="select__group">
                <label for=${this.name}>${this.label}</label>
                <select
                    id=${this.name}
                    ?data-error=${!!this._error}
                    @change=${this._handleChange}
                >
                    <option value="" disabled selected>Selecciona una opción</option>
                    ${this.options.map(opt => html`
                        <option value=${opt.value}>${opt.label}</option>
                    `)}
                </select>
                ${this._error ? html`<span class="select__error">${this._error}</span>` : nothing}
            </div>
        `
    }
}

declare global {
    interface HTMLElementTagNameMap {
        "select-form": SelectForm
    }
}