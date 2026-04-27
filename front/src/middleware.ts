import { authRoutes, navBarRoutes } from "./router/router"
import { authService } from "./services/auth";

export const middleware = (href: string): string => {
    const allRoutes = [...navBarRoutes, ...authRoutes]
    const route = allRoutes.find(route => route.href === href)

    if (!route) return '/not-found';

    if (route.public) return href;

    const user = authService.getUser();
    if (!user) return '/signIn';

    if (route.roles && !route.roles.includes(user.role)) return '/not-found';

    return href;
}