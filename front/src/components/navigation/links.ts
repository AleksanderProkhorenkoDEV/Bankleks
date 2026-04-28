import { customElement, property } from "lit/decorators.js";
import { linksStyles } from "./links.styles";
import { html, LitElement } from "lit";

@customElement("navigate-link")
export class NavigateLink extends LitElement {

    @property({ type: String }) href: string = '#'
    @property({ type: String }) variant: 'light' | 'dark' = 'light'

    handleRouteClick = (event: Event, route: string) => {
        event.preventDefault();
        this._navigate(route)
    }

    private _navigate = (href: String) => {
        this.dispatchEvent(new CustomEvent('navigate', {
            detail: { href },
            bubbles: true,
            composed: true
        }))
    }

    static styles = [
        linksStyles,
    ]

    render() {
        return html`
            <a 
                href=${this.href} 
                class=${this.variant}
                @click=${(e: Event) => this.handleRouteClick(e, this.href)}
            >
                <slot></slot>
            </a>
        `
    }
}