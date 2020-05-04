package es.achraf.deventer.viewmodel;

import com.google.firebase.auth.FirebaseAuth;

import es.achraf.deventer.view.IView;

public class ViewModelHome implements IViewModel.SignOut {

    // Fields
    private IView.SignOutListener signOutListener;

    // Setters

    /**
     * Establece el Listener que escuchará el intento de cerrar la sesión actual.
     *
     * @param signOutListener
     */
    @Override
    public void setSignOutListener(IView.SignOutListener signOutListener) {
        this.signOutListener = signOutListener;
    }

    // Methods

    /**
     * Cierra la sesión actual.
     */
    @Override
    public void signOut() {
        FirebaseAuth.getInstance().signOut();

        signOutListener.onSignOutComplete();
    }
}
