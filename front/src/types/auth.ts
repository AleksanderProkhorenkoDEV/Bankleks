export interface User {
    id: number,
    name: string,
    role: 'CLIENT' | 'ADMINISTRATOR'
    iban: string,
}

export interface ServiceResponse<T = void> {
    ok: boolean;
    data?: T,
    error?: string;
}

export interface RegisterBody {
    name: string;
    email: string;
    password: string;
}


export interface RegisterResponse {
    message: string;
    status: number;
}

export interface SignInBody {
    email: string,
    password: string
}

export interface SignInResponse {
    id: number,
    userName: string;
    token: string;
    rol: 'CLIENT' | 'ADMINISTRATOR';
    iban: string;
}