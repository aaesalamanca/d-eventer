package es.achraf.deventer.view;

import android.content.SharedPreferences;
import android.net.Uri;

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
         * Handler que ejecuta la acción requerida según el resultado de intentar crear un user.
         *
         * @param signedUp es el resultado del intento de creación de un user.
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

    interface GetProfileListener {
        /**
         * Handler que ejecuta la acción requerida cuando se han obtenido los datos del usuario
         * procedentes de la base de datos.
         */
        void onGetProfileComplete();
    }

    interface GetImageListener {
        /**
         * Handler que ejecuta la acción requerida cuando se ha obtenido la imagel de la base de
         * datos
         *
         * @param cloudUri es la Uri de la imagen en la base de datos.
         * @param isChange indica si se ha actualizado o no la imagen:
         *                 <p>
         *                 True -> Se ha actualizado
         *                 False -> No se ha actualizado
         */
        void onImageUploaded(Uri cloudUri, boolean isChange);
    }

    interface SignOutListener {
        /**
         * Handler que ejecuta la acción requerida cuando el user cierra la sesión.
         */
        void onSignOutComplete();
    }
}
