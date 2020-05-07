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
import android.os.Build;
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
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import es.achraf.deventer.MapsActivity;
import es.achraf.deventer.R;
import es.achraf.deventer.interfaces.ItemClickListener;
import es.achraf.deventer.model.Event;
import es.achraf.deventer.model.User;
import es.achraf.deventer.view.adapters.AdapterRecyclerViewPlanes;

public class EventsFragment extends Fragment implements ItemClickListener {

    private static final int CODIGO_PERMISOS = 123;
    private static final int COD_IMAGEN = 4040;

    private AdapterRecyclerViewPlanes adapterPlan;
    private RecyclerView rcvEvents;
    private Uri urlImagen;
    private String rutaImagenDuenoPlan;
    private Dialog createEventDialog;

    private CircleImageView civEvent;
    public static TextInputEditText tietLocation;
    private TextInputEditText tietName;
    private TextInputEditText tietDate;
    private TextInputEditText tietTime;
    private TextInputEditText tietDescription;
    private TextInputEditText tietPrice;

    private ProgressBar pbLoading;
    private TextView tvLoading;

    private ArrayList<Event> planes;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();//referencia de la app
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_events, container, false);
        init(view);

        readEvents();
        return view;
    }

    private void init(View view) {
        rcvEvents = view.findViewById(R.id.rcvEvents);

        pbLoading = view.findViewById(R.id.pbLoading);
        tvLoading = view.findViewById(R.id.tvLoading);

        loadCreateEventDialog(view);
    }

    private void loadCreateEventDialog(View view) {
        view.findViewById(R.id.efabCreateEvent).setOnClickListener(v -> {
            createEventDialog = new Dialog(getContext(), R.style.full_screen_dialog);
            createEventDialog.setContentView(R.layout.dialog_create_event);

            civEvent = createEventDialog.findViewById(R.id.civEvent);
            civEvent.setOnClickListener(v1 -> loadImage());

            tietName = createEventDialog.findViewById(R.id.tietName);
            tietDate = createEventDialog.findViewById(R.id.tietDate);
            tietDate.setOnClickListener(v1 -> showDatePicker());
            tietTime = createEventDialog.findViewById(R.id.tietTime);
            tietTime.setOnClickListener(v1 -> showTimePicker());
            createEventDialog.findViewById(R.id.tietLocation).setOnClickListener(v1 -> {
                if (checkGooglePlay()) {
                    startActivity(new Intent(getActivity(), MapsActivity.class));
                }
            });
            tietPrice = createEventDialog.findViewById(R.id.tietPrice);
            tietDescription = createEventDialog.findViewById(R.id.tietDescription);

            createEventDialog.findViewById(R.id.mbtnCreate).setOnClickListener(v1 -> {
                saveEvent();
                getActivity().finish();
                startActivity(getActivity().getIntent());
            });
            createEventDialog.findViewById(R.id.mbtnCancel)
                    .setOnClickListener(v1 -> createEventDialog.dismiss());

            createEventDialog.show();
        });
    }

    //carga de la imagen desde galeria
    private void loadImage() {
        if (checkPermissionREAD_EXTERNAL_STORAGE(getContext())) {
            Intent galeria = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(galeria, COD_IMAGEN);
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(getContext(), R.style.datepicker, (view, year, month, dayOfMonth) -> {
            tietDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
        }, currentYear, currentMonth, currentDay).show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();

        final int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        final int currentMinute = calendar.get(Calendar.MINUTE);

        new TimePickerDialog(getContext(), R.style.timePicker, (view, hourOfDay, minute) -> {
            tietTime.setText(String.format("%02d:%02d", hourOfDay, minute));
        }, currentHour, currentMinute, true).show();
    }

    private void saveEvent() {
        String name = tietName.getText().toString();
        String date = tietDate.getText().toString();
        String time = tietTime.getText().toString();
        String location = tietLocation.getText().toString();
        String price = tietPrice.getText().toString();
        String description = tietDescription.getText().toString();
        String owner = "dEventer";

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(date) &&
                !TextUtils.isEmpty(time) && !TextUtils.isEmpty(location) &&
                !TextUtils.isEmpty(price)) {

            if (mAuth.getCurrentUser() != null) {
                owner = mAuth.getCurrentUser().getDisplayName();
                rutaImagenDuenoPlan = mAuth.getCurrentUser().getUid() + "/fotoDePerfil.jpg";
            }

            String IMAGEN_SUBIDA = urlImagen.getLastPathSegment() + ".jpg";

            HashMap<String, Object> planesMap = new HashMap<>();
            planesMap.put("titulo", name);
            planesMap.put("fecha", date);
            planesMap.put("hora", time);
            planesMap.put("precio", price);
            planesMap.put("ubicacion", location);
            planesMap.put("descripcion", description);
            planesMap.put("dueno", owner);
            planesMap.put("imgDueno", rutaImagenDuenoPlan);
            planesMap.put("imagen", IMAGEN_SUBIDA);

            // arraylist de usuarios apuntados, inicialmente no habrá nadie obviamente al crearse el plan
            ArrayList<String> usuariosApuntados = new ArrayList<>();
            planesMap.put("usuariosApuntados", usuariosApuntados);

            //subimos la imagen al storage

            StorageReference sCreaPlan = storageReference.child("FotosPlanes").child(IMAGEN_SUBIDA);

            UploadTask uploadTask = sCreaPlan.putFile(urlImagen);


            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


            db.collection("tabla_planes").document().set(planesMap).addOnSuccessListener(aVoid ->
                    Snackbar.make(getView().getRootView(), "Event en marcha. ¡Buena suerte!", Snackbar.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Snackbar.make(getView().getRootView(), "Se ha producido un error al guardar los datos", Snackbar.LENGTH_SHORT).show());

            createEventDialog.dismiss();
        } else
            Snackbar.make(createEventDialog.getWindow().getDecorView().getRootView(), "Debe rellenar todos los campos para continuar", Snackbar.LENGTH_SHORT).show();


    }


    //función que muestra la alerta por si se ignoran los mensajes, éste los vuelve a pedir si no se ha aceptado el permiso
    private void showDialog(final Context context, final String[] permission) {

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Recordatorio de permiso");
        alertBuilder.setMessage("Es necesario aceptar los permisos para poder cambiar su foto de perfil");
        alertBuilder.setPositiveButton(android.R.string.yes, (dialog, which) -> ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), permission, CODIGO_PERMISOS));
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    //función para pedir los permisos
    private boolean checkPermissionREAD_EXTERNAL_STORAGE(final Context context) {

        int currentAPIVersion = Build.VERSION.SDK_INT;

        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    showDialog(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_DOCUMENTS});

                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_DOCUMENTS}, CODIGO_PERMISOS);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    //comprueba gps
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


    private void readEvents() {
        // EventsFragment.mostrarProgressBar();
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

                        // EventsFragment.ocultarProgressBar();
                    } else {
                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == COD_IMAGEN && data != null) {

            urlImagen = data.getData();

            Glide.with(getActivity().getApplicationContext()).load(urlImagen).error(R.mipmap.logo).dontTransform()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//almacene la imagen en cache antes y despues de la carga de la magen, consiguiendo una disminucon del lag
                    .thumbnail(.5f).into(civEvent);

        } else
            Snackbar.make(getView().getRootView(), "No se ha encontrado la imagen", Snackbar.LENGTH_SHORT).show();
    }


    @Override
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
                    }

                }

                @Override
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

                        Toast.makeText(getContext(), "Apuntado al event, que te diviertas", Toast.LENGTH_SHORT).show();
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


        txtTituloPlanDetalle.setText(event.getNombre());
        txtFechaPlanDetalle.setText(event.getFecha());
        txtHoraPlanDetalle.setText(event.getHora());
        txtPrecioPlanDetalle.setText(event.getPrecio());
        txtUbicacionPlanDetalle.setText(event.getUbicacion());
        txtDescripcionDetalle.setText(event.getDescripcion());
        txtDueno.setText(event.getDuenoPlan());
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

        StorageReference sCreaPlan = storageReference.child("FotosPlanes").child(event.getUrlImagen());

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

    }
}