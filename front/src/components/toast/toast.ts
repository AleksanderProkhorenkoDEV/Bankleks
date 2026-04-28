import { customElement, property } from "lit/decorators.js";
import { html, LitElement } from "lit";
import { toastStyles } from "./toast.styles";

@customElement("toast-message")
export class ToastMessage extends LitElement {

    @property({ type: String }) content: string = "default content of toast message"
    @property() variant: 'success' | 'error' = 'success';


    static styles = [
        toastStyles
    ]

    render() {
        return html`
            <div class="container">
                <p class="content">${this.content}</p>
            </div>
        `
    }
}

declare global {
    interface HTMLElementTagNameMap {
        "toast-message": ToastMessage
    }
}