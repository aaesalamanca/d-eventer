package es.achraf.deventer.view.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import es.achraf.deventer.R;
import es.achraf.deventer.model.Event;
import es.achraf.deventer.view.IView;
import es.achraf.deventer.view.ItemClickListener;
import es.achraf.deventer.view.MapActivity;
import es.achraf.deventer.view.adapters.EventAdapter;
import es.achraf.deventer.viewmodel.ViewModelEvents;

public class EventsFragment extends Fragment implements ItemClickListener {

    // Fields
    private static final int RC_IMAGE = 0;

    private static final int K_PERMISSION = 1;

    private ViewModelEvents vme;

    private String key;
    private ArrayList<String> alKeys;
    private ArrayList<Event> alEvent;

    private ProgressBar pbLoading;
    private TextView tvLoading;
    private TextView tvEmptyText;
    private TextView tvEmptyEmoji;

    private RecyclerView rcvEvent;

    private Dialog createEventDialog;

    private Uri imageUri;

    private CircleImageView civSetEvent;

    private TextInputEditText tietName;
    private TextInputEditText tietDate;
    private TextInputEditText tietTime;
    public static TextInputEditText tietLocation;
    private TextInputEditText tietPrice;
    private TextInputEditText tietDescription;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_events, container, false);
        init(view);

        return view;
    }

    /**
     * Inicializa los elemtnos del fragmento.
     *
     * @param view es la vista sobre la que se carga el fragmento.
     */
    private void init(View view) {
        vme = new ViewModelEvents();
        vme.setGetEventsListener((alKeys, alEvent) -> {
            this.alKeys = alKeys;
            this.alEvent = alEvent;

            EventAdapter adptEvent = new EventAdapter(getContext(),
                    this.alEvent, this, R.layout.item_event);
            rcvEvent.setAdapter(adptEvent);
            adptEvent.notifyDataSetChanged();
            rcvEvent.setLayoutManager(new LinearLayoutManager(getContext()));

            loadingMessage(false);
            loadingEmpty(this.alEvent.isEmpty());
        });
        vme.getEvents();
        vme.setGetImageListener((cloudUri, isChange) ->
                Glide.with(getContext()).load(cloudUri).error(R.mipmap.logo)
                        .dontTransform()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .thumbnail(.5f)
                        .into(civUser)
        );
        vme.setGetNameListener(name -> tvUser.setText(name));
        vme.setJoinListener(new IView.JoinListener() {
            @Override
            public void checkJoinedCompleted(boolean hasJoined) {
                if (hasJoined) {
                    mbtnJoin.setText(R.string.leave_event);
                } else {
                    mbtnJoin.setText(R.string.join_event);
                }
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

        rcvEvent = view.findViewById(R.id.rcvEvents);

        pbLoading = view.findViewById(R.id.pbLoading);
        tvLoading = view.findViewById(R.id.tvLoading);
        tvEmptyText = view.findViewById(R.id.tvEmptyText);
        tvEmptyEmoji = view.findViewById(R.id.tvEmptyEmoji);


        loadCreateEventDialog();
        view.findViewById(R.id.efabCreateEvent).setOnClickListener(v -> createEventDialog.show());

        loadViewEventDialog();

        loadingMessage(true);
    }

    /**
     * Carga el diálogo para crear un evento.
     */
    private void loadCreateEventDialog() {
        createEventDialog = new Dialog(getContext(), R.style.full_screen_dialog);
        createEventDialog.setContentView(R.layout.dialog_create_event);

        civSetEvent = createEventDialog.findViewById(R.id.civEvent);
        civSetEvent.setOnClickListener(v1 -> startGallery());

        tietName = createEventDialog.findViewById(R.id.tietName);
        tietDate = createEventDialog.findViewById(R.id.tietDate);
        tietDate.setOnClickListener(v1 -> showDatePicker());
        tietTime = createEventDialog.findViewById(R.id.tietTime);
        tietTime.setOnClickListener(v1 -> showTimePicker());
        tietLocation = createEventDialog.findViewById(R.id.tietLocation);
        tietLocation.setOnClickListener(v1 -> {
            if (checkGooglePlay()) {
                startActivity(new Intent(getActivity(), MapActivity.class));
            }
        });
        tietPrice = createEventDialog.findViewById(R.id.tietPrice);
        tietDescription = createEventDialog.findViewById(R.id.tietDescription);

        createEventDialog.findViewById(R.id.mbtnCreate).setOnClickListener(v1 -> uploadEvent());
        createEventDialog.findViewById(R.id.mbtnCancel)
                .setOnClickListener(v1 -> createEventDialog.dismiss());
    }

    /**
     * Lanza la galería, si la aplicación tiene permisos, para elegir la imagen del evento.
     */
    private void startGallery() {
        if (hasPermission(getContext())) {
            Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT,
                    MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, RC_IMAGE);
        }
    }

    /**
     * Comprueba si la aplicación tiene permisos para leer archivos almacenados externamente
     * —imágenes—.
     *
     * @param context es el contexto de la actividad en el que está el fragmento.
     * @return true si tiene permisos, false en caso contrario.
     */
    private boolean hasPermission(final Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showPermissionDialog(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, K_PERMISSION);
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * Muestra el diálogo para pedir permisos.
     *
     * @param permission es el array de String con los permisos requeridos.
     */
    private void showPermissionDialog(final Context context, final String[] permission) {
        new AlertDialog.Builder(context)
                .setCancelable(true)
                .setTitle(R.string.permission_reminder)
                .setMessage(R.string.accept_permission)
                .setPositiveButton(android.R.string.yes, (dialog, which) ->
                        ActivityCompat.requestPermissions(getActivity(),
                                permission, K_PERMISSION))
                .create().show();
    }

    /**
     * Muestra el diálogo para la obtención de la fecha en la que tendrá lugar el evento.
     */
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(getContext(), R.style.date_picker,
                (view, year, month, dayOfMonth) ->
                        tietDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year)
                , currentYear, currentMonth, currentDay).show();
    }

    /**
     * Muestra el diálogo para la obtención de la hora en la que tendrá lugar el evento.
     */
    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();

        final int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        final int currentMinute = calendar.get(Calendar.MINUTE);

        new TimePickerDialog(getContext(), R.style.time_picker,
                (view, hourOfDay, minute) ->
                        tietTime.setText(String.format("%02d:%02d", hourOfDay, minute))
                , currentHour, currentMinute, true).show();
    }

    /**
     * Comprueba si están disponibles los servicios de Google Play, para Maps y Places.
     *
     * @return true si los servicios de Google Play están disponibles, false en caso contrario.
     */
    private boolean checkGooglePlay() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(getContext());
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(getActivity(), result, 0).show();
            }
            return false;
        }
        return true;
    }

    /**
     * Guarda el evento en la base de datos.
     */
    private void uploadEvent() {
        String name = tietName.getText().toString();
        String date = tietDate.getText().toString();
        String time = tietTime.getText().toString();
        String location = tietLocation.getText().toString();
        String price = tietPrice.getText().toString();
        String description = tietDescription.getText().toString();

        if (isValidForm(name, date, time, location, price, description, imageUri)) {
            vme.uploadEvent(name, date, time, location, price, description, imageUri);
            createEventDialog.dismiss();
            getActivity().finish();
            startActivity(getActivity().getIntent());
        } else {
            Snackbar.make(createEventDialog.getWindow().getDecorView().getRootView(),
                    R.string.empty_fields, Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * Comprueba que el formulario es válido, es decir, que los campos de foto, nombre, fecha, hora,
     * ubicación, precio y descripción no están vacíos.
     *
     * @param name        es el nombre del evento.
     * @param date        es la fecha del evento.
     * @param time        es la hora del evento.
     * @param location    es la ubicación del evento.
     * @param price       es el precio del evento.
     * @param description es la descripción del evento.
     * @param uri         es la uri de la imagen del evento.
     * @return true si el formulario es válido, false en caso contrario.
     */
    private boolean isValidForm(String name, String date, String time, String location,
                                String price, String description, Uri uri) {
        return !(TextUtils.isEmpty(name) || TextUtils.isEmpty(date) || TextUtils.isEmpty(time)
                || TextUtils.isEmpty(location) || TextUtils.isEmpty(price)
                || TextUtils.isEmpty(description) || uri == null);
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
                vme.join(key);
            } else {
                vme.leave(key);
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
     * Muestra o hace invisibles —GONE— los textos que indican que no hay ningún evento disponible.
     *
     * @param empty indica si hay o no eventos:
     *              True -> No hay eventos.
     *              False -> Hay eventos.
     */
    private void loadingEmpty(boolean empty) {
        if (empty) {
            tvEmptyText.setVisibility(View.VISIBLE);
            tvEmptyEmoji.setVisibility(View.VISIBLE);
        } else {
            tvEmptyText.setVisibility(View.GONE);
            tvEmptyEmoji.setVisibility(View.GONE);
        }
    }

    /**
     * Se ejecuta cuando termina la actividad iniciada por esta y espera un resultado de vuelta.
     *
     * @param requestCode es el código que identifica a la actividad invocada.
     * @param resultCode  es el código del resultado.
     * @param data        es el Intent con los datos devueltos.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    imageUri = data.getData();

                    Glide.with(getActivity().getApplicationContext()).load(imageUri).error(R.mipmap.logo)
                            .dontTransform()
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .thumbnail(.5f).into(civSetEvent);
                }
            }
        } else {
            Snackbar.make(getView().getRootView(), R.string.image_not_found, Snackbar.LENGTH_SHORT).show();
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

        vme.getImage(event.getOwnerId());
        vme.getName(event.getOwnerId());
        vme.checkJoined(key);

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