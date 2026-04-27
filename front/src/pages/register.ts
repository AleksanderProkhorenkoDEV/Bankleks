import { customElement } from "lit/decorators.js";
import { html, LitElement } from "lit";

@customElement("register-page")
export class RegisterPage extends LitElement {

    render() {
        return html`
            <layout-auth>
                <register-form></register-form>
            </layout-auth>
        `
    }
}

declare global {
    interface HTMLElementTagNameMap {
        "register-page": RegisterPage
    }
}