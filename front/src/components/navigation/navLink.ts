import { html, LitElement } from "lit";
import { customElement, property } from "lit/decorators.js";
import { navLinkStyles } from "./navLink.styles";

@customElement("nav-link")
export class NavLink extends LitElement {

    @property() href = '';
    @property() title = '';

    private _handleClick = (e: Event) => {
        e.preventDefault();
        window.dispatchEvent(new CustomEvent('navigate', {
            detail: { href: this.href }
        }));
    }

    static styles? = [
        navLinkStyles,
    ]

    render() {
        return html`
            <a 
                href=${this.href} 
                class=${this.href == "/panel" ? "panel" : ""}
                @click=${this._handleClick}
            >
                ${this.title}
            </a>
        `
    }
}

declare global {
    interface HTMLElementTagNameMap {
        "nav-link": NavLink
    }
}