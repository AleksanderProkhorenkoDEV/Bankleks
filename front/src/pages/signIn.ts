import { html, LitElement } from "lit";
import { customElement } from "lit/decorators.js";

@customElement("signin-page")
export class SignInPage extends LitElement {
    render() {
        return html`
            <section>
                <h1>Inicio de sesión</h1>
            </section>
        `
    }
}

declare global {
    interface HTMLElementTagNameMap {
        "signin-page": SignInPage
    }
}