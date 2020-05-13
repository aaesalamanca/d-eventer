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
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import es.achraf.deventer.R;
import es.achraf.deventer.interfaces.ItemClickListener;
import es.achraf.deventer.model.Event;
import es.achraf.deventer.model.User;
import es.achraf.deventer.view.MapActivity;
import es.achraf.deventer.view.adapters.AdapterRecyclerViewPlanes;
import es.achraf.deventer.viewmodel.ViewModelEvents;

public class EventsFragment extends Fragment /*implements ItemClickListener*/ {

    // Fields
    private static final int RC_IMAGE = 0;

    private static final int K_PERMISSION = 1;

    private ViewModelEvents vme;

    private AdapterRecyclerViewPlanes adapterPlan;
    private RecyclerView rcvEvents;
    private Uri imageUri;
    private Dialog createEventDialog;

    private CircleImageView civEvent;

    private TextInputEditText tietName;
    private TextInputEditText tietDate;
    private TextInputEditText tietTime;
    public static TextInputEditText tietLocation;
    private TextInputEditText tietPrice;
    private TextInputEditText tietDescription;

    private ProgressBar pbLoading;
    private TextView tvLoading;

    private ArrayList<Event> planes;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

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

        //readEvents();
        return view;
    }

    /**
     * Inicializa los elemtnos del fragmento.
     *
     * @param view es la vista sobre la que se carga el fragmento.
     */
    private void init(View view) {
        vme = new ViewModelEvents();

        rcvEvents = view.findViewById(R.id.rcvEvents);

        pbLoading = view.findViewById(R.id.pbLoading);
        tvLoading = view.findViewById(R.id.tvLoading);

        loadCreateEventDialog(view);
    }

    /**
     * Carga el diálogo para crear un evento.
     *
     * @param view es la vista sobre la que se carga el diálogo.
     */
    private void loadCreateEventDialog(View view) {
        view.findViewById(R.id.efabCreateEvent).setOnClickListener(v -> {
            createEventDialog = new Dialog(getContext(), R.style.full_screen_dialog);
            createEventDialog.setContentView(R.layout.dialog_create_event);

            civEvent = createEventDialog.findViewById(R.id.civEvent);
            civEvent.setOnClickListener(v1 -> startGallery());

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

            createEventDialog.findViewById(R.id.mbtnCreate).setOnClickListener(v1 -> {
                uploadEvent();

                getActivity().finish();

                startActivity(getActivity().getIntent());
            });
            createEventDialog.findViewById(R.id.mbtnCancel)
                    .setOnClickListener(v1 -> createEventDialog.dismiss());

            createEventDialog.show();
        });
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

        new DatePickerDialog(getContext(), R.style.date_picker, (view, year, month, dayOfMonth) -> {
            tietDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
        }, currentYear, currentMonth, currentDay).show();
    }

    /**
     * Muestra el diálogo para la obtención de la hora en la que tendrá lugar el evento.
     */
    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();

        final int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        final int currentMinute = calendar.get(Calendar.MINUTE);

        new TimePickerDialog(getContext(), R.style.time_picker, (view, hourOfDay, minute) -> {
            tietTime.setText(String.format("%02d:%02d", hourOfDay, minute));
        }, currentHour, currentMinute, true).show();
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

        if (isValidForm(name, date, time, location, price, description)) {
            vme.uploadEvent(name, date, time, location, price, description, imageUri);

            createEventDialog.dismiss();
        } else
            Snackbar.make(createEventDialog.getWindow().getDecorView().getRootView(),
                    R.string.empty_fields, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Comprueba que el formulario es válido, es decir, que los campos nombre, fecha, hora,
     * ubicación, precio y descripción no están vacíos.
     *
     * @param name        es el nombre del plan.
     * @param date        es la fecha del plan.
     * @param time        es la hora del plan.
     * @param location    es la ubicación del plan.
     * @param price       es el precio del plan.
     * @param description es la descripción del plan.
     * @return true si el formulario es válido, false en caso contrario.
     */
    private boolean isValidForm(String name, String date, String time, String location,
                                String price, String description) {
        return !(TextUtils.isEmpty(name) || TextUtils.isEmpty(date) || TextUtils.isEmpty(time)
                || TextUtils.isEmpty(location) || TextUtils.isEmpty(price)
                || TextUtils.isEmpty(description));
    }

    /**
     * Lee los eventos de la base de datos.
     */
    /*private void readEvents() {
        planes = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("tabla_planes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {


                        for (QueryDocumentSnapshot document : task.getResult()) {

                            String idPlan = document.getId();
                            String fecha = document.getString("fecha");
                            String hora = document.getString("hora");
                            String urlImagen = document.getString("imagen");
                            String titulo = document.getString("titulo");
                            String ubicacion = document.getString("ubicacion");
                            String precio = document.getString("precio");
                            String descripcion = document.getString("descripcion");
                            String dueno = document.getString("dueno");
                            String imgDueno = document.getString("imgDueno");
                            ArrayList<String> usuariosApuntados = (ArrayList<String>) document.get("usuariosApuntados");

                            planes.add(new Event(idPlan, titulo, ubicacion, fecha, hora, precio, urlImagen, descripcion, dueno, imgDueno, usuariosApuntados));
                        }
                        adapterPlan = new AdapterRecyclerViewPlanes(getContext(), planes, this, R.layout.item_planes);
                        rcvEvents.setAdapter(adapterPlan);
                        adapterPlan.notifyDataSetChanged();
                        rcvEvents.setLayoutManager(new LinearLayoutManager(getContext()));
                    } else {
                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }*/

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
                            .thumbnail(.5f).into(civEvent);
                }
            }
        } else {
            Snackbar.make(getView().getRootView(), R.string.image_not_found, Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * @param view
     * @param pos
     */
    /*@Override
    public void onItemClick(View view, int pos) {

        Event event = (Event) planes.get(pos);

        final Dialog dialogVistaPlan = new Dialog(getContext(), R.style.full_screen_dialog);
        dialogVistaPlan.setContentView(R.layout.detalle_plan);

        MaterialButton btnApuntarsePlan = dialogVistaPlan.findViewById(R.id.btnApuntarsePlan);

        CircleImageView imgPlanDetalle = dialogVistaPlan.findViewById(R.id.imgPlanDetalle);
        CircleImageView imgPerfilUsuario = dialogVistaPlan.findViewById(R.id.imgPerfilUsuario);

        TextView txtFechaPlanDetalle = dialogVistaPlan.findViewById(R.id.txtFechaPlanDetalle);
        TextView txtHoraPlanDetalle = dialogVistaPlan.findViewById(R.id.txtHoraPlanDetalle);
        TextView txtUbicacionPlanDetalle = dialogVistaPlan.findViewById(R.id.txtUbicacionPlanDetalle);
        TextView txtPrecioPlanDetalle = dialogVistaPlan.findViewById(R.id.txtPrecioPlanDetalle);
        TextView txtTituloPlanDetalle = dialogVistaPlan.findViewById(R.id.txtTituloPlanDetalle);
        TextView txtDescripcionDetalle = dialogVistaPlan.findViewById(R.id.txtDescripcionDetalle);
        TextView txtDueno = dialogVistaPlan.findViewById(R.id.txtDuenoPlan);
        TextView txtNumApuntado = dialogVistaPlan.findViewById(R.id.txtNumApuntado);

        //comprobamos si el user ya está apuntado o no

        String ID = mAuth.getCurrentUser().getUid();
        if (ID != null) {
            DatabaseReference dbReferecnce = firebaseDatabase.getReference().child(ID);

            dbReferecnce.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);

                    ArrayList<String> idsUsuariosApuntados = event.getUsuariosApuntadosUID();
                    for (String id : idsUsuariosApuntados) {
                        /*if (id.equals(user.getUid())) {
                            btnApuntarsePlan.setEnabled(false);
                            btnApuntarsePlan.setText(R.string.apuntado);
                        }*/
                    /*}

                }

                /*@Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        //agregamos el user al event de nuestra base de datos para tener un seguimiento de os usuarios que hay en la base de datos por cada event
        btnApuntarsePlan.setOnClickListener(v -> {

            String nombree = mAuth.getCurrentUser().getDisplayName();
            mAuth.getCurrentUser().getUid();
            if (nombree != null) {
                DatabaseReference dbReferecnce = firebaseDatabase.getReference().child(mAuth.getCurrentUser().getUid());

                dbReferecnce.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        //event.getUsuariosApuntadosUID().add(user.getUid());//si no está, lo agrego

                        db.collection("tabla_planes").document(event.getId()).update("usuariosApuntados", event.getUsuariosApuntadosUID());

                        //Una vez agregamos el user a la lista de usuarios del event, agregamos el event a la lista de planes del user

                        DatabaseReference referenceDb = firebaseDatabase.getReference();
                        //DatabaseReference crearUsuario = referenceDb.child(user.getUid());
                        user.getAlEvent().add(event.getId());
                        //crearUsuario.setValue(user);


                        //FALTA LA OPCION DE DESAPUNTARSE DEL PLAN
                        /*event.getUsuariosApuntados().remove(user);
                        db.collection("tabla_planes").document(event.getId()).update("usuariosApuntados", event.getUsuariosApuntados());*/

                        /*Toast.makeText(getContext(), "Apuntado al event, que te diviertas", Toast.LENGTH_SHORT).show();
                        btnApuntarsePlan.setEnabled(false);
                        btnApuntarsePlan.setText(R.string.apuntado);

                        //agregamos uno al campo de numero de apuntados (dado que es mejor respecto al rendimiento que actualizar)
                        int num = Integer.parseInt(txtNumApuntado.getText().toString());
                        num += 1;
                        txtNumApuntado.setText(String.valueOf(num));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        txtTituloPlanDetalle.setText(event.getName());
        txtFechaPlanDetalle.setText(event.getDate());
        txtHoraPlanDetalle.setText(event.getTime());
        txtPrecioPlanDetalle.setText(event.getPrice());
        txtUbicacionPlanDetalle.setText(event.getLocation());
        txtDescripcionDetalle.setText(event.getDescription());
        txtDueno.setText(event.getOwnerId());
        txtNumApuntado.setText(String.valueOf(event.getUsuariosApuntadosUID().size()));

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageReference = storage.getReference();

        StorageReference sDuenoPlan = storageReference.child("ProfileActivity").child(event.getUriImageDuenoPlan());

        Task<Uri> taskDueno = sDuenoPlan.getDownloadUrl();

        taskDueno.addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getContext()).load(uri).error(R.mipmap.logo).dontTransform()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .thumbnail(.5f).into(imgPerfilUsuario);
            }
        });

        StorageReference sCreaPlan = storageReference.child("FotosPlanes").child(event.getImageUri());

        Task<Uri> taskPlan = sCreaPlan.getDownloadUrl();

        taskPlan.addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getContext()).load(uri).error(R.mipmap.logo).dontTransform()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .thumbnail(.5f).into(imgPlanDetalle);
            }
        });

        dialogVistaPlan.show();
    }*/
}