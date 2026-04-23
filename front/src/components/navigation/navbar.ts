import { customElement } from 'lit/decorators.js';
import { LitElement, html } from 'lit';


@customElement("nav-bar")
export class NavBar extends LitElement {
    render() {
        return html`
            <header>
                <h1>Bankleks</h1>
                <nav>
                    <a>Elemento 1</a>
                    <a>Elemento 2</a>
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
