import { customElement } from 'lit/decorators.js';
import { LitElement, html } from 'lit';
import { navBarRoutes } from '../../router/router';


@customElement("nav-bar")
export class NavBar extends LitElement {


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

    render() {
        return html`
            <header>
                <h1>Bankleks</h1>
                <nav>
                    ${navBarRoutes.map((item) => {
                        return html`
                            <a href=${item.href} @click=${(event: Event) => this.handleRouteClick(event, item.href)}>${item.title}</a>
                        `
                    })}
                </nav>
            </header>
        `
    }
}

declare global {
    interface HTMLElementTagNameMap {
        'nav-bar': NavBar
    }
}
