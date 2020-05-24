package es.achraf.deventer.view.fragments;

import android.app.Dialog;
import android.content.Intent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import es.achraf.deventer.R;
import es.achraf.deventer.view.ItemClickListener;
import es.achraf.deventer.model.Event;
import es.achraf.deventer.view.IView;
import es.achraf.deventer.view.adapters.EventAdapter;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
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

            EventAdapter adptEvent = new EventAdapter(getContext(),
                    this.alEvent, this, R.layout.item_event);
            rcvOwnEvents.setAdapter(adptEvent);
            adptEvent.notifyDataSetChanged();
            rcvOwnEvents.setLayoutManager(new LinearLayoutManager(getContext()));

            loadingMessage(false);
        });
        vmoe.getEvents();
        vmoe.setGetImageListener(((cloudUri, isChange) ->
                Glide.with(getContext()).load(cloudUri).error(R.mipmap.logo)
                        .dontTransform()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .thumbnail(.5f)
                        .into(civUser)
        ));
        vmoe.setGetNameListener(name -> tvUser.setText(name));
        vmoe.setJoinListener(new IView.JoinListener() {
            @Override
            public void checkJoinedCompleted(boolean hasJoined) {

            }

            @Override
            public void joinCompleted() {
                mbtnJoin.setText(R.string.leave_event);

                int joined = Integer.parseInt(tvJoined.getText().toString()) + 1;
                tvJoined.setText(String.valueOf(joined));
            }

            @Override
            public void leaveCompleted() {
                mbtnJoin.setText(R.string.join_event);

                int joined = Integer.parseInt(tvJoined.getText().toString()) - 1;
                tvJoined.setText(String.valueOf(joined));
            }
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
                vmoe.join(key);
            } else {
                vmoe.leave(key);
            }
        });

        tvDate = viewEventDialog.findViewById(R.id.tvDate);
        tvTime = viewEventDialog.findViewById(R.id.tvTime);

        viewEventDialog.findViewById(R.id.llLocation).setOnClickListener(v -> {
            String encodedPlace = tvLocation.getText().toString();
            encodedPlace = encodedPlace.replace(IView.MAPS_SPACE, IView.MAPS_SPACE_ENCODED);
            encodedPlace = encodedPlace.replace(IView.MAPS_COMMA, IView.MAPS_COMMA_ENCODED);
            Uri uri = Uri.parse(IView.MAPS_QUERY + encodedPlace);
            Intent mapsIntent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(mapsIntent);
        });
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

        vmoe.getImage(event.getOwnerId());
        vmoe.getName(event.getOwnerId());

        Glide.with(getContext()).load(Uri.parse(event.getImageUri())).error(R.mipmap.logo)
                .dontTransform()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(.5f)
                .into(civViewEvent);

        tvName.setText(event.getName());
        mbtnJoin.setText(R.string.leave_event);
        
        tvDate.setText(event.getDate());
        tvTime.setText(event.getTime());
        tvLocation.setText(event.getLocation());
        tvPrice.setText(event.getPrice());
        tvJoined.setText(String.valueOf(event.getUsersNum()));
        tvDescription.setText(event.getDescription());

        viewEventDialog.show();
    }
}
