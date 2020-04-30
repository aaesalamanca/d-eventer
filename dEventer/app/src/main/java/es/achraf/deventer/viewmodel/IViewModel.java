package es.achraf.deventer.viewmodel;

import android.os.Parcelable;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import es.achraf.deventer.view.IView;

public interface IViewModel extends Parcelable {

    // Fields

    String K_VIEWMODEL = "viewModel"; // Clave para el paso del ViewModel entre actividades.

    // Getters

    /**
     * Devuelve el email del usuario.
     * <p>
     *
     * @return el email del usuario.
     */
    String getEmail();

    /**
     * Devuelve la contaseña del usuario.
     *
     * @return la contraseña del usuario.
     */
    String getPassword();

    // Setters

    /**
     * Establece la vista actual del ViewModel.
     *
     * @param view vista con la que va a trabajar el ViewModel.
     */
    void setView(IView view);

    // Methods

    /**
     * Devuelve si el usuario ha iniciado o no sesión.
     *
     * @return true si ya ha iniciado sesión, false si no.
     */
    boolean isSignedIn();

    /**
     * Solicita crear un usuario con email y contraseña.
     *
     * @param email    es el email del usuario.
     * @param password es la contraseña del usuario.
     */
    void emailSignUp(String email, String password);

    /**
     * Solicita iniciar sesión con email y contraseña. También los guarda en SharedPreferences si
     * el usuario marca la opción correspondiente.
     *
     * @param email         es el email del usuario.
     * @param password      es la contraseña del usuario.
     * @param saveBiometric indica si el usuario quiere guardar el email y la contraseña como
     *                      SharedPreferences para poder utilizar la huella:
     *                      <p>
     *                      - Sí -> True
     *                      - No -> False
     */
    void emailSignIn(String email, String password, boolean saveBiometric);

    /**
     * Solicita iniciar sesión con cuenta de Google.
     *
     * @param account es la cuenta de Google.
     */
    void googleSignIn(GoogleSignInAccount account);

    /**
     * Cierra la sesión actual.
     */
    void signOut();
}
