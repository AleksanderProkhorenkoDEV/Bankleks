import { html, LitElement } from "lit";
import { customElement } from "lit/decorators.js";

@customElement("panel-page")
export class PanelPage extends LitElement {
    render() {
        return html`
            <section>
                <h1>Panel privado</h1>
            </section>
        `
    }
}

declare global {
    interface HTMLElementTagNameMap {
        "panel-page": PanelPage
    }
}