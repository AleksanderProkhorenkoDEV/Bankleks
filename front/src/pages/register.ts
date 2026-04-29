import { customElement } from "lit/decorators.js";
import { html, LitElement } from "lit";

@customElement("register-page")
export class RegisterPage extends LitElement {

    render() {
        return html`
            <layout-auth>
                <img 
                    src="/candado.png" 
                    width="350px" 
                    alt="candado representativo de autorización"
                    loading="lazy"
                    slot="image"
                />
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