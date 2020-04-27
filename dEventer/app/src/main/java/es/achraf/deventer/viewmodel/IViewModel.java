package es.achraf.deventer.viewmodel;

import es.achraf.deventer.view.IView;

public interface IViewModel {
    // Fields
    enum BIOMETRIC {
        TRUE, FALSE
    };

    // Getters
    String getEmail();
    String getPassword();

    // Setters
    void setView(IView view);

    // Methods
    boolean emailSignIn(String email, String password, BIOMETRIC biometric);
}
