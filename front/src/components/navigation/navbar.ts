import { navBarRoutes } from '../../router/router';
import { customElement } from 'lit/decorators.js';
import { navbarStyles } from './navbar.styles';
import { LitElement, html } from 'lit';


@customElement("nav-bar")
export class NavBar extends LitElement {

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
