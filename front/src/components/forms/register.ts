import { isEmail, minLength, required, validate } from "../../utils/validatior";
import { customElement, query } from "lit/decorators.js";
import { html, LitElement } from "lit";
import type { InputForm } from "./parts";
import { baseStyles } from "./base.styles";

@customElement("register-form")
export class RegisterForm extends LitElement {

    /**
     * Usamos query, para obtener el input y posteriormente en caso de existir 
     * un error, le modicamos el error message con su propiedad set.
     */
    @query('input-form[name="name"]') _nameInput!: InputForm
    @query('input-form[name="email"]') _emailInput!: InputForm;
    @query('input-form[name="password"]') _passwordInput!: InputForm;

    private _formData: Record<string, string> = {};

    private _handleInputChange = (e: CustomEvent) => {
        this._formData[e.detail.name] = e.detail.value
    }

    private _handleSubmit = (e: SubmitEvent) => {
        e.preventDefault()
        let isValid = true;

        const nameError = validate(this._formData.name ?? '', [required(), minLength(4)])
        const emailError = validate(this._formData.email ?? '', [required(), isEmail()])
        const passwordError = validate(this._formData.password ?? '', [required()])

        if (nameError) {
            this._nameInput.setError(nameError)
            isValid = false
        }
        if (emailError) {
            this._emailInput.setError(emailError)
            isValid = false
        }
        if (passwordError) {
            this._passwordInput.setError(passwordError)
            isValid = false
        }

        if (!isValid) return

        //TODO añadir llamada al back
        alert("Todo es válido")

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