package es.achraf.deventer.viewmodel;

import android.os.Parcelable;

import es.achraf.deventer.view.IView;

public interface IViewModel extends Parcelable {

    // Fields
    String K_VIEWMODEL = "viewModel";

    // Getters
    String getEmail();
    String getPassword();

    // Setters
    void setView(IView view);

    // Methods
    boolean isLogged();

    boolean emailSignIn(String email, String password, boolean biometricSave);
}
