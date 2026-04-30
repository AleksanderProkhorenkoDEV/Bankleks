import { html, LitElement, nothing } from "lit";
import { customElement, property, query, state } from "lit/decorators.js";
import type { TransactionResponse } from "../../types/transactions";
import type { InputForm } from "../forms";
import { minLength, required, validate } from "../../utils/validatior";
import { modalStyles } from "./edit-transaction.styles";

@customElement("edit-transaction-modal")
export class EditTransactionModal extends LitElement {

    @property({ type: Object }) transaction: TransactionResponse | null = null;

    @query('input-form[name="concept"]') _conceptInput!: InputForm;

    @state() private _concept: string = '';

    static styles = [modalStyles];

    updated(changed: Map<string, unknown>) {
        if (changed.has('transaction') && this.transaction) {
            this._concept = this.transaction.concept;
        }
    }

    private _handleInputChange = (e: CustomEvent) => {
        this._concept = e.detail.value;
    }

    private _close() {
        this.dispatchEvent(new CustomEvent('modal-close', { bubbles: true, composed: true }));
    }

    private _handleSubmit = async (e: SubmitEvent) => {
        e.preventDefault();

        const conceptError = validate(this._concept, [required(), minLength(4)]);
        if (conceptError) { this._conceptInput.setError(conceptError); return; }

        this.dispatchEvent(new CustomEvent('modal-submit', {
            detail: { id: this.transaction?.id, concept: this._concept },
            bubbles: true,
            composed: true
        }));
    }

    render() {
        if (!this.transaction) return nothing;

        return html`
            <div class="overlay" @click=${(e: Event) => e.target === e.currentTarget && this._close()}>
                <div class="modal">
                    <div class="modal-header">
                        <h2>Editar concepto</h2>
                        <button class="close-btn" @click=${this._close}>✕</button>
                    </div>
                    <form @submit=${this._handleSubmit}>
                        <input-form
                            name="concept"
                            label="Concepto"
                            type="text"
                            placeholder=${this.transaction.concept}
                            @input-change=${this._handleInputChange}
                        ></input-form>
                        <div class="modal-footer">
                            <button-form variant="danger" type="button" @click=${this._close}>
                                Cancelar
                            </button-form>
                            <button-form variant="primary" .type=${"submit"}>
                                Guardar
                            </button-form>
                        </div>
                    </form>
                </div>
            </div>
        `;
    }
}

declare global {
    interface HTMLElementTagNameMap{
        "edit-transaction-modal": EditTransactionModal
    }
}