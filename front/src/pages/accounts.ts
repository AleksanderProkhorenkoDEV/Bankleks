import { customElement, state } from "lit/decorators.js";
import type { AccountResponse } from "../types/account";
import { getAccount } from "../services/account";
import { html, LitElement } from "lit";
import { accountStyles } from "./styles/account.styles";

@customElement("account-page")
export class AccountPage extends LitElement {

    @state() private _account: AccountResponse | null = null;
    @state() private _loading: boolean = true;
    @state() private _error: string = '';

    async connectedCallback() {
        super.connectedCallback();
        await this._loadAccount();
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


    static styles = [
        accountStyles
    ]

    render() {
        if (this._loading) return html`<p>Cargando...</p>`;
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
                        <button class="show-btn">⊙ Mostrar</button>
                    </div>
                    <div>
                        <p class="card-number-label">Número de tarjeta</p>
                        <p class="card-number">${maskedNumber}</p>
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
                        <p class="info-row-value">${maskedAccount}</p>
                    </div>
                </div>
                <div class="account-type-badge">Cuenta Corriente Premium</div>
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