package es.achraf.deventer.viewmodel;

import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.achraf.deventer.model.Plan;
import es.achraf.deventer.model.User;
import es.achraf.deventer.view.IView;

import static android.content.Context.MODE_PRIVATE;

public class ViewModel implements IViewModel, Parcelable {

    // Fields
    public static final String SPK_EMAIL = "email";
    public static final String SPK_PASSWORD = "password";

    private IView view;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private Plan planModel;
    private User userModel;

    // Constructors
    public ViewModel() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        userModel = new User();
        planModel = new Plan();
    }

    // Getters
    @Override
    public String getEmail() {

        return "";
    }

    @Override
    public String getPassword() {

        return "";
    }

    // Setters
    @Override
    public void setView(IView view) {
        this.view = view;
    }

    // Methods
    @Override
    public boolean emailSignIn(String email, String password, IViewModel.BIOMETRIC biometric) {
        email = email.trim();
        password = password.trim();

        if (biometric == BIOMETRIC.TRUE) {
            SharedPreferences sharedPreferences = view.getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SPK_EMAIL, email);
            editor.putString(SPK_PASSWORD, password);
            editor.commit();
        }

        return true;
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
