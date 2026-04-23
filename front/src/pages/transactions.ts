import { html, LitElement } from "lit";
import { customElement } from "lit/decorators.js";

@customElement("transaction-page")
export class TransactionPage extends LitElement {
    render() {
        return html`
            <section>
                <h1>Transaciones</h1>
            </section>
        `
    }
}

declare global {
    interface HTMLElementTagNameMap {
        "transaction-page": TransactionPage
    }
}