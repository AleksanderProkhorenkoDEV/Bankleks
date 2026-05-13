import { customElement, state } from "lit/decorators.js";
import type { AccountResponse, TimezoneType } from "../types/account";
import { getAccount, getTimezone, updateTimezone } from "../services/account";
import { html, LitElement } from "lit";
import { accountStyles } from "./styles/account.styles";

const TIMEZONES: TimezoneType[] = [
    'UTC',
    'Europe/Madrid',
    'Europe/London',
    'America/New_York',
    'America/Los_Angeles',
    'Asia/Tokyo',
];

@customElement("account-page")
export class AccountPage extends LitElement {

    @state() private _account: AccountResponse | null = null;
    @state() private _loading: boolean = true;
    @state() private _error: string = '';
    @state() private _showAccount: boolean = false;
    @state() private _timezone: TimezoneType = 'UTC';
    @state() private _savingTimezone: boolean = false;
    @state() private _timezoneSaved: boolean = false;

    async connectedCallback() {
        super.connectedCallback();
        await Promise.all([this._loadAccount(), this._loadTimezone()]);
    }

    private async _loadAccount() {
        const result = await getAccount();

        if (!result.ok) {
            this._error = result.error ?? 'Error desconocido';
            this._loading = false;
            return;
        }

        this._account = result.data ?? null;
        this._loading = false;
    }

    private _toggleAccount = (event: Event) => {
        event.preventDefault();
        this._showAccount = !this._showAccount;
    }

    private async _loadTimezone() {
        const result = await getTimezone();
        if (result.ok && result.data) {
            this._timezone = result.data;
        }
    }

    private _saveTimezone = async (event: Event) => {
        event.preventDefault();
        this._savingTimezone = true;
        const result = await updateTimezone(this._timezone);
        this._savingTimezone = false;

        if (result.ok) {
            this._timezoneSaved = true;
            this.dispatchEvent(new CustomEvent("show-toast", {
                detail: { type: "success", message: "Zona horaria actualizada correctamente" },
                bubbles: true,
                composed: true
            }));
            return;
        }

        this.dispatchEvent(new CustomEvent("show-toast", {
            detail: { type: "error", message: "No hemos podido actualizar el valor. Inténtelo más tarde." },
            bubbles: true,
            composed: true
        }));
    }

    private _onTimezoneChange = (event: Event) => {
        this._timezone = (event.target as HTMLSelectElement).value as TimezoneType;
        this._timezoneSaved = false;
    }


    static styles = [accountStyles]

    render() {
        if (this._loading) return html`<loading-screen></loading-screen>`;
        if (this._error) return html`<p>${this._error}</p>`;

        const name = this._account?.userSummaryDTO.username ?? '';
        const balance = this._account?.balance.toLocaleString('es-ES', { minimumFractionDigits: 2 }) ?? '0,00';
        const accountNumber = this._account?.accountNumber ?? '';
        const maskedNumber = accountNumber ? `•••• •••• •••• ${accountNumber.slice(-4)}` : '';
        const maskedAccount = accountNumber ? `${accountNumber.slice(0, 4)} •••• •••• ${accountNumber.slice(-4)}` : '';
        const now = new Date().toLocaleString('es-ES', { dateStyle: 'short', timeStyle: 'short' });

        return html`
        <section class="account">
            <article class="header">
                <h1>Hola, ${name}</h1>
                <p>Aquí puedes consultar el estado de tu cuenta</p>
            </article>

            <div class="grid">
                <div class="balance-card">
                    <div class="balance-card-header">
                        <span class="balance-label">⊟ Balance disponible</span>
                        <div class="balance-icon">↗</div>
                    </div>
                    <h2 class="balance-amount">${balance} €</h2>
                    <hr class="balance-divider" />
                    <div class="balance-footer">
                        <span class="dot"></span>
                        <span>Última actualización: Hoy, ${now}</span>
                    </div>
                </div>

                <div class="bank-card">
                    <div class="bank-card-header">
                        <span class="bank-card-title">⊟ Mi Banco</span>
                        <button class="show-btn" @click=${this._toggleAccount}>
                            ⊙ ${this._showAccount ? 'Ocultar' : 'Mostrar'}
                        </button>
                    </div>
                    <div>
                        <p class="card-number-label">Número de tarjeta</p>
                        <p class="card-number">${this._showAccount ? accountNumber : maskedNumber}</p>
                    </div>
                    <div class="bank-card-footer">
                        <div>
                            <p class="card-meta-label">Titular</p>
                            <p class="card-meta-value">${name}</p>
                        </div>
                        <div>
                            <p class="card-meta-label">Válida hasta</p>
                            <p class="card-meta-value">12/28</p>
                        </div>
                    </div>
                </div>
            </div>

            <div class="grid">
                <div class="info-card">
                    <h3 class="info-card-title">⊙ Información de la cuenta</h3>
                    <div class="info-row">
                        <div class="info-row-icon">👤</div>
                        <div>
                            <p class="info-row-label">Titular</p>
                            <p class="info-row-value">${name}</p>
                        </div>
                    </div>
                    <div class="info-row">
                        <div class="info-row-icon">⊟</div>
                        <div>
                            <p class="info-row-label">Número de cuenta</p>
                            <p class="info-row-value">${this._showAccount ? accountNumber : maskedAccount}</p>
                        </div>
                    </div>
                    <div class="account-type-badge">Cuenta Corriente Premium</div>
                </div>
                <div class="info-card">
                    <h3 class="info-card-title">⊙ Zona horaria</h3>
                    <div class="timezone-row">
                        <div class="timezone-row-top">
                            <div class="info-row-icon">🕐</div>
                            <div style="flex:1">
                                <p class="info-row-label">Zona horaria de la cuenta</p>
                                <select
                                    class="timezone-select"
                                    @change=${this._onTimezoneChange}
                                >
                                    ${TIMEZONES.map(tz => html`
                                        <option value=${tz} ?selected=${tz === this._timezone}>${tz}</option>
                                    `)}
                                </select>
                            </div>
                        </div>
                        <div class="timezone-row-actions">
                            <button-form
                                type="button"
                                @click=${this._saveTimezone}
                                .disabled=${this._savingTimezone}
                                .variant=${"secondary"}
                            >
                                ${this._savingTimezone ? 'Guardando...' : this._timezoneSaved ? '✓ Guardado' : 'Guardar'}
                            </button-form>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    `
    }
}

declare global {
    interface HTMLElemenTagByName {
        "account-page": AccountPage
    }
}