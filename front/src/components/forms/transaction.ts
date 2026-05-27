import type { ScheduledMode, ScheduledTransactionBody, TransactionBody, TransactionFormData, TransactionType } from "../../types/transactions";
import { createScheduledTransaction, createTransaction } from "../../services/transaction";
import { isIBAN, isPositive, required, validate } from "../../utils/validatior";
import { customElement, query, state } from "lit/decorators.js";
import type { InputForm, SelectForm } from "./parts";
import { html, LitElement, nothing } from "lit";
import { baseStyles } from "./base.styles";
import { modalStyles } from "../modals/edit-transaction.styles";

@customElement("transaction-form")
export class TransactionForm extends LitElement {

    @query('input-form[name="concept"]') _conceptInput!: InputForm;
    @query('input-form[name="amount"]') _amountInput!: InputForm;
    @query('input-form[name="originIban"]') _originInput!: InputForm;
    @query('input-form[name="destinationIban"]') _destinationInput!: InputForm;
    @query('select-form[name="type"]') _typeSelect!: SelectForm;
    @query('input[type="checkbox"]') _scheduledCheckbox!: HTMLInputElement;

    @state() private _type: TransactionType = 'TRANSFER';
    @state() private _isScheduled: boolean = false;
    @state() private _showModal: boolean = false;
    @state() private _scheduledMode: ScheduledMode = 'dates';
    @state() private _scheduledDates: string[] = [];
    @state() private _pendingDate: string = '';
    @state() private _rangeStart: string = '';
    @state() private _formKey: number = 0;

    private _formData: TransactionFormData = {
        concept: "", amount: "0.00", originIban: "", destinationIban: "",
        type: "TRANSFER", isScheduled: false,
        scheduledDates: [], scheduledTime: "", targetTimezone: "Europe/Madrid",
        scheduledMode: 'dates', recurrence: null, recurrenceEndDate: ""
    };

    private _timezones = [
        { value: 'Europe/Madrid', label: 'Madrid (UTC+1/+2)' },
        { value: 'Atlantic/Canary', label: 'Canarias (UTC+0/+1)' },
        { value: 'Europe/London', label: 'Londres (UTC+0/+1)' },
        { value: 'Europe/Paris', label: 'París (UTC+1/+2)' },
        { value: 'America/New_York', label: 'Nueva York (UTC-5/-4)' },
        { value: 'UTC', label: 'UTC' },
    ];

    private _recurrenceOptions = [
        { value: 'BEGINNING_OF_MONTH', label: 'Primeros de mes (día 1)' },
        { value: 'MIDDLE_OF_MONTH', label: 'Mitad de mes (día 15)' },
        { value: 'END_OF_MONTH', label: 'Finales de mes (último día)' },
    ];

    private _handleInputChange = (e: CustomEvent) => {
        const key = e.detail.name as keyof TransactionFormData;
        const value = e.detail.value as TransactionFormData[typeof key];
        this._formData = { ...this._formData, [key]: value };
        if (key === 'type') this._type = value as TransactionType;
    }

    private _handleModalInputChange = (e: CustomEvent) => {
        const { name, value } = e.detail;
        this._formData = { ...this._formData, [name]: value };
        if (name === 'scheduledDate') this._pendingDate = value;
        if (name === 'rangeStart') this._rangeStart = value;
    }

    private _handleScheduledToggle = (e: Event) => {
        this._isScheduled = (e.target as HTMLInputElement).checked;
        if (this._isScheduled) this._showModal = true;
    }

    private _handleModeChange = (mode: ScheduledMode) => {
        this._scheduledMode = mode;
        this._scheduledDates = [];
        this._formData = { ...this._formData, scheduledMode: mode, scheduledDates: [], recurrence: null };
    }

    private _addDate = () => {
        if (!this._pendingDate) return;
        if (this._scheduledDates.includes(this._pendingDate)) return;
        this._scheduledDates = [...this._scheduledDates, this._pendingDate].sort();
        this._formData = { ...this._formData, scheduledDates: this._scheduledDates };
        this._pendingDate = '';
    }

    private _addRange = () => {
        if (!this._rangeStart || !this._pendingDate) return;
        const start = new Date(this._rangeStart);
        const end = new Date(this._pendingDate);
        if (start > end) return;

        const dates: string[] = [];
        const current = new Date(start);
        while (current <= end) {
            const dateStr = current.toISOString().split('T')[0];
            if (!this._scheduledDates.includes(dateStr)) dates.push(dateStr);
            current.setDate(current.getDate() + 1);
        }

        this._scheduledDates = [...this._scheduledDates, ...dates].sort();
        this._formData = { ...this._formData, scheduledDates: this._scheduledDates };
        this._rangeStart = '';
        this._pendingDate = '';
    }

    private _removeDate = (date: string) => {
        this._scheduledDates = this._scheduledDates.filter(d => d !== date);
        this._formData = { ...this._formData, scheduledDates: this._scheduledDates };
    }

    private _closeModal = () => {
        this._showModal = false;
        if (this._scheduledMode === 'dates' && this._scheduledDates.length === 0) {
            this._isScheduled = false;
            if (this._scheduledCheckbox) this._scheduledCheckbox.checked = false;
        }
    }

    private _confirmModal = () => {
        this._showModal = false;
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
            if (!this._formData.scheduledTime) { isValid = false; }
            if (this._scheduledMode === 'dates' && this._scheduledDates.length === 0) { isValid = false; }
            if (this._scheduledMode === 'recurrent' && !this._formData.recurrence) { isValid = false; }
        }

        return isValid;
    }

    private _buildScheduledBody(): ScheduledTransactionBody {
        const body: ScheduledTransactionBody = {
            concept: this._formData.concept,
            amount: parseFloat(this._formData.amount),
            originIban: this._formData.originIban,
            destinationIban: this._formData.destinationIban,
            targetTimezone: this._formData.targetTimezone,
            scheduledTime: this._formData.scheduledTime,
            scheduledDates: this._scheduledDates,
        };

        if (this._scheduledMode === 'recurrent') {
            body.recurrence = this._formData.recurrence!;
            if (this._formData.recurrenceEndDate) {
                body.recurrenceEndDate = this._formData.recurrenceEndDate + "T00:00:00";
            }
        }

        return body;
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
            if (error) { this._dispatchToast("error", "No hemos podido programar la transacción."); return; }
            this._dispatchToast("success", "Transacción programada correctamente");
            this._resetForm();
            return;
        }

        const { error } = await createTransaction(this._buildBody());
        if (error) { this._dispatchToast("error", "No hemos podido realizar la transacción."); return; }
        this._dispatchToast("success", "Transacción creada correctamente");
        this._resetForm();
    }

    static styles = [baseStyles, modalStyles];

    private _renderDatesMode() {
        return html`
            <!-- Fechas sueltas -->
            <div class="date-picker">
                <input-form name="scheduledDate" label="Añadir fecha" type="date"
                    @input-change=${this._handleModalInputChange}>
                </input-form>
                <button type="button" class="date-add-btn" @click=${this._addDate}>+ Añadir</button>
            </div>

            <!-- Rango de fechas -->
            <p class="range-label">O selecciona un rango</p>
            <div class="date-picker">
                <input-form name="rangeStart" label="Desde" type="date"
                    @input-change=${this._handleModalInputChange}>
                </input-form>
                <input-form name="scheduledDate" label="Hasta" type="date"
                    @input-change=${this._handleModalInputChange}>
                </input-form>
                <button type="button" class="date-add-btn" @click=${this._addRange}>+ Rango</button>
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
        `;
    }

    private _renderRecurrentMode() {
        return html`
            <select-form name="recurrence" label="Repetir"
                .options=${this._recurrenceOptions}
                @input-change=${this._handleModalInputChange}>
            </select-form>

            <input-form name="scheduledDates" label="Fecha de inicio" type="date"
                @input-change=${(e: CustomEvent) => {
                this._scheduledDates = [e.detail.value];
                this._formData = { ...this._formData, scheduledDates: [e.detail.value] };
            }}>
            </input-form>

            <input-form name="recurrenceEndDate" label="Fecha de fin (opcional)" type="date"
                @input-change=${this._handleModalInputChange}>
            </input-form>
        `;
    }

    private _renderModal() {
        return html`
            <div class="overlay" @click=${(e: Event) => { if (e.target === e.currentTarget) this._closeModal(); }}>
                <div class="modal">
                    <div class="modal-header">
                        <h2>Configurar programación</h2>
                        <button class="close-btn" type="button" @click=${this._closeModal}>✕</button>
                    </div>

                    <!-- Selector de modo -->
                    <div class="mode-tabs">
                        <button type="button"
                            class="mode-tab ${this._scheduledMode === 'dates' ? 'active' : 'active-secondary'}"
                            @click=${() => this._handleModeChange('dates')}>
                            Programada
                        </button>
                        <button type="button"
                            class="mode-tab ${this._scheduledMode === 'recurrent' ? 'active' : 'active-secondary'}"
                            @click=${() => this._handleModeChange('recurrent')}>
                            Recurrente
                        </button>
                    </div>

                    <!-- Hora y timezone — comunes a todos los modos -->
                    <input-form name="scheduledTime" label="Hora de ejecución" type="time"
                        @input-change=${this._handleModalInputChange}>
                    </input-form>

                    <select-form name="targetTimezone" label="Zona horaria"
                        .options=${this._timezones}
                        @input-change=${this._handleModalInputChange}>
                    </select-form>

                    ${this._scheduledMode === 'dates' ? this._renderDatesMode() : this._renderRecurrentMode()}

                    <div class="modal-footer">
                        <button type="button" class="date-add-btn cancel" @click=${this._closeModal}>Cancelar</button>
                        <button type="button" class="date-add-btn confirm" @click=${this._confirmModal}>Confirmar</button>
                    </div>
                </div>
            </div>
        `;
    }

    private _resetForm = () => {
        this._type = 'TRANSFER';
        this._isScheduled = false;
        this._showModal = false;
        this._scheduledMode = 'dates';
        this._scheduledDates = [];
        this._pendingDate = '';
        this._rangeStart = '';
        this._formData = {
            concept: "", amount: "0.00", originIban: "", destinationIban: "",
            type: "TRANSFER", isScheduled: false,
            scheduledDates: [], scheduledTime: "", targetTimezone: "Europe/Madrid",
            scheduledMode: 'dates', recurrence: null, recurrenceEndDate: ""
        };
        this._formKey++;
        if (this._scheduledCheckbox) this._scheduledCheckbox.checked = false;
    }

    render() {
        return html`
            ${this._showModal ? this._renderModal() : nothing}

            <form key=${this._formKey} @submit=${this._handleSubmit}>
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

                <!-- Toggle + resumen de programación -->
                <label class="scheduled-toggle">
                    <input type="checkbox" @change=${this._handleScheduledToggle} />
                    Programar transferencia
                </label>

                ${this._isScheduled ? html`
                    <div class="scheduled-summary" @click=${() => this._showModal = true}>
                        <span>⚙️ ${this._scheduledMode === 'recurrent'
                    ? `Recurrente · ${this._formData.recurrence ?? 'sin configurar'}`
                    : `${this._scheduledDates.length} fecha(s) seleccionada(s)`
                }</span>
                        <span class="edit-link">Editar</span>
                    </div>
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