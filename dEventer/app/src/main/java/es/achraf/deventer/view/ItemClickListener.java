package es.achraf.deventer.view;

import android.view.View;

public interface ItemClickListener {

    /**
     * Handler que ejecuta la acción requerida cuando se pulsa un ítem.
     *
     * @param view es la vista.
     * @param pos  es la posición del ítem.
     */
    void onItemClick(View view, int pos);
}
