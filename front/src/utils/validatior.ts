type Validator = (value: string) => string | null;

export const required = (): Validator => (value) => value.trim() === '' ? 'Este campo es obligatorio' : null;

export const minLength = (min: number): Validator => (value) => value.length < min ? `Mínimo ${min} caracteres` : null;

export const isEmail = (): Validator => (value) => !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value) ? 'Email no válido' : null;

export const validate = (value: string, validators: Validator[]): string => {
    for (const validator of validators) {
        const error = validator(value);
        if (error) return error;
    }
    return '';
};