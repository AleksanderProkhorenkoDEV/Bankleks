import { authRoutes, navBarRoutes } from '../../router/router';
import { customElement, state } from 'lit/decorators.js';
import { navbarStyles } from './navbar.styles';
import { signOut } from '../../services/auth';
import { authStore } from '../../store/auth';
import { LitElement, html } from 'lit';


@customElement("nav-bar")
export class NavBar extends LitElement {

    @state() private _unsubscribe?: () => void

    static styles? = [
        navbarStyles,
    ]


    connectedCallback(): void {
        super.connectedCallback()

        this._unsubscribe = authStore.subscribe(() => {
            this.requestUpdate();
        })
    }

    disconnectedCallback() {
        super.disconnectedCallback();
        this._unsubscribe?.();
    }

    private _handleLogout = async () => {
        const { ok, error } = await signOut();
        console.log(ok, error);
        window.dispatchEvent(new CustomEvent('navigate', {
            detail: { href: '/signIn' }
        }));
    }

    render() {

        const { user } = authStore.getState()

        const allRoutes = [...navBarRoutes, ...authRoutes]
        const visibleRoutes = allRoutes.filter(route => {
            // si hay usuario, no mostrar rutas públicas
            if (user && route.public) return false;
            // si no hay usuario, no mostrar rutas privadas
            if (!user && !route.public) return false;
            // filtrar por rol
            if (route.roles && !route.roles.some(role => role === user?.role)) return false;

            return true;
        });
       

        return html`
            <header>
                <h1>Bankleks</h1>
                <nav>
                    ${visibleRoutes.map((item) => {
            return html`
                            <nav-link 
                                .href=${item.href}
                                .title=${item.title}
                            >
                            </nav-link>
                        `
        })}
                    ${user && html`<button-form 
                                        .type=${"button"} 
                                        .variant=${"danger"}
                                        @click=${this._handleLogout}
                                    >
                                        Cerrar sesión
                                    </button-form>`}
                </nav>
            </header>
        `
    }
}

declare global {
    interface HTMLElementTagNameMap {
        'nav-bar': NavBar
    }
}
