import type { InputForm } from '../components/forms/parts';
import { validate } from './validatior';

interface FieldConfig {
    value: string;
    validators: Array<(value: string) => string | null>;
    input: InputForm;
}

export const validateForm = (fields: Record<string, FieldConfig>): boolean => {
    let isValid = true;

    for (const field of Object.values(fields)) {
        const error = validate(field.value, field.validators);
        if (error) {
            field.input.setError(error);
            isValid = false;
        }
    }

    return isValid;
}