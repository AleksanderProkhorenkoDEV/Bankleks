import { html, LitElement } from "lit";
import { customElement } from "lit/decorators.js";

@customElement("transaction-page")
export class TransactionPage extends LitElement {
    render() {
        return html`
            <layout-auth>
                <img 
                    src="/transacciones.png" 
                    width="350px" 
                    alt="candado representativo de autorización"
                    loading="lazy"
                    slot="image"
                />
                <transaction-form></transaction-form>
            </layout-auth>
        `
    }
}

declare global {
    interface HTMLElementTagNameMap {
        "transaction-page": TransactionPage
    }
}