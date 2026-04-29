import { html, LitElement } from "lit";
import { customElement } from "lit/decorators.js";

@customElement("signin-page")
export class SignInPage extends LitElement {
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