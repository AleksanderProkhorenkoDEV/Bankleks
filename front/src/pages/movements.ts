import { html, LitElement } from "lit";
import { customElement } from "lit/decorators.js";

@customElement("movements-page")
export class MovementsPage extends LitElement {
    render() {
        return html`
            <section>
                <h1>Movimientos</h1>
            </section>
        `
    }
}

declare global {
    interface HTMLElementTagNameMap {
        "movements-page": MovementsPage
    }
}