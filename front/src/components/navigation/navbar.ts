import { customElement } from 'lit/decorators.js';
import { LitElement, html } from 'lit';
import { navBarRoutes } from '../../router/router';
import { navbarStyles } from './navbar.styles';


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

    static styles? = [
        navbarStyles,
    ]

    render() {
        return html`
            <header>
                <h1>Bankleks</h1>
                <nav>
                    ${navBarRoutes.map((item) => {
                        return html`
                            <nav-link 
                                .href=${item.href}
                                .title=${item.title}
                                @click=${(event: Event) => this.handleRouteClick(event, item.href)}
                            >
                            </nav-link>
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
