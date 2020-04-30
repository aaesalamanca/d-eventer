package es.achraf.deventer.viewmodel;

import com.google.firebase.auth.FirebaseAuth;

import es.achraf.deventer.view.IView;

public class ViewModelSignUp implements IViewModel.SignUp {

    // Fields
    private IView.SignUpCompleteListener signUpCompleteListener;

    // Setters

    @Override
    public void setSignUpCompleteListener(IView.SignUpCompleteListener signUpCompleteListener) {
        this.signUpCompleteListener = signUpCompleteListener;
    }

    // Methods

    /**
     * Solicita crear un usuario con email y contraseña.
     * <p>
     * https://firebase.google.com/docs/auth/android/start#sign_up_new_users
     *
     * @param email    es el email del usuario.
     * @param password es la contraseña del usuario.
     */
    @Override
    public void emailSignUp(String email, String password) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,
                password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                signUpCompleteListener.onSignUpComplete(true);
            } else {
                signUpCompleteListener.onSignUpComplete(false);
            }
        });
    }
}
