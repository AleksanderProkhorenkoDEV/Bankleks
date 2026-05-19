import type { TableColumn } from "../components/table/table";
import { customElement, state } from "lit/decorators.js";
import { deleteUser, getUsers } from "../services/user";
import { panelStyles } from "./styles/panel.styles";
import type { UserResponse } from "../types/user";
import { html, LitElement } from "lit";
import { getFailedTransfers, retryTransfer } from "../services/transaction";
import type { ScheduledTransactionResponse } from "../types/transactions";


const USER_COLUMNS: TableColumn[] = [
    { key: 'id', label: 'ID' },
    { key: 'name', label: 'Nombre' },
    { key: 'email', label: 'Email' },
    {
        key: 'role', label: 'Rol',
        render: (value) => {
            const map: Record<string, string> = {
                'ROLE_CLIENT': 'Cliente',
                'ROLE_ADMINISTRATOR': 'Administrador',
                'CLIENT': 'Cliente',
                'ADMINISTRATOR': 'Administrador',
            };
            return map[value] ?? value;
        }
    },
    { key: 'iban', label: 'Iban' },
    {
        key: 'balance', label: 'Balance',
        render: (value) => {
            return parseFloat(value).toFixed(3);
        }
    }
];

@customElement("panel-page")
export class PanelPage extends LitElement {

    @state() private _rows: UserResponse[] = [];
    @state() private _currentPage: number = 0;
    @state() private _totalPages: number = 0;
    @state() private _loading: boolean = true;

    @state() private _failedRows: ScheduledTransactionResponse[] = [];
    @state() private _failedCurrentPage: number = 0;
    @state() private _failedTotalPages: number = 0;
    @state() private _failedLoading: boolean = true;
    @state() private _retryingId: number | null = null;

    static styles = [panelStyles];

    async connectedCallback() {
        super.connectedCallback();
        await Promise.all([
            this._load(0),
            this._loadFailed(0)
        ]);
    }

    private async _load(page: number) {
        this._loading = true;
        const result = await getUsers(page);
        if (!result.ok || !result.data) return;
        this._rows = result.data.content;
        this._currentPage = result.data.currentPage;
        this._totalPages = result.data.totalPages;
        this._loading = false;
    }

    private async _loadFailed(page: number) {
        this._failedLoading = true;
        const result = await getFailedTransfers(page);
        if (!result.ok || !result.data) return;
        this._failedRows = result.data.content;
        this._failedCurrentPage = result.data.currentPage;
        this._failedTotalPages = result.data.totalPages;
        this._failedLoading = false;
    }

    private _handlePageChange = (e: CustomEvent) => {
        this._load(e.detail.page);
    }

    private _handleFailedPageChange = (e: CustomEvent) => {
        this._loadFailed(e.detail.page);
    }

    private _handleDelete = async (e: CustomEvent) => {
        const { id } = e.detail.row;
        const { ok } = await deleteUser(id);

        this.dispatchEvent(new CustomEvent('show-toast', {
            detail: {
                type: ok ? 'success' : 'error',
                message: ok ? 'Usuario eliminado.' : 'No se pudo eliminar el usuario.'
            },
            bubbles: true,
            composed: true
        }));

        if (ok) await this._load(this._currentPage);
    }

    private _handleRetry = async (id: number) => {
        this._retryingId = id;
        const { ok } = await retryTransfer(id);

        this.dispatchEvent(new CustomEvent('show-toast', {
            detail: {
                type: ok ? 'success' : 'error',
                message: ok ? 'Transferencia reintentada con éxito.' : 'No se pudo reintentar la transferencia.'
            },
            bubbles: true,
            composed: true
        }));

        this._retryingId = null;
        if (ok) await this._loadFailed(this._failedCurrentPage);
    }

    render() {
        const FAILED_COLUMNS: TableColumn[] = [
            { key: 'id', label: 'ID' },
            { key: 'concept', label: 'Concepto' },
            {
                key: 'amount', label: 'Cantidad',
                render: (value) => html`<span style="color: var(--color-danger); font-weight:600">-${parseFloat(value).toFixed(2)}€</span>`
            },
            {
                key: 'accountOrigin', label: 'Origen',
                render: (value) => value?.accountNumber ?? '-'
            },
            {
                key: 'accountDestination', label: 'Destino',
                render: (value) => value?.accountNumber ?? '-'
            },
            {
                key: 'scheduledAt', label: 'Fecha programada',
                render: (value: string) => new Date(value).toLocaleString('es-ES', {
                    day: '2-digit', month: '2-digit', year: 'numeric',
                    hour: '2-digit', minute: '2-digit'
                })
            },
            {
                key: 'recurrence', label: 'Recurrencia',
                render: (value) => {
                    const map: Record<string, string> = {
                        BEGINNING_OF_MONTH: 'Día 1',
                        MIDDLE_OF_MONTH: 'Día 15',
                        END_OF_MONTH: 'Fin de mes',
                    };
                    return value ? map[value] ?? value : 'Sin recurrencia';
                }
            },
            {
                key: 'id', label: 'Acción',
                render: (value: number) => html`
                <button-form
                    variant=${"danger"}
                    ?disabled=${this._retryingId !== null}
                    @click=${() => this._handleRetry(value)}
                >
                    ↺ ${this._retryingId === value ? 'Reintentando...' : 'Reintentar'}
                </button-form>
            `
            },
        ];

        return html`
        <section class="page">
            <h1>Panel de administración</h1>

            ${this._loading ? html`<loading-screen></loading-screen>` : html`
                <data-table
                    .hideEdit=${true}
                    .columns=${USER_COLUMNS}
                    .rows=${this._rows}
                    .currentPage=${this._currentPage}
                    .totalPages=${this._totalPages}
                    showActions
                    @page-change=${this._handlePageChange}
                    @row-delete=${this._handleDelete}
                ></data-table>
            `}

            <div class="section-header">
                <h2>Transferencias fallidas</h2>
                ${this._failedRows.length > 0 ? html`
                    <span class="badge-failed">${this._failedRows.length}</span>
                ` : ''}
            </div>

            ${this._failedLoading ? html`<loading-screen></loading-screen>` : html`
                ${this._failedRows.length === 0 ? html`
                    <p class="empty-state">No hay transferencias fallidas.</p>
                ` : html`
                    <data-table
                        .hideEdit=${true}
                        .hideDelete=${true}
                        .columns=${FAILED_COLUMNS}
                        .rows=${this._failedRows}
                        .currentPage=${this._failedCurrentPage}
                        .totalPages=${this._failedTotalPages}
                        @page-change=${this._handleFailedPageChange}
                    ></data-table>
                `}
            `}
        </section>
    `;
    }
}

declare global {
    interface HTMLElementTagNameMap {
        "panel-page": PanelPage
    }
}