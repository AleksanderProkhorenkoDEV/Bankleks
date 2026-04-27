interface User {
    id: string,
    name: string,
    role: 'client' | 'admin'
}

class AuthService {
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
}

export const authService = new AuthService();
