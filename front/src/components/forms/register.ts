import { isEmail, minLength, required } from "../../utils/validatior";
import { validateForm } from "../../utils/form.validation";
import { customElement, query } from "lit/decorators.js";
import { authService } from "../../services/auth";
import type { RegisterBody } from "../../types";
import { baseStyles } from "./base.styles";
import type { InputForm } from "./parts";
import { html, LitElement } from "lit";

@customElement("register-form")
export class RegisterForm extends LitElement {

    /**
     * Usamos query, para obtener el input y posteriormente en caso de existir 
     * un error, le modicamos el error message con su propiedad set.
     */
    @query('input-form[name="name"]') _nameInput!: InputForm
    @query('input-form[name="email"]') _emailInput!: InputForm;
    @query('input-form[name="password"]') _passwordInput!: InputForm;

    private _formData: RegisterBody = { email: "", name: "", password: "" };

    private _handleInputChange = (e: CustomEvent) => {
        const key = e.detail.name as keyof RegisterBody;
        this._formData[key] = e.detail.value;
    }

    private _handleSubmit = async (e: SubmitEvent) => {
        console.log('ENTRAMOS EN EL SUBMIT');
        
        e.preventDefault()
        
        const isValid = validateForm({
            name: { value: this._formData.name ?? '', validators: [required(), minLength(4)], input: this._nameInput },
            email: { value: this._formData.email ?? '', validators: [required(), isEmail()], input: this._emailInput },
            password: { value: this._formData.password ?? '', validators: [required()], input: this._passwordInput },
        });
        console.log('ES VALIDO?', isValid);
        
        if (!isValid) return;

        const { ok, error } = await authService.register(this._formData);
        console.log(ok, 'USUARIO CREADO');
        console.log(error, 'ALGO VA MAL');        
    }

    static styles = [
        baseStyles,
    ]

    /**
     * Pasamos las propiedades como atributos sin hacer binding, para que vivan en el DOM
     * así podemos acceder a ellas con el query y modificar lo mensajes de error.
    */
    render() {
        return html`
            <form @submit=${this._handleSubmit}>
                <input-form
                    name="name"
                    label="Introduce tu nombre y apellidos"
                    type="text"
                    placeholder="Emilio García"
                    @input-change=${this._handleInputChange}

                >
                </input-form>
                <input-form
                    name="email"
                    label="Introduce tu email"
                    type="email"
                    placeholder="emilio@gmail.com"
                    @input-change=${this._handleInputChange}

                >
                </input-form>
                <input-form
                    name="password"
                    label="Introduce tu contraseña"
                    type="password"
                    placeholder=""
                    @input-change=${this._handleInputChange}

                >
                </input-form>
                <button-form variant=${"primary"} type=${"submit"}>Registrarse</button-form>
            </form>
        `
    }
}

declare global {
    interface HTMLElementTagNameMap {
        "register-form": RegisterForm
    }
}