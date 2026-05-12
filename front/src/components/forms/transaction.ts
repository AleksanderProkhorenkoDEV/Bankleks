import type { ScheduledTransactionBody, TransactionBody, TransactionFormData, TransactionType } from "../../types/transactions";
import { createScheduledTransaction, createTransaction } from "../../services/transaction";
import { isIBAN, isPositive, required, validate } from "../../utils/validatior";
import { customElement, query, state } from "lit/decorators.js";
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
    @query('input-form[name="scheduledTime"]') _scheduledTimeInput!: InputForm;
    @query('select-form[name="targetTimezone"]') _timezoneSelect!: SelectForm;
    @query('input-form[name="scheduledDate"]') _scheduledDateInput!: InputForm;

    @state() private _type: TransactionType = 'TRANSFER';
    @state() private _isScheduled: boolean = false;
    @state() private _scheduledDates: string[] = [];
    @state() private _pendingDate: string = '';

    private _formData: TransactionFormData = {
        concept: "", amount: "0.00", originIban: "", destinationIban: "",
        type: "DEPOSIT", isScheduled: false,
        scheduledDate: [], scheduledTime: "", targetTimezone: "Europe/Madrid"
    };

    private _timezones = [
        { value: 'Europe/Madrid', label: 'Madrid (UTC+1/+2)' },
        { value: 'Atlantic/Canary', label: 'Canarias (UTC+0/+1)' },
        { value: 'Europe/London', label: 'Londres (UTC+0/+1)' },
        { value: 'Europe/Paris', label: 'París (UTC+1/+2)' },
        { value: 'America/New_York', label: 'Nueva York (UTC-5/-4)' },
        { value: 'UTC', label: 'UTC' },
    ];

    private _handleInputChange = (e: CustomEvent) => {
        const key = e.detail.name as keyof TransactionFormData;
        const value = e.detail.value as TransactionFormData[typeof key];
        this._formData = { ...this._formData, [key]: value };

        if (key === 'type') this._type = value as TransactionType;
        if (key === 'scheduledDate') this._pendingDate = value as string;
    }

    private _handleScheduledToggle = (e: Event) => {
        this._isScheduled = (e.target as HTMLInputElement).checked;
        this._formData.isScheduled = this._isScheduled;
    }

    private _addDate = () => {
        if (!this._pendingDate) return;
        if (this._scheduledDates.includes(this._pendingDate)) return;

        this._scheduledDates = [...this._scheduledDates, this._pendingDate].sort();
        this._formData = { ...this._formData, scheduledDate: this._scheduledDates };
        this._pendingDate = '';
    }

    private _removeDate = (date: string) => {
        this._scheduledDates = this._scheduledDates.filter(d => d !== date);
        this._formData = { ...this._formData, scheduledDate: this._scheduledDates };
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

        if (this._isScheduled) {
            const timeError = validate(this._formData.scheduledTime ?? '', [required()]);
            if (timeError) { this._scheduledTimeInput.setError(timeError); isValid = false; }

            if (this._scheduledDates.length === 0) {
                this._scheduledDateInput.setError('Añade al menos una fecha');
                isValid = false;
            }
        }

        return isValid;
    }

    private _buildScheduledBody(): ScheduledTransactionBody {
        return {
            concept: this._formData.concept,
            amount: parseFloat(this._formData.amount),
            originIban: this._formData.originIban,
            destinationIban: this._formData.destinationIban,
            targetTimezone: this._formData.targetTimezone,
            scheduledTime: this._formData.scheduledTime,
            scheduledDates: this._scheduledDates,
        }
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

        if (this._isScheduled) {
            const { error } = await createScheduledTransaction(this._buildScheduledBody());
            if (error) {
                this._dispatchToast("error", "No hemos podido programar la transacción.");
                return;
            }
            this._dispatchToast("success", "Transacción programada correctamente");
            return;
        }

        const { error } = await createTransaction(this._buildBody());
        if (error) {
            this._dispatchToast("error", "No hemos podido realizar la transacción.");
            return;
        }
        this._dispatchToast("success", "Transacción creada correctamente");
    }

    static styles = [baseStyles];

    render() {
        return html`
            <form @submit=${this._handleSubmit}>
                <input-form name="concept" label="Concepto" type="text"
                    placeholder="Bizum desayuno"
                    @input-change=${this._handleInputChange}>
                </input-form>

                <input-form name="amount" label="Cantidad" type="number" step="0.01"
                    placeholder="0,00"
                    @input-change=${this._handleInputChange}>
                </input-form>

                <select-form name="type" label="Tipo de transacción"
                    .options=${[
                { value: 'TRANSFER', label: 'Transferencia' },
                { value: 'DEPOSIT', label: 'Ingreso' },
                { value: 'WITHDRAWAL', label: 'Retirada' }
            ]}
                    @input-change=${this._handleInputChange}>
                </select-form>

                ${this._type !== 'DEPOSIT' ? html`
                    <input-form name="originIban" label="Cuenta de origen" type="text"
                        placeholder="ES03659556...."
                        @input-change=${this._handleInputChange}>
                    </input-form>
                ` : nothing}

                ${this._type !== 'WITHDRAWAL' ? html`
                    <input-form name="destinationIban" label="Cuenta destino" type="text"
                        placeholder="ES026595698...."
                        @input-change=${this._handleInputChange}>
                    </input-form>
                ` : nothing}

                <label class="scheduled-toggle">
                    <input type="checkbox" @change=${this._handleScheduledToggle} />
                    Programar transferencia
                </label>

                ${this._isScheduled ? html`

                    <input-form name="scheduledTime" label="Hora de ejecución" type="time"
                        @input-change=${this._handleInputChange}>
                    </input-form>

                    <select-form name="targetTimezone" label="Zona horaria"
                        .options=${this._timezones}
                        @input-change=${this._handleInputChange}>
                    </select-form>

                    <!-- Selector de fechas -->
                    <div class="date-picker">
                        <input-form name="scheduledDate" label="Añadir fecha" type="date"
                            @input-change=${this._handleInputChange}>
                        </input-form>
                        <button type="button" class="date-add-btn" @click=${this._addDate}>
                            + Añadir
                        </button>
                    </div>

                    ${this._scheduledDates.length > 0 ? html`
                        <div class="date-tags">
                            ${this._scheduledDates.map(date => html`
                                <span class="date-tag">
                                    ${date}
                                    <button type="button" @click=${() => this._removeDate(date)}>✕</button>
                                </span>
                            `)}
                        </div>
                    ` : nothing}

                ` : nothing}

                <button-form variant="primary" .type=${"submit"}>
                    ${this._isScheduled ? 'Programar transacción' : 'Realizar transacción'}
                </button-form>
            </form>
        `;
    }
}

declare global {
    interface HTMLElementTagNameMap {
        "transaction-form": TransactionForm
    }
}