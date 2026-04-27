export interface User {
    name: string,
    role: 'ROLE_CLIENT' | 'ROLE_ADMINISTRATOR'
}

export interface ServiceResponse {
    ok: boolean;
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
    userName: string;
    token: string;
    rol: 'ROLE_CLIENT' | 'ROLE_ADMINISTRATOR';
}