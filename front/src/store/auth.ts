import type { User } from "../types";

type Listener = () => void;

/**
 *  Simulamos un contexto como podría ser zustan, al que suscribimos nuestra 
 *  aplicación así, podemos acceder al usuario etc. En cualquier lado de la aplicación.
 *  Se intento hacer con el patrón singleton, pero si tenias un error de importación
 *  javascript cargaba otro modulo y se perdía la información.
 * 
 */
function createStore<T>(initialState: T) {
    let _state = initialState;
    const _listeners = new Set<Listener>();

    return {
        getState: (): T => _state,

        setState: (partial: Partial<T>) => {
            _state = { ..._state, ...partial };
            _listeners.forEach(listener => listener());
        },

        subscribe: (listener: Listener): (() => void) => {
            _listeners.add(listener);
            return () => _listeners.delete(listener);
        }
    }
}

export const authStore = createStore({
    user: null as User | null,
    jwt: null as string | null,
});