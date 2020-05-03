package es.achraf.deventer.view;

import android.content.SharedPreferences;

public interface IView {

    // Interfaces

    interface GetPreferencesListener {
        /**
         * Obtiene las SharedPreferences de la Activity.
         *
         * @param mode es el modo de acceso a las SharedPreferences.
         * @return las SharedPreferences según mode.
         */
        SharedPreferences getPreferences(int mode);
    }

    interface SignUpCompleteListener {
        /**
         * Handler que ejecuta la acción requerida según el resultado de intentar crear un usuario.
         *
         * @param signedUp es el resultado del intento de creación de un usuario.
         *                 <p>
         *                 - True -> Usuario creado con éxito
         *                 - False -> Usuario no creado
         */
        void onSignUpComplete(boolean signedUp);
    }

    interface SignInCompleteListener {
        /**
         * Handler que ejecuta la acción requerida según el resultado del intento de inicio de sesión.
         *
         * @param signedIn es el resultado del intento de inicio de sesión.
         *                 <p>
         *                 - True -> Inicio de sesión con éxito
         *                 - False -> Inicio de sesión fracasado
         */
        void onSignInComplete(boolean signedIn);
    }

    interface SignOutListener {
        /**
         * Handler que ejecuta la acción requerida cuando el usuario cierra la sesión.
         */
        void onSignOutComplete();
    }
}
