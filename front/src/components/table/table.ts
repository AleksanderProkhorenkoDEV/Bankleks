import { html, LitElement, nothing } from "lit";
import { customElement, property } from "lit/decorators.js";
import { tableStyles } from "./table.styles";

export interface TableColumn {
    key: string;
    label: string;
    render?: (value: any, row: any) => any;
}


@customElement("data-table")
export class DataTable extends LitElement {

    @property({ type: Array }) columns: TableColumn[] = [];
    @property({ type: Array }) rows: any[] = [];
    @property({ type: Number }) currentPage: number = 0;
    @property({ type: Number }) totalPages: number = 0;
    @property({ type: Boolean }) showActions: boolean = false;

    static styles = [
        tableStyles
    ];

    private _emitPage(page: number) {
        this.dispatchEvent(new CustomEvent('page-change', {
            detail: { page },
            bubbles: true,
            composed: true
        }));
    }

    private _emitEdit(row: any) {
        this.dispatchEvent(new CustomEvent('row-edit', {
            detail: { row },
            bubbles: true,
            composed: true
        }));
    }

    private _emitDelete(row: any) {
        this.dispatchEvent(new CustomEvent('row-delete', {
            detail: { row },
            bubbles: true,
            composed: true
        }));
    }

    render() {
        return html`
            <div class="table-wrapper">
                <table>
                    <thead>
                        <tr>
                            ${this.columns.map(col => html`<th>${col.label}</th>`)}
                            ${this.showActions ? html`<th>Acciones</th>` : nothing}
                        </tr>
                    </thead>
                    <tbody>
                        ${this.rows.length === 0
                ? html`<tr><td class="empty" colspan=${this.columns.length + 1}>Sin resultados</td></tr>`
                : this.rows.map(row => html`
                                <tr>
                                    ${this.columns.map(col => html`
                                        <td>
                                            ${col.render ? col.render(row[col.key], row) : row[col.key]}
                                        </td>
                                    `)}
                                    ${this.showActions ? html`
                                        <td>
                                            <div class="actions">
                                                <button class="btn-action btn-edit" @click=${() => this._emitEdit(row)}>
                                                    ✎
                                                </button>
                                                <button class="btn-action btn-delete" @click=${() => this._emitDelete(row)}>
                                                    🗑
                                                </button>
                                            </div>
                                        </td>
                                    ` : nothing}
                                </tr>
                            `)
            }
                    </tbody>
                </table>
            </div>

            <div class="pagination">
                <button
                    ?disabled=${this.currentPage === 0}
                    @click=${() => this._emitPage(this.currentPage - 1)}
                >←</button>
                <span>Pág ${(this.currentPage ?? 0) + 1} de ${this.totalPages ?? 0}</span>
                <button
                    ?disabled=${this.currentPage >= this.totalPages - 1}
                    @click=${() => this._emitPage(this.currentPage + 1)}
                >→</button>
            </div>
        `;
    }
}

declare global {
    interface HTMLElementTagNameMap {
        "data-table": DataTable
    }
}