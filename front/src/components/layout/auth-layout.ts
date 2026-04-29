import { html, LitElement } from "lit";
import { customElement } from "lit/decorators.js";
import { authLayoutStyles } from "./auth-layout.styles";

@customElement("layout-auth")
export class LayoutAuth extends LitElement {

    static styles = [
        authLayoutStyles
    ]

    render() {
        return html`
            <section class="auth">
                <article class="auth__item">
                    <slot name="image"></slot>
                </article>
                <article class="auth__item">
                    <slot></slot>
                </article>
            </section>
        `
    }
}

declare global {
    interface HTMLElementTagNameMap {
        "layout-auth": LayoutAuth
    }
}