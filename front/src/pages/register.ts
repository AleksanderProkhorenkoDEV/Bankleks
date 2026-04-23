import { customElement } from "lit/decorators.js";
import { html, LitElement } from "lit";

@customElement("register-page")
export class RegisterPage extends LitElement {
    render() {
        return html`
            <section>
                <h1>Register page</h1>
            </section>
        `
    }
}

declare global {
    interface HTMLElementTagNameMap {
        "register-page": RegisterPage
    }
}