type Validator = (value: string) => string | null;

export const required = (): Validator => (value) => value.trim() === '' ? 'Este campo es obligatorio' : null;

export const minLength = (min: number): Validator => (value) => value.length < min ? `Mínimo ${min} caracteres` : null;

export const isPositive = (): Validator => (value) => parseFloat(value) <= 0 ? 'Debe ser mayor que 0' : null;

export const isIBAN = (): Validator => (value) => {
    const clean = value.replace(/\s/g, '').toUpperCase();
    return /^[A-Z]{2}\d{2}[A-Z0-9]{4}\d{7}([A-Z0-9]?){0,16}$/.test(clean)
        ? null
        : 'IBAN no válido';
}

export const isEmail = (): Validator => (value) => !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value) ? 'Email no válido' : null;

export const validate = (value: string, validators: Validator[]): string => {
    for (const validator of validators) {
        const error = validator(value);
        if (error) return error;
    }
    return '';
};