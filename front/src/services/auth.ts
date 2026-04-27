import type { RegisterBody, User } from "../types";
import type { RegisterResponse, ServiceResponse, SignInBody, SignInResponse } from "../types/auth";
import { HttpService } from "./http";

class AuthService extends HttpService {
    private _user: User | null = null;
    private _jwt: string | null = null;
    private _refreshTimer: number | null = null;


    getUser(): User | null {
        return this._user;
    }

    setUser(user: User) {
        this._user = user;
    }

    getJwt(): string | null {
        return this._jwt;
    }

    clear() {
        this._user = null;
    }

    private _startRefreshTimer(): void {
        this._stopRefreshTimer();

        this._refreshTimer = window.setInterval(async () => {
            await this._refresh();
        }, 14 * 60 * 1000);
    }

    private _stopRefreshTimer(): void {
        if (this._refreshTimer) {
            clearInterval(this._refreshTimer);
            this._refreshTimer = null;
        }
    }


    private async _refresh(): Promise<void> {
        try {
            const data = await this.post<{ token: string }>('/auth/refresh', {});
            this._jwt = data.token;
        } catch {
            this._jwt = null;
            this._user = null;
            this._stopRefreshTimer();
            window.dispatchEvent(new CustomEvent('session-expired'));
        }
    }


    async register(body: RegisterBody): Promise<{ ok: boolean, error?: string }> {
        try {
            await this.post<RegisterResponse>('/auth/register', body);
            return { ok: true };
        } catch (error: any) {
            return { ok: false, error: error.message };
        }
    }

    async signIn(body: SignInBody): Promise<ServiceResponse> {
        try {
            const data = await this.post<SignInResponse>('/auth/login', body);

            this._jwt = data.token;
            this._user = {
                name: data.userName,
                role: data.rol
            };

            console.log(this._user);

            this._startRefreshTimer();

            return { ok: true };
        } catch (error) {
            const message = error instanceof Error ? error.message : 'Error desconocido';
            return { ok: false, error: message };
        }
    }


    async initialize(): Promise<void> {
        try {
            const data = await this.post<SignInResponse>('/auth/refresh', {});
            this._jwt = data.token;
            this._user = { name: data.userName, role: data.rol };
            this._startRefreshTimer();
        } catch {
            this._jwt = null;
            this._user = null;
        }
    }
}

export const authService = new AuthService();
