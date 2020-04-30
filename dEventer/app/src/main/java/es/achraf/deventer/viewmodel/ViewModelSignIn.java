package es.achraf.deventer.viewmodel;

import android.content.SharedPreferences;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import es.achraf.deventer.view.IView;

import static android.content.Context.MODE_PRIVATE;

public class ViewModelSignIn implements IViewModel.SetGetPreferencesListener,
        IViewModel.GetEmail, IViewModel.GetPassword, IViewModel.GetDisplayName, IViewModel.SignIn {

    // Fields

    // Clave para la obtención del email en las SharedPreferences
    private static final String KSP_EMAIL = "email";
    // Clave para la obtención de la contraseña en las SharedPreferences
    private static final String KSP_PASSWORD = "password";

    private IView.SignInCompleteListener signInCompleteListener;
    private IView.GetPreferencesListener getPreferencesListener;

    // Getters

    /**
     * Devuelve el email del usuario.
     * <p>
     * https://developer.android.com/training/data-storage/shared-preferences#ReadSharedPreference
     *
     * @return el email del usuario. Cadena vacía si no se ha guardado con anterioridad o no se
     * tiene acceso a la vista.
     */
    @Override
    public String getEmail() {
        return getPreferencesListener.getPreferences(MODE_PRIVATE)
                .getString(KSP_EMAIL, "");
    }

    /**
     * Devuelve la contaseña del usuario.
     * <p>
     * https://developer.android.com/training/data-storage/shared-preferences#ReadSharedPreference
     *
     * @return la contraseña del usuario. Cadena vacía si no se ha guardado con anterioridad o no
     * se tiene acceso a la vista.
     */
    @Override
    public String getPassword() {
        return getPreferencesListener.getPreferences(MODE_PRIVATE)
                .getString(KSP_PASSWORD, "");
    }

    @Override
    public String getDisplayName() {
        return FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
    }

    // Setters

    /**
     * Establece el Listener que escuchará el resultado del intento de inicio de sesión.
     *
     * @param signInCompleteListener es el Listener del intento de inicio de sesión.
     */
    @Override
    public void setSignInCompleteListener(IView.SignInCompleteListener signInCompleteListener) {
        this.signInCompleteListener = signInCompleteListener;
    }

    /**
     * Establece el Listener que escuchará la petición de las SharedPreferences
     *
     * @param getPreferencesListener es el Listener de la petición de las SharedPreferences.
     */
    @Override
    public void setGetPreferencesListener(IView.GetPreferencesListener getPreferencesListener) {
        this.getPreferencesListener = getPreferencesListener;
    }

    // Methods

    /**
     * Devuelve si el usuario ha iniciado o no sesión.
     * <p>
     * https://firebase.google.com/docs/auth/android/password-auth#sign_in_a_user_with_an_email_address_and_password
     *
     * @return true si ya ha iniciado sesión, false si no.
     */
    @Override
    public boolean isSignedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    /**
     * Solicita iniciar sesión con email y contraseña. También los guarda en SharedPreferences si
     * el usuario marca la opción correspondiente.
     * <p>
     * https://firebase.google.com/docs/auth/android/password-auth#sign_in_a_user_with_an_email_address_and_password
     *
     * @param email         es el email del usuario.
     * @param password      es la contraseña del usuario.
     * @param saveBiometric indica si el usuario quiere guardar el email y la contraseña como
     *                      SharedPreferences para poder utilizar la huella:
     *                      <p>
     *                      - Sí -> True
     *                      - No -> False
     */
    @Override
    public void emailSignIn(String email, String password, boolean saveBiometric) {
        email = email.trim();
        password = password.trim();

        if (saveBiometric) {
            saveBiometric(email, password);
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        signInCompleteListener.onSignInComplete(true);
                    } else {
                        signInCompleteListener.onSignInComplete(false);
                    }
                });
    }

    /**
     * Guarda el email y la contraseña del usuario en SharedPreferences.
     * <p>
     * https://developer.android.com/training/data-storage/shared-preferences#WriteSharedPreference
     *
     * @param email    es el email del usuario.
     * @param password es la contraseña del usuario.
     */
    private void saveBiometric(String email, String password) {
        SharedPreferences sharedPreferences = getPreferencesListener.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KSP_EMAIL, email);
        editor.putString(KSP_PASSWORD, password);
        editor.apply();
    }

    /**
     * Solicita iniciar sesión con cuenta de Google.
     * <p>
     * https://firebase.google.com/docs/auth/android/google-signin#authenticate_with_firebase
     * https://developers.google.com/identity/sign-in/android/sign-in#configure_google_sign-in_and_the_googlesigninclient_object
     *
     * @param account es la cuenta de Google.
     */
    @Override
    public void googleSignIn(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),
                null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        signInCompleteListener.onSignInComplete(true);
                    } else {
                        signInCompleteListener.onSignInComplete(false);
                    }
                });
    }
}
