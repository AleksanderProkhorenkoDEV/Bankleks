import type { TemplateResult } from "lit";

export interface Router {
    href: string,
    title: string,
    component: () => TemplateResult,
    public?: boolean,
    roles?: string[]
}