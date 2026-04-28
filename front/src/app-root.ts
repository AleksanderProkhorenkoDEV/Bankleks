import { customElement, property, state } from 'lit/decorators.js'
import { authRoutes, navBarRoutes } from './router/router'
import { LitElement, html } from 'lit'
import { appStyles } from './app.styles.css';
import { middleware } from './middleware';
import { authStore } from './store/auth';



@customElement('app-root')
export class AppRoot extends LitElement {

  @property() private _activeRoute: string = ""

  @state() private _unsuscribeAuth: (() => void) | null = null
  @state() private showToast: boolean = false
  @state() private toastType: "success" | "error" = "success"
  @state() private toastMessage: string = ""


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

    window.addEventListener('navigate', this._handleNavigation);
    window.addEventListener('session-expired', () => {
      this._updateUrl('/signIn');
    });

    this.addEventListener('show-toast', this._handleToast as EventListener)

    const unsubscribe = authStore.subscribe(() => {
      this.requestUpdate();
    });

    this._unsuscribeAuth = unsubscribe;
  }

  disconnectedCallback() {
    super.disconnectedCallback();
    window.removeEventListener('navigate', this._handleNavigation);
    this._unsuscribeAuth?.();
    this.removeEventListener('show-toast', this._handleToast as EventListener);

  }

  private _handleToast(e: CustomEvent) {
    const { message, type } = e.detail;

    this.toastMessage = message;
    this.toastType = type;
    this.showToast = true;

    setTimeout(() => {
      this.showToast = false;
    }, 4000);
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

        ${this.showToast
              ? html`
            <toast-message 
              variant=${this.toastType} 
              .content=${this.toastMessage}
            ></toast-message>
          `
          : ''
        }
      </main>
    `
  }

}

declare global {
  interface HTMLElementTagNameMap {
    'app-root': AppRoot
  }
}
