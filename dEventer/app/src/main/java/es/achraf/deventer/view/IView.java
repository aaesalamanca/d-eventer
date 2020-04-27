package es.achraf.deventer.view;

import android.content.SharedPreferences;

public interface IView {
    SharedPreferences getPreferences(int mode);
}
