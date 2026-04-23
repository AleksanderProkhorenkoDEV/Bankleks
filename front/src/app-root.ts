import { customElement, property } from 'lit/decorators.js'
import { LitElement, html } from 'lit'
import { authRoutes, navBarRoutes } from './router/router'



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
