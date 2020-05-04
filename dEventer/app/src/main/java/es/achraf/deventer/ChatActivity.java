package es.achraf.deventer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.achraf.deventer.adaptadores.AdapterMensajes;
import es.achraf.deventer.mensaje.MensajeEnviar;
import es.achraf.deventer.mensaje.MensajeRecibir;
import es.achraf.deventer.restApi.RestApiConstants;
import es.achraf.deventer.restApi.Endpoints;
import es.achraf.deventer.restApi.Respuesta;
import es.achraf.deventer.restApi.RestApiAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    private static final int PHOTO_SEND = 1;

    private CircleImageView fotoPlanChat;
    private TextView tituloPlanChat;
    private RecyclerView recyclerViewMensaje;
    private EditText txtMensaje;
    private FloatingActionButton btnEnviar;
    private AdapterMensajes adapter;
    private ImageButton btnEnviarFoto;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private static String fotoPerfilCadena;

    private String tituloPlan;
    private String urlImagenPlan;
    private String idPlan;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        fotoPlanChat = findViewById(R.id.fotoPlanChat);

        tituloPlanChat = findViewById(R.id.tituloPlanChat);
        recyclerViewMensaje = findViewById(R.id.recyclerViewMensaje);
        txtMensaje = findViewById(R.id.txtMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);
        btnEnviarFoto = findViewById(R.id.btnEnviarFoto);
        fotoPerfilCadena = null;

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        idPlan = getIntent().getStringExtra("id");
        tituloPlan = getIntent().getStringExtra("titulo");
        urlImagenPlan = getIntent().getStringExtra("imagen");


        if (!(tituloPlan == null || urlImagenPlan == null)) {

            FirebaseStorage storage = FirebaseStorage.getInstance();

            StorageReference storageReference = storage.getReference();

            StorageReference sCreaPlan = storageReference.child("FotosPlanes").child(urlImagenPlan);

            Task<Uri> task = sCreaPlan.getDownloadUrl();

            task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(ChatActivity.this).load(uri).error(R.mipmap.logo).fitCenter().into(fotoPlanChat);
                    tituloPlanChat.setText(tituloPlan);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("chat");//Sala de chat (nombre)
        storage = FirebaseStorage.getInstance();

        adapter = new AdapterMensajes(ChatActivity.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this);
        recyclerViewMensaje.setLayoutManager(layoutManager);
        recyclerViewMensaje.setAdapter(adapter);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = mAuth.getCurrentUser();

                if (user != null && !TextUtils.isEmpty(txtMensaje.getText().toString())) {

                    fotoPerfilCadena = user.getUid() + "/fotoDePerfil.jpg";

                    String nombreUsuario = mAuth.getCurrentUser().getDisplayName();
                    String mensaje = txtMensaje.getText().toString();
                    MensajeEnviar mEnviar = new MensajeEnviar(mensaje, nombreUsuario, fotoPerfilCadena, "1", ServerValue.TIMESTAMP);


                    String idUsuario = RestApiConstants.TOKEN;
                    mEnviar.setToken(idUsuario);

                    String idMensaje = databaseReference.child(idPlan).push().getKey();
                    if (idMensaje != null)
                        databaseReference.child(idPlan).child(idMensaje).setValue(mEnviar);
                    txtMensaje.setText("");

                    //  Toast.makeText(ChatActivity.this, RestApiConstants.TOKEN, Toast.LENGTH_SHORT).show();


                    //  enviarNotificacion(idUsuario, idMensaje, mEnviar, idPlan);

                }
            }
        });

        btnEnviarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/jpeg");
                i.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                String titulo = "Seleccione una imagen";
                startActivityForResult(Intent.createChooser(i, titulo), PHOTO_SEND);
            }
        });

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setScrollbar();
            }
        });

        databaseReference.child(idPlan).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NotNull DataSnapshot dataSnapshot, String s) {
                MensajeRecibir m = dataSnapshot.getValue(MensajeRecibir.class);
                if (m != null)
                    m.setVisto(false);
                adapter.addMensaje(m);
            }

            @Override
            public void onChildChanged(@NotNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(@NotNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NotNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                Log.d("TAG", databaseError.getMessage());
            }
        });
    }


    //POST
    private synchronized void enviarNotificacion(String idUsuario, String idMensaje, MensajeEnviar mEnviar, String idPlan) {
        RestApiAdapter restApiAdapter = new RestApiAdapter();
        Endpoints endpoints = restApiAdapter.establecerConexionApi();
        String mensaje = mEnviar.getMensaje();
        Call<Respuesta> respuestaCall = endpoints.registrarTokenId(idUsuario, mensaje);

        respuestaCall.enqueue(new Callback<Respuesta>() {
            @Override
            public void onResponse(Call<Respuesta> call, Response<Respuesta> response) {
                Respuesta respuesta = response.body();

                //una vez enviado el id, mensaje y token, necesito recibir  los datos del mismo y devolverlos para mostrar la informacion
                mEnviar.setToken(respuesta.getToken());
                Map<String, Object> mapEnviar = new HashMap<>();
                mapEnviar.put(idMensaje, mEnviar);
                //setValue(mEnviar);
                database.getReference().child("chat").child(idPlan).updateChildren(mapEnviar);
            }

            @Override
            public void onFailure(Call<Respuesta> call, Throwable t) {
                Log.e("ERROR CALLBACK", t.getMessage());
            }
        });
    }

    //GET
    public void recibirNotificacion(String id) {
        Respuesta respuesta = new Respuesta(id, "123", "perro");

        RestApiAdapter restApiAdapter = new RestApiAdapter();
        Endpoints endpoints = restApiAdapter.establecerConexionApi();
        Call<Respuesta> respuestaCall = endpoints.traerUsuario(respuesta.getId(), respuesta.getMensaje());

        respuestaCall.enqueue(new Callback<Respuesta>() {
            @Override
            public void onResponse(Call<Respuesta> call, Response<Respuesta> response) {
                Respuesta res = response.body();
                //Toast.makeText(getApplicationContext(), "ID: " + res.getId() + "\n" + "MENSAJE: " + res.getMensaje() + "\n" + "TOKEN: " + res.getToken(), Toast.LENGTH_LONG).show();

                String id = res.getId();
                String mensaje = res.getMensaje();
                String token = res.getToken();
            }


            @Override
            public void onFailure(Call<Respuesta> call, Throwable t) {

            }
        });
    }

    private void setScrollbar() {
        recyclerViewMensaje.scrollToPosition(adapter.getItemCount() - 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_SEND && resultCode == Activity.RESULT_OK) {
            Uri u = data.getData();
            storageReference = storage.getReference("imagenes_chat");//imagenes_chat

            final StorageReference fotoReferencia = storageReference.child(u.getLastPathSegment());
            UploadTask uploadTask = fotoReferencia.putFile(u);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    MensajeEnviar m = new MensajeEnviar(mAuth.getCurrentUser().getDisplayName() + " ha enviado una foto", u.toString(), tituloPlanChat.getText().toString(), fotoPerfilCadena, "2", ServerValue.TIMESTAMP);
                    databaseReference.child(idPlan).push().setValue(m);
                }
            });
        }
    }

}
