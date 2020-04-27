package es.achraf.deventer.viewmodel;

import android.content.SharedPreferences;
import android.os.Parcel;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.achraf.deventer.model.Plan;
import es.achraf.deventer.model.User;
import es.achraf.deventer.view.IView;

import static android.content.Context.MODE_PRIVATE;

public class ViewModel implements IViewModel {

    // Fields
    public static final String KSP_EMAIL = "email";
    public static final String KSP_PASSWORD = "password";

    private IView view;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private Plan planModel;
    private User userModel;

    // Constructors
    public ViewModel() {
        init();
    }

    // Getters
    /**
     * Devuelve el email del usuario.
     *
     * Debe cambiarse para que lo devuelva del modelo y no de las SharedPreferences.
     * @return
     */
    @Override
    public String getEmail() {
        return view.getPreferences(MODE_PRIVATE).getString(KSP_EMAIL, "");
    }

    @Override
    public String getPassword() {
        return view.getPreferences(MODE_PRIVATE).getString(KSP_PASSWORD, "");
    }

    // Setters
    @Override
    public void setView(IView view) {
        this.view = view;
    }

    // Methods
    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        userModel = new User();
        planModel = new Plan();
    }

    @Override
    public boolean isLogged() {
        return firebaseUser != null;
    }

    @Override
    public boolean emailSignIn(String email, String password, boolean saveBiometric) {
        email = email.trim();
        password = password.trim();

        if (saveBiometric) {
            saveBiometric(email, password);
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseUser = firebaseAuth.getCurrentUser();
                        }
                    }
                });

        return firebaseUser != null;
    }

    private void saveBiometric(String email, String password) {
        SharedPreferences sharedPreferences = view.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KSP_EMAIL, email);
        editor.putString(KSP_PASSWORD, password);
        editor.commit();
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
