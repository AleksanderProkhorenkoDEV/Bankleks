import { customElement } from "lit/decorators.js";
import { notFoundStyles } from "./404.styles";
import { html, LitElement} from "lit";

@customElement("not-found-page")
export class NotFoundPage extends LitElement {

    static styles = [
        notFoundStyles,
    ]

    render() {
        return html`
            <div class="container">
                <p class="code">404</p>
                <h1 class="title">Página no encontrada</h1>
                <p class="description">La ruta a la que intentas acceder no existe.</p>
                <navigate-link href="/resumen">Volver al inicio</navigate-link>
            </div>
        `;
    }
}

declare global {
    interface HTMLElementTagNameMap {
        "not-found-page": NotFoundPage
    }
}