import { html, LitElement } from "lit";
import { customElement, property } from "lit/decorators.js";
import { navLinkStyles } from "./navLink.styles";

@customElement("nav-link")
export class NavLink extends LitElement {

    @property() href = '';
    @property() title = '';

    static styles? = [
        navLinkStyles,
    ]

    render() {
        return html`
            <a href=${this.href} class=${ this.href == "/panel" ? "panel" : ""}>${this.title}</a>
        `
    }
}

declare global {
    interface HTMLElementTagNameMap {
        "nav-link": NavLink
    }
}