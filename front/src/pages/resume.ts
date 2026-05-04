import { customElement, state } from "lit/decorators.js";
import { getAccountStats } from "../services/account";
import type { AccountStats } from "../types/account";
import { resumePage } from "./styles/resume.styles";
import { html, LitElement } from "lit";

@customElement("resume-page")
export class ResumePage extends LitElement {

    @state() private _stats: AccountStats | null = null;
    @state() private _loading: boolean = true;
    @state() private _error: string = '';

    static styles = [
        resumePage
    ]

    async connectedCallback() {
        super.connectedCallback();
        await this._loadStats();
    }

    private async _loadStats() {
        this._loading = true;
        const result = await getAccountStats();

        if (!result.ok || !result.data) {
            this._error = result.error ?? 'Error al cargar las estadísticas';
            this._loading = false;
            return;
        }

        this._stats = result.data;
        this._loading = false;
    }

    render() {
        return html`
            <section class="page">
                <h1>Resumen</h1>

                ${this._loading ? html`
                    <div class="loading">Cargando estadísticas...</div>
                ` : this._error ? html`
                    <div class="error">${this._error}</div>
                ` : html`
                    <div class="charts-grid">
                        <div class="chart-card">
                            <h3 class="chart-title">Ingresos vs Gastos</h3>
                            <donut-chart
                                .income=${this._stats?.totalIncome ?? 0}
                                .expense=${this._stats?.totalExpense ?? 0}
                            ></donut-chart>
                        </div>

                        <div class="chart-card">
                            <h3 class="chart-title">Evolución del balance</h3>
                            <line-chart
                                .points=${this._stats?.balancePointDTO ?? []}
                            ></line-chart>  
                        </div>
                    </div>
                `}
            </section>
        `;
    }
}

declare global {
    interface HTMLElementTagNameMap {
        "resume-page": ResumePage
    }
}