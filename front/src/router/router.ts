import { html } from "lit";
import type { Router } from "../types";

const navBarRoutes: Router[] = [
    {
        href: "/resumen",
        title: "Resumen",
        component: () => html`<resume-page></resume-page>`,
        public: false,
        roles: ["client"]
    },
    {
        href: "/movimientos",
        title: "Movimientos",
        component: () => html`<movements-page></movements-page>`,
        public: false,
        roles: ["client"]
    },
    {
        href: "/transacciones",
        title: "Transacciones",
        component: () => html`<transaction-page></transaction-page>`,
        public: false,
        roles: ["client"]
    },
    {
        href: "/panel",
        title: "Panel",
        component: () => html`<panel-page></panel-page>`,
        public: false,
        roles: ["admin"]
    }
]

const authRoutes: Router[] = [
    {
        href: "/signIn",
        title: "Iniciar sesión",
        component: () => html`<signin-page></signin-page>`,
        public: true,
    },
    {
        href: "/register",
        title: "Crear cuenta",
        component: () => html`<register-page></register-page>`,
        public: true
    }
]
