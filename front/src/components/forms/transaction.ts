import type { TransactionBody, TransactionFormData, TransactionType } from "../../types/transactions";
import { isIBAN, isPositive, required, validate } from "../../utils/validatior";
import { customElement, query, state } from "lit/decorators.js";
import { createTransaction } from "../../services/transaction";
import type { InputForm, SelectForm } from "./parts";
import { html, LitElement, nothing } from "lit";
import { baseStyles } from "./base.styles";


@customElement("transaction-form")
export class TransactionForm extends LitElement {

    @query('input-form[name="concept"]') _conceptInput!: InputForm;
    @query('input-form[name="amount"]') _amountInput!: InputForm;
    @query('input-form[name="originIban"]') _originInput!: InputForm;
    @query('input-form[name="destinationIban"]') _destinationInput!: InputForm;
    @query('select-form[name="type"]') _typeSelect!: SelectForm;

    @state() private _type: TransactionType = 'TRANSFER';

    private _formData: TransactionFormData = { concept: "", amount: "0.00", originIban: "", destinationIban: "", type: "DEPOSIT" };

    private _handleInputChange = (e: CustomEvent) => {
        const key = e.detail.name as keyof TransactionFormData;
        this._formData[key] = e.detail.value;

        if (e.detail.name === 'type') {
            this._type = e.detail.value as TransactionType;
        }
    }

    private _dispatchToast(type: 'success' | 'error', message: string) {
        this.dispatchEvent(new CustomEvent("show-toast", {
            detail: { type, message },
            bubbles: true,
            composed: true
        }));
    }

    private _validate(): boolean {
        let isValid = true;

        const conceptError = validate(this._formData.concept ?? '', [required()]);
        const amountError = validate(this._formData.amount ?? '', [required(), isPositive()]);
        const typeError = validate(this._formData.type ?? '', [required()]);

        if (conceptError) { this._conceptInput.setError(conceptError); isValid = false; }
        if (amountError) { this._amountInput.setError(amountError); isValid = false; }
        if (typeError) { this._typeSelect.setError(typeError); isValid = false; }

        if (this._type !== 'DEPOSIT') {
            const originError = validate(this._formData.originIban ?? '', [required(), isIBAN()]);
            if (originError) { this._originInput.setError(originError); isValid = false; }
        }

        if (this._type !== 'WITHDRAWAL') {
            const destinationError = validate(this._formData.destinationIban ?? '', [required(), isIBAN()]);
            if (destinationError) { this._destinationInput.setError(destinationError); isValid = false; }
        }

        return isValid;
    }

    private _buildBody(): TransactionBody {
        return {
            concept: this._formData.concept,
            amount: parseFloat(this._formData.amount),
            transactionType: this._type,
            originIban: this._type !== 'DEPOSIT' ? this._formData.originIban : undefined,
            destinationIban: this._type !== 'WITHDRAWAL' ? this._formData.destinationIban : undefined,
        }
    }

    private _handleSubmit = async (e: SubmitEvent) => {
        e.preventDefault();

        if (!this._validate()) return;

        const { error } = await createTransaction(this._buildBody());
        if (error) {
            this._dispatchToast("error", "No hemos podido realizar la transacción.")
            return
        }

        this._dispatchToast("success", "Transacción creada correctamente")
    }


    static styles = [
        baseStyles,
    ]

    render() {
        return html`
            <form @submit=${this._handleSubmit}>
            <input-form
                name="concept"
                label="Concepto"
                type="text"
                placeholder="Bizum desayuno"
                @input-change=${this._handleInputChange}
            ></input-form>

            <input-form
                name="amount"
                label="Cantidad"
                type="number"
                step="0.01"
                placeholder="0,00"
                @input-change=${this._handleInputChange}
            ></input-form>

            <select-form
                name="type"
                label="Tipo de transacción"
                .options=${[
                { value: 'TRANSFER', label: 'Transferencia' },
                { value: 'DEPOSIT', label: 'Ingreso' },
                { value: 'WITHDRAWAL', label: 'Retirada' }
            ]}
                @input-change=${this._handleInputChange}
            ></select-form>

            ${this._type !== 'DEPOSIT' ? html`
                <input-form
                    name="originIban"
                    label="Cuenta de origen"
                    type="text"
                    placeholder="ES03659556...."
                    @input-change=${this._handleInputChange}
                ></input-form>
            ` : nothing}

            ${this._type !== 'WITHDRAWAL' ? html`
                <input-form
                    name="destinationIban"
                    label="Cuenta destino"
                    type="text"
                    placeholder="ES026595698...."
                    @input-change=${this._handleInputChange}
                ></input-form>
            ` : nothing}

            <button-form variant="primary" .type=${"submit"}>
                Realizar transacción
            </button-form>
        </form>
        `
    }
}

declare global {
    interface HTMLElementTagNameMap {
        "transaction-form": TransactionForm
    }
}