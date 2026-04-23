import { customElement } from 'lit/decorators.js'
import { LitElement, html } from 'lit'



@customElement('app-root')
export class AppRoot extends LitElement {

  render() {
    return html`
      <main>
        <nav-bar></nav-bar>
      </main>
    `
  }

}

declare global {
  interface HTMLElementTagNameMap {
    'app-root': AppRoot
  }
}
