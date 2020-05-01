package es.achraf.deventer.viewmodel;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import es.achraf.deventer.view.IView;

public interface IViewModel {

    // Interfaces
    interface GetEmail {
        /**
         * Devuelve el email del usuario.
         * <p>
         *
         * @return el email del usuario.
         */
        String getEmail();
    }

    interface GetPassword {
        /**
         * Devuelve la contaseña del usuario.
         *
         * @return la contraseña del usuario.
         */
        String getPassword();
    }

    interface GetDisplayName {
        /**
         * Devuelve el nombre del usuario.
         *
         * @return el nombre del usuario.
         */
        String getDisplayName();
    }

    interface SetGetPreferencesListener {
        /**
         * Establece el Listener que escuchará la petición de las SharedPreferences
         *
         * @param getPreferencesListener es el Listener de la petición de las SharedPreferences.
         */
        void setGetPreferencesListener(IView.GetPreferencesListener getPreferencesListener);
    }

    interface SignUp {
        void setSignUpCompleteListener(IView.SignUpCompleteListener signUpCompleteListener);

        /**
         * Solicita crear un usuario con email y contraseña.
         *
         * @param email    es el email del usuario.
         * @param password es la contraseña del usuario.
         */
        void emailSignUp(String email, String password);
    }

    interface SignIn {
        /**
         * Establece el Listener que escuchará el resultado del intento de inicio de sesión.
         *
         * @param signInCompleteListener es el Listener del intento de inicio de sesión.
         */
        void setSignInCompleteListener(IView.SignInCompleteListener signInCompleteListener);

        /**
         * Devuelve si el usuario ha iniciado o no sesión.
         *
         * @return true si ya ha iniciado sesión, false si no.
         */
        boolean isSignedIn();

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
    }

    interface SignOut {
        /**
         * Establece el Listener que escuchará el intento de cerrar la sesión actual.
         *
         * @param signOutListener
         */
        void setSignOutListener(IView.SignOutListener signOutListener);

        /**
         * Cierra la sesión actual.
         */
        void signOut();
    }
}
