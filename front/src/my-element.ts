import { customElement } from 'lit/decorators.js'
import { LitElement, css, html } from 'lit'



@customElement('my-element')
export class MyElement extends LitElement {



}

declare global {
  interface HTMLElementTagNameMap {
    'my-element': MyElement
  }
}
