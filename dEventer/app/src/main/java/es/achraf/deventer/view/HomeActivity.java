package es.achraf.deventer.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.roughike.bottombar.BottomBar;

import es.achraf.deventer.Perfil;
import es.achraf.deventer.R;
import es.achraf.deventer.fragments.FragmentChat;
import es.achraf.deventer.fragments.FragmentMisPlanes;
import es.achraf.deventer.fragments.FragmentPlanes;
import es.achraf.deventer.restApi.ConstantesRestApi;
import es.achraf.deventer.viewmodel.IViewModel;


public class HomeActivity extends AppCompatActivity {

    // Methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomBar bottomBar = findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(tabId -> {
            if (tabId == R.id.tab_planes) {
                FragmentPlanes fPlanes = new FragmentPlanes();
                cambiarFragment(fPlanes);

            } else if (tabId == R.id.tab_misplanes) {
                FragmentMisPlanes fMisPlanes = new FragmentMisPlanes();
                cambiarFragment(fMisPlanes);

            } else if (tabId == R.id.tab_chat) {
                FragmentChat fChat = new FragmentChat();
                cambiarFragment(fChat);
            }
        });
    }

    public void cambiarFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContenedor, fragment).commit();

        recuperarToken();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    public void recuperarToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                //Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();
                ConstantesRestApi.TOKEN = token;
            }
        });
    }

    public void cerrarSession() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        Intent intentLogin = new Intent(HomeActivity.this, SignInActivity.class);
        startActivity(intentLogin);
        finish();
        mAuth.signOut();
    }


    public void cuadroDialogo(String mensaje, String titulo) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("CERRAR SESIÓN", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cerrarSession();
                    }
                })
                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mSalir:
                cuadroDialogo("¿Seguro que desea cerrar sesión?", "Cerrar sesión");
                break;

            case R.id.mPerfil:
                Intent intentPerfil = new Intent(HomeActivity.this, Perfil.class);
                startActivity(intentPerfil);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
