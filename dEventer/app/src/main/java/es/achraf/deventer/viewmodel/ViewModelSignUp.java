package es.achraf.deventer.viewmodel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import es.achraf.deventer.model.User;
import es.achraf.deventer.view.IView;

public class ViewModelSignUp implements IViewModel.SignUp {

    // Fields
    private IView.SignUpCompleteListener signUpCompleteListener;

    // Setters

    /**
     * Establece el Listener que escuchará el intento de crear un user.
     *
     * @param signUpCompleteListener es el Listener del intento de inicio de sesión.
     */
    @Override
    public void setSignUpCompleteListener(IView.SignUpCompleteListener signUpCompleteListener) {
        this.signUpCompleteListener = signUpCompleteListener;
    }

    // Methods

    /**
     * Solicita crear un user con email y contraseña.
     * <p>
     * https://firebase.google.com/docs/auth/android/start#sign_up_new_users
     *
     * @param email      es el email del user.
     * @param password   es la contraseña del user.
     * @param name       es el nombre del user.
     * @param age        es la age del user.
     * @param postalCode es el código postal del user.
     */
    @Override
    public void emailSignUp(String email, String password, String name, String age,
                            String sex, String postalCode) {
        email = email.trim();
        password = password.trim();
        name = name.trim();
        age = age.trim();
        sex = sex.trim();
        postalCode = postalCode.trim();

        User user = new User();
        user.setName(name);
        user.setAge(age);
        user.setSex(sex);
        user.setPostalCode(postalCode);

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,
                password).addOnCompleteListener(taskUserCreated -> {
            if (taskUserCreated.isSuccessful()) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase.getInstance().getReference().child(IViewModel.USERS)
                        .child(firebaseUser.getUid())
                        .setValue(user);

                signUpCompleteListener.onSignUpComplete(true);
            } else {
                signUpCompleteListener.onSignUpComplete(false);
            }
        });
    }
}
