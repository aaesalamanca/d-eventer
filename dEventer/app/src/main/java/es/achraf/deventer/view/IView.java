package es.achraf.deventer.view;

import android.content.SharedPreferences;

public interface IView {

    // Methods

    /**
     * Handler que ejecuta la acción requerida según el resultado del intento de inicio de sesión.
     * <p>
     *
     * @param signedIn es el resultado del intento de inicio de sesión.
     */
    void onSignInComplete(boolean signedIn);

    /**
     * Obtiene las SharedPreferences de la Activity.
     *
     * @param mode es el modo de acceso a las SharedPreferences.
     * @return las SharedPreferences según mode.
     */
    SharedPreferences getPreferences(int mode);
}
