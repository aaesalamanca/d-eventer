package es.achraf.deventer.view.fragments;

import android.app.Dialog;
import android.net.Uri;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import es.achraf.deventer.R;
import es.achraf.deventer.interfaces.ItemClickListener;
import es.achraf.deventer.model.Event;
import es.achraf.deventer.view.adapters.RecyclerViewEventAdapter;
import es.achraf.deventer.viewmodel.ViewModelOwnEvents;

public class OwnEventsFragment extends Fragment implements ItemClickListener {

    // Fields
    private ViewModelOwnEvents vmoe;

    private String key;
    private ArrayList<String> alKeys;
    private ArrayList<Event> alEvent = new ArrayList<>();

    private ProgressBar pbLoading;
    private TextView tvLoading;

    private RecyclerView rcvOwnEvents;
    private RecyclerViewEventAdapter adapterMisPlanes;

    private Dialog viewEventDialog;

    private CircleImageView civUser;
    private TextView tvUser;

    private CircleImageView civViewEvent;
    private TextView tvName;
    private MaterialButton mbtnJoin;

    private TextView tvDate;
    private TextView tvTime;
    private TextView tvLocation;
    private TextView tvPrice;
    private TextView tvJoined;
    private TextView tvDescription;

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
        vmoe = new ViewModelOwnEvents();
        vmoe.setGetEventsListener((alKeys, alEvent) -> {
            this.alKeys = alKeys;
            this.alEvent = alEvent;


        });

        rcvOwnEvents = view.findViewById(R.id.rcvOwnEvents);

        pbLoading = view.findViewById(R.id.pbLoading);
        tvLoading = view.findViewById(R.id.tvLoading);

        loadViewEventDialog();

        loadingMessage(true);
    }

    /**
     * Carga el diálogo para ver un evento.
     */
    private void loadViewEventDialog() {
        viewEventDialog = new Dialog(getContext(), R.style.full_screen_dialog);
        viewEventDialog.setContentView(R.layout.dialog_view_event);

        civUser = viewEventDialog.findViewById(R.id.civUser);
        tvUser = viewEventDialog.findViewById(R.id.tvUser);

        civViewEvent = viewEventDialog.findViewById(R.id.civEvent);
        tvName = viewEventDialog.findViewById(R.id.tvName);
        mbtnJoin = viewEventDialog.findViewById(R.id.mbtnJoin);
        mbtnJoin.setOnClickListener(v -> {
            if (mbtnJoin.getText().equals(getString(R.string.join_event))) {

            } else {

            }
        });

        tvDate = viewEventDialog.findViewById(R.id.tvDate);
        tvTime = viewEventDialog.findViewById(R.id.tvTime);
        tvLocation = viewEventDialog.findViewById(R.id.tvLocation);
        tvPrice = viewEventDialog.findViewById(R.id.tvPrice);
        tvJoined = viewEventDialog.findViewById(R.id.tvJoined);
        tvDescription = viewEventDialog.findViewById(R.id.tvDescription);
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
        key = alKeys.get(pos);
        Event event = alEvent.get(pos);

        Glide.with(getContext()).load(Uri.parse(event.getImageUri())).error(R.mipmap.logo)
                .dontTransform()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(.5f)
                .into(civViewEvent);

        tvName.setText(event.getName());
        tvDate.setText(event.getDate());
        tvTime.setText(event.getTime());
        tvLocation.setText(event.getLocation());
        tvPrice.setText(event.getPrice());
        tvJoined.setText(String.valueOf(event.getUsersNum()));
        tvDescription.setText(event.getDescription());

        viewEventDialog.show();
    }
}
