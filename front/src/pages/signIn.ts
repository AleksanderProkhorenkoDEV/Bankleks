import { html, LitElement } from "lit";
import { customElement } from "lit/decorators.js";

@customElement("signin-page")
export class SignInPage extends LitElement {
    render() {
        return html`
            <layout-auth>
                <signin-form></signin-form>
            </layout-auth>
        `
    }
}

declare global {
    interface HTMLElementTagNameMap {
        "signin-page": SignInPage
    }
}