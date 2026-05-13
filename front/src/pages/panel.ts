import type { TableColumn } from "../components/table/table";
import { customElement, state } from "lit/decorators.js";
import { deleteUser, getUsers } from "../services/user";
import { panelStyles } from "./styles/panel.styles";
import type { UserResponse } from "../types/user";
import { html, LitElement } from "lit";


const COLUMNS: TableColumn[] = [
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

    static styles = [panelStyles];

    async connectedCallback() {
        super.connectedCallback();
        await this._load(0);
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

    private _handlePageChange = (e: CustomEvent) => {
        this._load(e.detail.page);
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

    render() {
        return html`
            <section class="page">
                <h1>Panel de administración</h1>

                ${this._loading ? html`<loading-screen></loading-screen>` : html`
                    <data-table
                        .hideEdit=${true}
                        .columns=${COLUMNS}
                        .rows=${this._rows}
                        .currentPage=${this._currentPage}
                        .totalPages=${this._totalPages}
                        showActions
                        @page-change=${this._handlePageChange}
                        @row-delete=${this._handleDelete}
                    ></data-table>
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