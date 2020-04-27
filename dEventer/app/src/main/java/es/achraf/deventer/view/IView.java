package es.achraf.deventer.view;

import android.content.SharedPreferences;

public interface IView {

    // Methods
    void onSignInComplete(boolean signedIn);

    SharedPreferences getPreferences(int mode);
}
