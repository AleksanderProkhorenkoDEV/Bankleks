/**
 * En las peticiones en caso de que las respuestas no sean buenas, es decir,
 * no sean del rango 200 al 299 se lanza una excepción con la respuesta. 
 * 
 * Por que así la capturamos, y mostramoas el mensaje de error, en caso contrario retornamos
 * el response normal del back-end.
 */
export class HttpService {

    private _baseUrl: string = import.meta.env.VITE_API_URL;

    protected async get<T>(endpoint: string): Promise<T> {
        const response = await fetch(`${this._baseUrl}${endpoint}`, {
            credentials: 'include'
        });

        if (!response.ok) throw await response.json();
        return response.json();
    }

    protected async post<T>(endpoint: string, body: unknown): Promise<T> {
        console.log('BASE URL', this._baseUrl);
        console.log('ENDPOINT', endpoint);

        const response = await fetch(`${this._baseUrl}${endpoint}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify(body)
        });
        console.log('RESPONSE', response);

        if (!response.ok) throw await response.json();
        return response.json();
    }

    protected async patch<T>(endpoint: string, body: unknown): Promise<T> {
        const response = await fetch(`${this._baseUrl}${endpoint}`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify(body)
        });

        if (!response.ok) throw await response.json();
        return response.json();
    }

    protected async delete<T>(endpoint: string): Promise<T> {
        const response = await fetch(`${this._baseUrl}${endpoint}`, {
            method: 'DELETE',
            credentials: 'include',
        });

        if (!response.ok) throw await response.json();
        return response.json();
    }
}