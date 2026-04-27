import { validateForm } from "../../utils/form.validation";
import { isEmail, required } from "../../utils/validatior";
import { customElement, query } from "lit/decorators.js";
import type { SignInBody } from "../../types/auth";
import { baseStyles } from "./base.styles";
import type { InputForm } from "./parts";
import { html, LitElement } from "lit";


/**
 * El formulario no lo refactorizo, porque al tener shadow-dowm y multiples eventos
 * no estaba siendo capaz de capturarlo bien. Preferi usar estilos bases y crear el tag
 * form
 */
@customElement("signin-form")
export class SignInForm extends LitElement {


    @query('input-form[name="email"]') _emailInput!: InputForm;
    @query('input-form[name="password"]') _passwordInput!: InputForm;

    private _formData: SignInBody = { email: "", password: "" };



    private _handleInputChange = (e: CustomEvent) => {
        const key = e.detail.name as keyof SignInBody;
        this._formData[key] = e.detail.value;
    }

    private _handleSubmit = async (e: SubmitEvent) => {
        e.preventDefault()

        const isValid = validateForm({
            email: { value: this._formData.email ?? '', validators: [required(), isEmail()], input: this._emailInput },
            password: { value: this._formData.password ?? '', validators: [required()], input: this._passwordInput },
        });

        if (!isValid) return;

        //TODO: implement toast message
    }

    static styles = [
        baseStyles,
    ]


    render() {
        return html`
            <form @submit=${this._handleSubmit}>
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
                <navigate-link .href=${"/register"} .variant=${"dark"}>
                    ¿No tiene cuenta? Create una
                </navigate-link>
                <button-form variant=${"primary"} type=${"submit"}>Iniciar sesión</button-form>
            </form>
        `
    }
}

declare global {
    interface HTMLElementTagNameMap {
        "signin-form": SignInForm
    }
}