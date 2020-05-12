package es.achraf.deventer.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.snackbar.Snackbar;

import de.hdodenhof.circleimageview.CircleImageView;
import es.achraf.deventer.R;
import es.achraf.deventer.viewmodel.ViewModelProfile;

public class ProfileActivity extends AppCompatActivity {

    // Fields

    private static final int RC_IMAGE = 0;

    private static final int K_PERMISSION = 1;

    private ViewModelProfile vmp;

    private CircleImageView civProfile;

    private TextView tvUser;
    private TextView tvName;
    private TextView tvAge;
    private TextView tvSex;
    private TextView tvPostalCode;
    private TextView tvEmail;

    // Methods

    /**
     * Primer método ejecutado por la actividad. Inicializa los elmentos de la actividad.
     *
     * @param savedInstanceState es el bundle que almacena los datos del estado de la actividad
     *                           cuando se produce un cambio como rotaciones.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
    }

    /**
     * Inicializa los elementos de la actividad.
     */
    private void init() {
        vmp = new ViewModelProfile();
        vmp.setGetProfileListener(this::getProfile);
        vmp.setGetImageListener((cloudUri, isChange) -> {
            getImage(cloudUri);
            if (isChange) {
                Snackbar.make(getWindow().getDecorView().getRootView(),
                        R.string.succeded_image_change, Snackbar.LENGTH_SHORT).show();
            }
        });
        vmp.getImage();

        tvUser = findViewById(R.id.tvUser);
        tvName = findViewById(R.id.tvName);
        tvAge = findViewById(R.id.tvAge);
        tvSex = findViewById(R.id.tvSex);
        tvPostalCode = findViewById(R.id.tvPostalCode);
        tvEmail = findViewById(R.id.tvEmail);

        findViewById(R.id.efabConfirm).setOnClickListener(v -> finish());

        civProfile = findViewById(R.id.civProfile);
        civProfile.setOnClickListener(v -> startGallery());
    }

    /**
     * Lanza la galería, si la aplicación tiene permisos, para elegir la imagen de perfil.
     */
    private void startGallery() {
        if (hasPermission()) {
            Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT,
                    MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, RC_IMAGE);
        }
    }

    /**
     * Comprueba si la aplicación tiene permisos para leer archivos almacenados externamente
     * —imágenes—.
     *
     * @return true si tiene permisos, false en caso contrario.
     */
    private boolean hasPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showPermissionDialog(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        K_PERMISSION);
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
    private void showPermissionDialog(String[] permission) {
        new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle(R.string.permission_reminder)
                .setMessage(R.string.accept_permission)
                .setPositiveButton(android.R.string.yes, (dialog, which) ->
                        ActivityCompat.requestPermissions(this, permission,
                                K_PERMISSION))
                .create().show();
    }

    /**
     * Carga el perfil del usuario.
     */
    private void getProfile() {
        String user = vmp.getName();
        String email = vmp.getEmail();
        String name = vmp.getName();
        String age = vmp.getAge();
        String sex = vmp.getSex();
        String postalCode = vmp.getPostalCode();

        tvUser.setText(user);
        tvEmail.setText(email);
        tvName.setText(name);
        tvAge.setText(age);
        tvSex.setText(sex);
        tvPostalCode.setText(postalCode);
    }

    /**
     * Obtiene la imagen a partir de la Uri y la carga en el formulario.
     *
     * @param cloudUri es la Uri de la imagen en la base ded atos.
     */
    private void getImage(Uri cloudUri) {
        Glide.with(getApplicationContext()).load(cloudUri).error(R.mipmap.logo).dontTransform()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(.5f).into(civProfile);
    }

    /**
     * Se ejecuta cuando termina la actividad iniciada por esta y espera un resultado de vuelta.
     *
     * @param requestCode es el código que identifica a la actividad invocada.
     * @param resultCode  es el código del resultado.
     * @param data        es el Intent con los datos devueltos.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    vmp.uploadImage(data.getData());
                }
            }
        }
    }
}
