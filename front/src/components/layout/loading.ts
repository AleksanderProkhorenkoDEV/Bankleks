import { customElement } from "lit/decorators.js";
import { loadingStyles } from "./loading.styles";
import { html, LitElement } from "lit";

@customElement("loading-screen")
export class LoadingScreen extends LitElement {

    static styles = [
        loadingStyles
    ]

    render() {
        return html`
            <div class="container">
                <div class="spinner"></div>
                <span class="text">Cargando...</span>
            </div>
        `;
    }
}

declare global {
    interface HTMLElementTagNameMap {
        "loading-screen": LoadingScreen
    }
}