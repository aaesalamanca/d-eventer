package es.achraf.deventer.fragments;

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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
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
import es.achraf.deventer.adaptadores.AdapterRecyclerViewPlanes;
import es.achraf.deventer.interfaces.ItemClickListener;
import es.achraf.deventer.model.Event;
import es.achraf.deventer.model.User;

public class FragmentPlanes extends Fragment implements ItemClickListener {

    private static final int CODIGO_PERMISOS = 123;
    private static final int COD_IMAGEN = 4040;

    private AdapterRecyclerViewPlanes adapterPlan;
    private RecyclerView recyclerViewPlanes;
    private ExtendedFloatingActionButton fbCreaPlan;
    private CircleImageView imgCreaPlan;
    private Uri urlImagen;
    private String rutaImagenDuenoPlan;
    private Dialog dialog;

    public static TextInputEditText txtUbicacionCreaPlan;
    private TextInputEditText txtTituloCreaPlan;
    private TextInputEditText txtFechaCreaPlan;
    private TextInputEditText txtHoraCreaPlan;
    private TextInputEditText txtDescripcion;
    private TextInputEditText txtPrecioCreaPlan;

    private MaterialButton btnCrearPlan;
    private MaterialButton btnSalirDeCrearPlan;

    private static ProgressBar progressbarPlanes;
    private static TextView cargandoPlanes;

    private ArrayList<Event> planes;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();//referencia de la app
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_planes, container, false);

        progressbarPlanes = v.findViewById(R.id.progressbarPlanes);
        cargandoPlanes = v.findViewById(R.id.cargandoPlanes);

        recyclerViewPlanes = v.findViewById(R.id.recyclerViewPlanes);

        fbCreaPlan = v.findViewById(R.id.fabCreaPlan);

        leerDatos();

        fbCreaPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = new Dialog(getContext(), R.style.full_screen_dialog);
                dialog.setContentView(R.layout.dialog_crea_plan);

                //Instanciamos los elementos del cuadro de dialogo
                imgCreaPlan = dialog.findViewById(R.id.imgCreaPlan);

                txtTituloCreaPlan = dialog.findViewById(R.id.txtTituloCreaPlan);

                txtFechaCreaPlan = dialog.findViewById(R.id.txtFechaCreaPlan);
                txtFechaCreaPlan.setOnClickListener(this);

                txtHoraCreaPlan = dialog.findViewById(R.id.txtHoraCreaPlan);
                txtHoraCreaPlan.setOnClickListener(this);


                txtDescripcion = dialog.findViewById(R.id.txtDescripcion);

                txtUbicacionCreaPlan = dialog.findViewById(R.id.txtUbicacionCreaPlan);
                txtUbicacionCreaPlan.setOnClickListener(this);

                txtPrecioCreaPlan = dialog.findViewById(R.id.txtPrecioCreaPlan);

                btnCrearPlan = dialog.findViewById(R.id.btnCrearPlan);


                btnSalirDeCrearPlan = dialog.findViewById(R.id.btnSalirDeCrearPlan);

                btnSalirDeCrearPlan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                        //FLAT DIALOG
                        /*    final FlatDialog flatDialog = new FlatDialog(getContext());
                        flatDialog.setTitle("ejemplo de flat dialog").setSubtitle("ejemplo de subtitulo")
                                .setFirstTextFieldHint("email_w_icon")
                                .setSecondTextFieldHint("password")
                                .setFirstButtonText("CONNECT")
                                .setSecondButtonText("CANCEL")
                                .withFirstButtonListner(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Toast.makeText(getContext(), flatDialog.getFirstTextField(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .withSecondButtonListner(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        flatDialog.dismiss();
                                    }
                                })
                                .show();*/
                    }
                });


                //PONER IMAGEN
                imgCreaPlan.setOnClickListener(v16 -> cargarImagen());

                //DATE PICKER
                txtFechaCreaPlan.setOnClickListener(v12 -> {
                    mostrarDatePicker();
                });

                //TIME PICKER
                txtHoraCreaPlan.setOnClickListener(v13 -> {
                    mostrarTimePicker();
                });

                //envia a mi mapa creado
                txtUbicacionCreaPlan.setOnClickListener(v14 -> {

                    if (CheckGooglePlayServices()) {
                        startActivity(new Intent(getActivity(), MapsActivity.class));
                    }
                });

                //guardamos el plan
                btnCrearPlan.setOnClickListener(v15 -> {
                    guardarPlan();
                    getActivity().finish();
                    startActivity(getActivity().getIntent());
                });

                //mostrar el dialogo que crea el plan
                dialog.show();
            }
        });

        return v;
    }


    public void mostrarDatePicker() {
        Calendar calendar = Calendar.getInstance();

        final int ano = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        final int dia = calendar.get(Calendar.DAY_OF_MONTH);

        @SuppressWarnings("SimpleDateFormat") DatePickerDialog dFecha = new DatePickerDialog(getContext(), R.style.datepicker, (view, year, month, dayOfMonth) -> {

            Date fecha;
            try {
                fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).parse(dayOfMonth + "/" + month + 1 + "/" + year);
                if (fecha != null)
                    txtFechaCreaPlan.setText(new SimpleDateFormat("dd/MM/yyyy").format(fecha));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }, ano, mes, dia);
        dFecha.show();
    }

    public void mostrarTimePicker() {
        Calendar calendar = Calendar.getInstance();

        final int hora = calendar.get(Calendar.HOUR_OF_DAY);
        final int minutos = calendar.get(Calendar.MINUTE);

        TimePickerDialog tHora = new TimePickerDialog(getContext(), R.style.timePicker, (view, hourOfDay, minute) -> {

            Date fecha;
            try {
                fecha = new SimpleDateFormat("kk:mm", Locale.FRANCE).parse(hourOfDay + ":" + minute);
                if (fecha != null) {
                    if (hourOfDay == 0)
                        txtHoraCreaPlan.setText(new SimpleDateFormat("00:mm").format(fecha));
                    else
                        txtHoraCreaPlan.setText(new SimpleDateFormat("kk:mm").format(fecha));
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }, hora, minutos, true);
        tHora.show();
    }

    //función que guarda nuestro plan creado
    public void guardarPlan() {
        if (!TextUtils.isEmpty(txtTituloCreaPlan.getText().toString()) && !TextUtils.isEmpty(txtFechaCreaPlan.getText().toString()) &&
                !TextUtils.isEmpty(txtHoraCreaPlan.getText().toString()) && !TextUtils.isEmpty(txtUbicacionCreaPlan.getText().toString()) &&
                !TextUtils.isEmpty(txtPrecioCreaPlan.getText().toString())) {


            String titulo = txtTituloCreaPlan.getText().toString();
            String fecha = txtFechaCreaPlan.getText().toString();
            String hora = txtHoraCreaPlan.getText().toString();
            String ubicacion = txtUbicacionCreaPlan.getText().toString();
            String precio = txtPrecioCreaPlan.getText().toString();
            String descripcion = txtDescripcion.getText().toString();
            String duenoPlan = "dEventer";


            if (mAuth.getCurrentUser() != null) {
                duenoPlan = mAuth.getCurrentUser().getDisplayName();
                rutaImagenDuenoPlan = mAuth.getCurrentUser().getUid() + "/fotoDePerfil.jpg";
            }

            String IMAGEN_SUBIDA = urlImagen.getLastPathSegment() + ".jpg";

            HashMap<String, Object> planesMap = new HashMap<>();
            planesMap.put("titulo", titulo);
            planesMap.put("fecha", fecha);
            planesMap.put("hora", hora);
            planesMap.put("precio", precio);
            planesMap.put("ubicacion", ubicacion);
            planesMap.put("descripcion", descripcion);
            planesMap.put("dueno", duenoPlan);
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

            dialog.dismiss();
        } else
            Snackbar.make(dialog.getWindow().getDecorView().getRootView(), "Debe rellenar todos los campos para continuar", Snackbar.LENGTH_SHORT).show();


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

    //carga de la imagen desde galeria
    private void cargarImagen() {
        if (checkPermissionREAD_EXTERNAL_STORAGE(getContext())) {
            Intent galeria = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(galeria, COD_IMAGEN);
        }
    }


    //comprueba gps
    private boolean CheckGooglePlayServices() {
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

    /*    public static void mostrarProgressBar() {
            progressbarPlanes.setVisibility(View.VISIBLE);
            cargandoPlanes.setVisibility(View.VISIBLE);
        }


        public static void ocultarProgressBar() {
            progressbarPlanes.setVisibility(View.GONE);
            cargandoPlanes.setVisibility(View.GONE);
        }
    */
    //recupero los datos de cloud firestore
    private void leerDatos() {
        // FragmentPlanes.mostrarProgressBar();
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
                        recyclerViewPlanes.setAdapter(adapterPlan);
                        adapterPlan.notifyDataSetChanged();
                        recyclerViewPlanes.setLayoutManager(new LinearLayoutManager(getContext()));

                        // FragmentPlanes.ocultarProgressBar();
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

            Glide.with(getActivity().getApplicationContext()).load(urlImagen).error(R.drawable.logo).dontTransform()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//almacene la imagen en cache antes y despues de la carga de la magen, consiguiendo una disminucon del lag
                    .thumbnail(.5f).into(imgCreaPlan);

        } else
            Snackbar.make(getView().getRootView(), "No se ha encontrado la imagen", Snackbar.LENGTH_SHORT).show();
    }


    @Override
    public void onItemClick(View view, int posicion) {

        Event event = (Event) planes.get(posicion);

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

        //comprobamos si el usuario ya está apuntado o no

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

        //agregamos el usuario al event de nuestra base de datos para tener un seguimiento de os usuarios que hay en la base de datos por cada event
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

                        //Una vez agregamos el usuario a la lista de usuarios del event, agregamos el event a la lista de planes del usuario

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

        StorageReference sDuenoPlan = storageReference.child("Perfil").child(event.getUriImageDuenoPlan());

        Task<Uri> taskDueno = sDuenoPlan.getDownloadUrl();

        taskDueno.addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getContext()).load(uri).error(R.drawable.logo).dontTransform()
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
                Glide.with(getContext()).load(uri).error(R.drawable.logo).dontTransform()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .thumbnail(.5f).into(imgPlanDetalle);
            }
        });

        dialogVistaPlan.show();

    }
}