import { customElement, property } from "lit/decorators.js";
import { errorStyle } from "./error.style";
import { html, LitElement } from "lit";

@customElement("error-screen")
export class ErrorScreen extends LitElement {

    @property({ type: String }) message: string = 'Algo salió mal, inténtalo de nuevo.';

    static styles = [
        errorStyle
    ]

    private _handleRetry() {
        window.location.reload();
    }

    render() {
        return html`
            <div class="container">
                <div class="icon">⚠️</div>
                <h1 class="title">Algo salió mal</h1>
                <p class="message">${this.message}</p>
                <div class="actions">
                    <button-form variant="primary" type="button" @click=${this._handleRetry}>
                        Reintentar
                    </button-form>
                    <navigate-link href="/resumen">Volver al inicio</navigate-link>
                </div>
            </div>
        `;
    }
}

declare global {
    interface HTMLElementTagNameMap {
        "error-screen": ErrorScreen
    }
}