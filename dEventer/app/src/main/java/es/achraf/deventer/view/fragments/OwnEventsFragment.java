package es.achraf.deventer.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import es.achraf.deventer.R;
import es.achraf.deventer.interfaces.ItemClickListener;
import es.achraf.deventer.model.Event;
import es.achraf.deventer.view.adapters.RecyclerViewEventAdapter;

public class OwnEventsFragment extends Fragment implements ItemClickListener {

    // Fields
    private String key;
    private ArrayList<String> alKeys;
    private ArrayList<Event> alEvent = new ArrayList<>();

    private ProgressBar pbLoading;
    private TextView tvLoading;

    private RecyclerView rcvOwnEvents;
    private RecyclerViewEventAdapter adapterMisPlanes;

    // Methods

    /**
     * Primer método ejecutado por el fragmento. Inicializa los elementos del fragmento.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState es el bundle que almacena los datos del estado del fragmento
     *                           cuando se produce un cambio como rotaciones de la pantalla.
     * @return la vista creada.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_own_events, container, false);
        init(view);

        return view;
    }

    /**
     * Inicializa los elemtnos del fragmento.
     *
     * @param view es la vista sobre la que se carga el fragmento.
     */
    private void init(View view) {
        rcvOwnEvents = view.findViewById(R.id.rcvOwnEvents);

        pbLoading = view.findViewById(R.id.pbLoading);
        tvLoading = view.findViewById(R.id.tvLoading);

        loadingMessage(true);
    }

    /**
     * Muestra o hace invisibles —GONE— distintos elementos de la actividad relacionados con la
     * espera e invocación de una nueva actividad.
     *
     * @param loading indica si los elementos deben desaparecer o verse.
     *                <p>
     *                - True -> Deben verse
     *                - False -> No deben verse
     */
    private void loadingMessage(boolean loading) {
        if (loading) {
            pbLoading.setVisibility(View.VISIBLE);
            tvLoading.setVisibility(View.VISIBLE);
        } else {
            pbLoading.setVisibility(View.GONE);
            tvLoading.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(View view, int pos) {

    }
}
