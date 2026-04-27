import type { RegisterBody, User } from "../types";
import type { RegisterResponse } from "../types/auth";
import { HttpService } from "./http";

class AuthService extends HttpService {
    private _user: User | null = null;

    getUser(): User | null {
        return this._user;
    }

    setUser(user: User) {
        this._user = user;
    }

    clear() {
        this._user = null;
    }


    async register(body: RegisterBody): Promise<{ ok: boolean, error?: string }> {
        console.log('BODY QUE ENVIAMOS', body);
        
        try {
            await this.post<RegisterResponse>('/auth/register', body);
            return { ok: true };
        } catch (error: any) {
            return { ok: false, error: error.message };
        }
    }
}

export const authService = new AuthService();
