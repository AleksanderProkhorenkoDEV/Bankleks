import type { TransactionResponse } from "../types/transactions";
import type { TableColumn } from "../components/table/table";
import { movementsStyles } from "./styles/movements.styles";
import { getTransactions } from "../services/transaction";
import { customElement, state } from "lit/decorators.js";
import { html, LitElement } from "lit";




const COLUMNS: TableColumn[] = [
    { key: 'concept', label: 'Concepto' },
    {
        key: 'transactionType', label: 'Tipo',
        render: (value) => {
            const map: Record<string, string> = {
                DEPOSIT: 'Ingreso',
                WITHDRAWAL: 'Retirada',
                TRANSFER: 'Transferencia'
            };
            return map[value] ?? value;
        }
    },
    {
        key: 'amount', label: 'Cantidad',
        render: (value) => `${value.toFixed(2)}€`
    },
    {
        key: 'transactionDate', label: 'Fecha',  
        render: (value: string) => {
            const [year, month, day] = value.split('-').map(Number);
            const d = new Date(year, month - 1, day); // evita problemas de timezone
            return d.toLocaleDateString('es-ES');
        }
    },
];

@customElement("movements-page")
export class MovementsPage extends LitElement {

    @state() private _rows: TransactionResponse[] = [];
    @state() private _currentPage: number = 0;
    @state() private _totalPages: number = 0;
    @state() private _loading: boolean = true;

    static styles = [
        movementsStyles
    ];

    async connectedCallback() {
        super.connectedCallback();
        await this._load(0);
    }

    private async _load(page: number) {
        this._loading = true;
        const result = await getTransactions(page);

        if (!result.ok || !result.data) return;

        this._rows = result.data.content;
        this._currentPage = result.data.currentPage;
        this._totalPages = result.data.totalPages;
        this._loading = false;
    }

    private _handlePageChange(e: CustomEvent) {
        this._load(e.detail.page);
    }

    private _handleEdit(e: CustomEvent) {
        console.log('editar', e.detail.row);
        // TODO: abrir modal de edición
    }

    private _handleDelete(e: CustomEvent) {
        console.log('eliminar', e.detail.row);
        // TODO: confirmar y llamar al servicio
    }

    render() {
        return html`
            <section class="page">
                <h1>Movimientos</h1>

                ${this._loading
                ? html`<p>Cargando...</p>`
                : html`
                        <data-table
                            .columns=${COLUMNS}
                            .rows=${this._rows}
                            .currentPage=${this._currentPage}
                            .totalPages=${this._totalPages}
                            showActions
                            @page-change=${this._handlePageChange}
                            @row-edit=${this._handleEdit}
                            @row-delete=${this._handleDelete}
                        ></data-table>
                    `
            }
            </section>
        `;
    }
}

declare global {
    interface HTMLElementTagNameMap {
        "movements-page": MovementsPage
    }
}