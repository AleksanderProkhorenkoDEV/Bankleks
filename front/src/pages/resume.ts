import { html, LitElement } from "lit";
import { customElement } from "lit/decorators.js";

@customElement("resume-page")
export class ResumePage extends LitElement {
    render() {
        return html`
            <section>
                <h1>Resume page</h1>
            </section>
        `
    }
}

declare global {
    interface HTMLElementTagNameMap {
        "resume-page": ResumePage
    }
}