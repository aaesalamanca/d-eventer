package es.achraf.deventer.viewmodel;

import es.achraf.deventer.view.IView;

public class ViewModelChat implements IViewModel.GetEvents {

    // Fields
    private IView.GetEventsListener getEventsListener;

    // Getters

    /**
     * Solicita leer los eventos a la base de datos.
     */
    @Override
    public void getEvents() {

    }

    // Setters

    /**
     * Establece el listener que escuchará la petición de leer los eventos de la base de
     * datos.
     *
     * @param getEventsListener es el listener de la petición de lectura de los eventos de la
     *                          base de datos.
     */
    @Override
    public void setGetEventsListener(IView.GetEventsListener getEventsListener) {
        this.getEventsListener = getEventsListener;
    }
}
