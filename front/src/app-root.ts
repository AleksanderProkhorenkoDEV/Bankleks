import { customElement, property } from 'lit/decorators.js'
import { authRoutes, navBarRoutes } from './router/router'
import { LitElement, html } from 'lit'
import { appStyles } from './app.styles.css';
import { middleware } from './middleware';



@customElement('app-root')
export class AppRoot extends LitElement {

  @property()
  private _activeRoute: string = ""

  constructor() {
    super();
    this._updateUrl("/signIn")
  }

  private _handleNavigation = (e: Event) => {
    const event = e as CustomEvent;
    this._updateUrl(event.detail.href)
  }

  connectedCallback() {
    super.connectedCallback();
    this.addEventListener('navigate', this._handleNavigation);
  }

  disconnectedCallback() {
    super.disconnectedCallback();
    this.removeEventListener('navigate', this._handleNavigation);
  }

  private _updateUrl = (route: string) => {
    const href = middleware(route)
    history.pushState({}, '', href)
    this._activeRoute = href
  }

  static styles? = [
    appStyles,
  ]

  render() {
    const allRoutes = [...navBarRoutes, ...authRoutes]
    const route = allRoutes.find(item => item.href === this._activeRoute)

    return html`
      <nav-bar></nav-bar>
      <main>
        ${route ? route.component() : html`<p>404 not found</p>`}
      </main>
    `
  }

}

declare global {
  interface HTMLElementTagNameMap {
    'app-root': AppRoot
  }
}
