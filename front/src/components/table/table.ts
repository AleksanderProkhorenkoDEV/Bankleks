import { html, LitElement, nothing } from "lit";
import { customElement, property } from "lit/decorators.js";
import { tableStyles } from "./table.styles";
import { authStore } from "../../store/auth";

export interface TableColumn {
    key: string;
    label: string;
    render?: (value: any, row: any) => any;
}

/**
 * @component DataTable
 * @element data-table
 *
 * Componente de tabla genérica y reutilizable con soporte para paginación,
 * renderizado personalizado de celdas y acciones por fila.
 *
 * ---
 *
 * ## Uso básico
 *
 * ```html
 * <data-table
 *   .columns=${columns}
 *   .rows=${rows}
 *   .currentPage=${0}
 *   .totalPages=${5}
 * ></data-table>
 * ```
 *
 * ---
 *
 * ## Propiedades
 *
 * | Propiedad     | Tipo            | Default | Descripción                                      |
 * |---------------|-----------------|---------|--------------------------------------------------|
 * | columns       | TableColumn[]   | []      | Definición de columnas. Ver interfaz TableColumn |
 * | rows          | any[]           | []      | Datos a mostrar. Cada objeto es una fila         |
 * | currentPage   | number          | 0       | Página actual (0-based)                          |
 * | totalPages    | number          | 0       | Total de páginas disponibles                     |
 * | showActions   | boolean         | false   | Muestra columna de acciones (editar/eliminar)    |
 *
 * ---
 *
 * ## Interfaz TableColumn
 *
 * ```typescript
 * interface TableColumn {
 *   key: string;                              // nombre del campo en el objeto row
 *   label: string;                            // texto que aparece en la cabecera
 *   render?: (value: any, row: any) => any;   // función opcional para personalizar el contenido de la celda
 * }
 * ```
 *
 * ---
 *
 * ## Columnas con render personalizado
 *
 * El campo `render` recibe el valor de la celda y la fila completa,
 * permitiendo transformar el contenido o devolver un TemplateResult de Lit:
 *
 * ```typescript
 * const columns: TableColumn[] = [
 *   { key: 'concept', label: 'Concepto' },
 *   {
 *     key: 'amount',
 *     label: 'Cantidad',
 *     render: (value) => `${value.toFixed(2)}€`
 *   },
 *   {
 *     key: 'transactionType',
 *     label: 'Tipo',
 *     render: (value, row) => {
 *       // puedes acceder a cualquier campo del row completo
 *       const sent = row.originAccount?.accountNumber === userIban;
 *       return html`<span style="color: red">${sent ? 'Enviado' : 'Recibido'}</span>`;
 *     }
 *   },
 * ];
 * ```
 *
 * ---
 *
 * ## Eventos
 *
 * El componente emite eventos con `bubbles: true` y `composed: true`,
 * lo que permite escucharlos desde cualquier ancestro aunque haya Shadow DOM de por medio.
 *
 * | Evento       | Detail           | Descripción                                      |
 * |--------------|------------------|--------------------------------------------------|
 * | page-change  | { page: number } | Se emite al pulsar los botones de paginación     |
 * | row-edit     | { row: any }     | Se emite al pulsar el botón de editar una fila   |
 * | row-delete   | { row: any }     | Se emite al pulsar el botón de eliminar una fila |
 *
 * ```typescript
 * // Escuchar desde el componente padre
 * html`
 *   <data-table
 *     @page-change=${(e) => this._loadPage(e.detail.page)}
 *     @row-edit=${(e) => this._openEditModal(e.detail.row)}
 *     @row-delete=${(e) => this._confirmDelete(e.detail.row)}
 *   ></data-table>
 * `
 * ```
 *
 * ---
 *
 * ## Acciones y roles
 *
 * Cuando `showActions` está activo, siempre se muestra el botón de editar.
 * El botón de eliminar solo aparece si el usuario autenticado tiene el rol `ADMINISTRATOR`.
 * Esto se obtiene automáticamente del store de autenticación — no necesitas pasarlo como prop.
 *
 * ---
 *
 * ## Paginación
 *
 * La paginación es controlada externamente — el componente no gestiona
 * el estado de la página ni hace peticiones. Solo emite el evento `page-change`
 * con la página solicitada y el padre es responsable de cargar los datos.
 *
 * ```typescript
 * private _handlePageChange(e: CustomEvent) {
 *   this._loadData(e.detail.page); // llama a tu servicio con la nueva página
 * }
 * ```
 *
 * ---
 *
 * ## Ejemplo completo
 *
 * ```typescript
 * import { DataTable, TableColumn } from './components/table/data-table.component';
 *
 * const columns: TableColumn[] = [
 *   { key: 'concept',         label: 'Concepto' },
 *   { key: 'transactionType', label: 'Tipo' },
 *   {
 *     key: 'amount',
 *     label: 'Cantidad',
 *     render: (value) => `${value.toFixed(2)}€`
 *   },
 *   {
 *     key: 'transactionDate',
 *     label: 'Fecha',
 *     render: (value: string) => {
 *       const [y, m, d] = value.split('-').map(Number);
 *       return new Date(y, m - 1, d).toLocaleDateString('es-ES');
 *     }
 *   },
 * ];
 *
 * // En el render del componente padre:
 * html`
 *   <data-table
 *     .columns=${columns}
 *     .rows=${this._rows}
 *     .currentPage=${this._currentPage}
 *     .totalPages=${this._totalPages}
 *     showActions
 *     @page-change=${this._handlePageChange}
 *     @row-edit=${this._handleEdit}
 *     @row-delete=${this._handleDelete}
 *   ></data-table>
 * `
 * ```
 */
@customElement("data-table")
export class DataTable extends LitElement {

    @property({ type: Array }) columns: TableColumn[] = [];
    @property({ type: Array }) rows: any[] = [];
    @property({ type: Number }) currentPage: number = 0;
    @property({ type: Number }) totalPages: number = 0;
    @property({ type: Boolean }) showActions: boolean = false;
    @property({ type: Boolean }) hideEdit: boolean = false;

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

        const { user } = authStore.getState()

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
                                                ${this.hideEdit ? nothing : html`<button-form type=${"button"} variant="update" @click=${() => this._emitEdit(row)}>
                                                    ✎
                                                </button-form>`
                        }
                                                ${user?.role === "ADMINISTRATOR" ? html`<button-form type="button" variant="danger" @click=${() => this._emitDelete(row)}>
                                                    🗑
                                                </button-form>` : nothing
                        }
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