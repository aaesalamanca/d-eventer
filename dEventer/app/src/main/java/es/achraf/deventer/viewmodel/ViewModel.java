package es.achraf.deventer.viewmodel;

import android.content.SharedPreferences;
import android.os.Parcel;


import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import es.achraf.deventer.model.Plan;
import es.achraf.deventer.model.User;
import es.achraf.deventer.view.IView;

import static android.content.Context.MODE_PRIVATE;

public class ViewModel implements IViewModel {

    // Fields

    // Clave para la obtención del email en las SharedPreferences
    private static final String KSP_EMAIL = "email";
    // Clave para la obtención de la contraseña en las SharedPreferences
    private static final String KSP_PASSWORD = "password";

    private IView view; // View para seguir el patrón MVVM

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private Plan planModel;
    private User userModel;

    // Constructors

    /**
     * Constructor por defecto —vacío—.
     */
    public ViewModel() {
        init();
    }

    // Getters

    /**
     * Devuelve el email del usuario.
     * <p>
     * Debe cambiarse la implementación para que lo devuelva del modelo y no de las
     * SharedPreferences.
     * <p>
     * https://developer.android.com/training/data-storage/shared-preferences#ReadSharedPreference
     *
     * @return el email del usuario. Cadena vacía si no se ha guardado con anterioridad o no se
     * tiene acceso a la vista.
     */
    @Override
    public String getEmail() {
        if (view != null) {
            return view.getPreferences(MODE_PRIVATE).getString(KSP_EMAIL, "");
        }

        return "";
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
        if (view != null) {
            return view.getPreferences(MODE_PRIVATE).getString(KSP_PASSWORD, "");
        }

        return "";
    }

    // Setters

    /**
     * Establece la vista actual del ViewModel.
     *
     * @param view vista con la que va a trabajar el ViewModel.
     */
    @Override
    public void setView(IView view) {
        this.view = view;
    }

    // Methods

    /**
     * Inicializa el objeto.
     */
    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        userModel = new User();
        planModel = new Plan();
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
        SharedPreferences sharedPreferences = view.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KSP_EMAIL, email);
        editor.putString(KSP_PASSWORD, password);
        editor.commit();
    }

    /**
     * Devuelve si el usuario ha iniciado o no sesión.
     * <p>
     * https://firebase.google.com/docs/auth/android/password-auth#sign_in_a_user_with_an_email_address_and_password
     *
     * @return true si ya ha iniciado sesión, false si no.
     */
    @Override
    public boolean isSignedIn() {
        return firebaseUser != null;
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

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (view != null) {
                        if (task.isSuccessful()) {
                            firebaseUser = firebaseAuth.getCurrentUser();
                            view.onSignInComplete(true);
                        } else {
                            view.onSignInComplete(false);
                        }
                    }
                });
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
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (view != null) {
                        if (task.isSuccessful()) {
                            firebaseUser = firebaseAuth.getCurrentUser();
                            view.onSignInComplete(true);
                        } else {
                            view.onSignInComplete(false);
                        }
                    }
                });
    }

    /**
     * Cierra la sesión actual.
     */
    @Override
    public void signOut() {
        if (view != null) {
            view.onSignOutComplete();
        }

        firebaseAuth.signOut();
    }

    // Parcelable implementation

    protected ViewModel(Parcel in) {
    }

    public static final Creator<ViewModel> CREATOR = new Creator<ViewModel>() {
        @Override
        public ViewModel createFromParcel(Parcel in) {
            return new ViewModel(in);
        }

        @Override
        public ViewModel[] newArray(int size) {
            return new ViewModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
