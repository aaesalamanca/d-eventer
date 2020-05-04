package es.achraf.deventer;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import es.achraf.deventer.model.User;

public class ProfileActivity extends AppCompatActivity {

    private static final int COD_IMAGEN = 1010;
    public static final int CODIGO_PERMISOS = 123;

    private CircleImageView imgPerfil;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mListener;

    private TextView txtNombreUsuario;
    private TextView txtNombreYapellido;
    private TextView txtEdad;
    private TextView txtSexo;
    private TextView txtCp;
    private TextView txtEmail;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    StorageReference storageReference = storage.getReference();//referencia de la app


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        this.txtNombreUsuario = findViewById(R.id.txtNombreUsuario);
        this.txtNombreYapellido = findViewById(R.id.txtNombreYapellido);
        this.txtEdad = findViewById(R.id.txtEdad);
        this.txtSexo = findViewById(R.id.txtSexo);
        this.txtCp = findViewById(R.id.txtCp);
        this.txtEmail = findViewById(R.id.tietEmail);

        ExtendedFloatingActionButton btnOk = findViewById(R.id.btnOk);

        btnOk.setOnClickListener(v -> finish());

        this.imgPerfil = findViewById(R.id.imgPerfil);
        this.imgPerfil.setOnClickListener(v -> cargarImagenGaleria());

        this.mAuth = FirebaseAuth.getInstance();
        this.mListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
            }
        };
    }


    public void cargarImagenGaleria() {
        if (checkPermissionREAD_EXTERNAL_STORAGE()) {
            Intent galeria = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(galeria, COD_IMAGEN);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recuperarDatosPerfilFirebase();
        mAuth.addAuthStateListener(mListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mListener != null) {
            mAuth.removeAuthStateListener(mListener);
        }
        recuperarDatosPerfilFirebase();
    }

    public void recuperarDatosPerfilFirebase() {
        String nombre = Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName();
        Toast.makeText(this, nombre, Toast.LENGTH_SHORT).show();
        if (nombre != null) {
            DatabaseReference dbReferecnce = FirebaseDatabase.getInstance().getReference().child(mAuth.getCurrentUser().getUid());

            dbReferecnce.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String email = mAuth.getCurrentUser().getEmail();
                    String nombre = mAuth.getCurrentUser().getDisplayName();

                    User user = dataSnapshot.getValue(User.class);

                    assert email != null;
                    assert nombre != null;
                    assert user != null;
                    /*if (email.equalsIgnoreCase(user.getEmail()) && nombre.equalsIgnoreCase(user.getName())) {

                        //txtNombreUsuario.setText(user.getName().toUpperCase());
                        txtNombreYapellido.setText(user.getName());
                        txtEdad.setText(user.getAge());
                        txtSexo.setText(user.getSex());
                        txtCp.setText(user.getPostalCode());
                        // txtEmail.setText(user.getEmail());
                        recuperarImagen();

                    }*/
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ProfileActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void recuperarDatosPerfilFacebook() {
        FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            String name = object.getString("name");
                            String email = object.getString("email_w_icon");
                            String genero = object.getString("gender");
                            String birthday = object.getString("birthday");
                            String location = object.getString("location");

                            txtNombreYapellido.setText(name);
                            txtEmail.setText(email);
                            txtSexo.setText(genero);
                            txtEdad.setText(birthday);
                            txtCp.setText(location);

                        } catch (JSONException e) {
                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email_w_icon,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                LoginManager.getInstance().logOut();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
    }


    public boolean checkPermissionREAD_EXTERNAL_STORAGE() {

        int currentAPIVersion = Build.VERSION.SDK_INT;

        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    showDialog("Es necesario aceptar los permisos para poder cambiar su foto de perfil", ProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_DOCUMENTS});

                } else {
                    ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_DOCUMENTS}, CODIGO_PERMISOS);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context, final String[] permission) {

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Recordatorio de permiso");
        alertBuilder.setMessage(msg);
        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(ProfileActivity.this, permission, CODIGO_PERMISOS);
            }
        });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    public void recuperarImagen() {
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String rutaImagen = uId + "/fotoDePerfil.jpg";


        StorageReference sPerfil = storageReference.child("ProfileActivity").child(rutaImagen);

        Task<Uri> task = sPerfil.getDownloadUrl();

        task.addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(getApplicationContext()).load(uri).error(R.mipmap.logo).dontTransform()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)//almacene la imagen en cache antes y despues de la carga de la magen, consiguiendo una disminucon del lag
                        .thumbnail(.5f).into(imgPerfil);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == COD_IMAGEN && data != null) {

            String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            Uri uri = data.getData();


            String rutaImagen = uId + "/fotoDePerfil.jpg";

            StorageReference sPerfil = storageReference.child("ProfileActivity").child(rutaImagen);

            assert uri != null;
            UploadTask uploadTask = sPerfil.putFile(uri);

            //aquí debería de guardar la url del dueno

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Glide.with(getApplicationContext()).load(uri).error(R.mipmap.logo).dontTransform()
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)//almacene la imagen en cache antes y despues de la carga de la magen, consiguiendo una disminucon del lag
                            .thumbnail(.5f).into(imgPerfil);
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Imagen cambiada con éxito", Snackbar.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        }
    }
}
