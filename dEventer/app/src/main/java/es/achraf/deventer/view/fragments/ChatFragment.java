package es.achraf.deventer.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import es.achraf.deventer.ChatActivity;
import es.achraf.deventer.R;
import es.achraf.deventer.view.IView;
import es.achraf.deventer.view.ItemClickListener;
import es.achraf.deventer.model.Event;

public class ChatFragment extends Fragment implements ItemClickListener {

    // Fields
    private ArrayList<Event> alEvent = new ArrayList<>();

    private ProgressBar pbLoading;
    private TextView tvLoading;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        init(view);

        return view;
    }

    /**
     * Inicializa los elemtnos del fragmento.
     *
     * @param view es la vista sobre la que se carga el fragmento.
     */
    private void init(View view) {

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

    /**
     * Handler que ejecuta la acción requerida cuando se hace click en un ítem del
     * RecyclerView.
     *
     * @param view es la vista en la que se ha hecho click.
     * @param pos  es la posición del elemento en el que se ha hecho click.
     */
    @Override
    public void onItemClick(View view, int pos) {
        Event event = alEvent.get(pos);

        Bundle eventBundle = new Bundle();
        eventBundle.putParcelable(IView.EVENT, event);

        Intent intentChat = new Intent(getActivity(), ChatActivity.class);
        intentChat.putExtras(eventBundle);
        startActivity(intentChat);
    }
}
