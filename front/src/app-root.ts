import { customElement, property } from 'lit/decorators.js'
import { authRoutes, navBarRoutes } from './router/router'
import { LitElement, html } from 'lit'
import { appStyles } from './app.styles.css';



@customElement('app-root')
export class AppRoot extends LitElement {


  @property()
  private _activeRoute: string = ""

  constructor() {
    super();
    this._updateUrl("/resumen")
  }

  private __handleNavigation = (event: CustomEvent) => {
    const route = event.detail.href
    this._updateUrl(route)
  }

  private _updateUrl = (route: string) => {
    history.pushState({}, '', route)
    this._activeRoute = route
  }

  static styles? = [
    appStyles,
  ]

  render() {
    const allRoutes = [...navBarRoutes, ...authRoutes]
    const route = allRoutes.find(item => item.href === this._activeRoute)

    return html`
      <nav-bar @navigate=${this.__handleNavigation}></nav-bar>
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
