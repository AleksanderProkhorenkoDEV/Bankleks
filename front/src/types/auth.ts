export interface User {
    id: string,
    name: string,
    role: 'client' | 'admin'
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