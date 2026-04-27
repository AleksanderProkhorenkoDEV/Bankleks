import { customElement } from "lit/decorators.js";
import { html, LitElement } from "lit";
import { registerStyles } from "./register.styles";

@customElement("register-page")
export class RegisterPage extends LitElement {


    static styles? = [
        registerStyles,
    ]

    render() {
        return html`
            <section class="register">
                <article class="register__item">
                    <img 
                        src="/candado.png" 
                        width="350px" 
                        alt="candado representativo de autorización"
                        loading="lazy"
                    />
                </article>
                <article class="register__item">
                    <register-form></register-form>
                </article>
            </section>
        `
    }
}

declare global {
    interface HTMLElementTagNameMap {
        "register-page": RegisterPage
    }
}