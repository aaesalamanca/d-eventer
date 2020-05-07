package es.achraf.deventer.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.iid.FirebaseInstanceId;
import com.roughike.bottombar.BottomBar;

import es.achraf.deventer.R;
import es.achraf.deventer.view.fragments.ChatFragment;
import es.achraf.deventer.view.fragments.EventsFragment;
import es.achraf.deventer.view.fragments.OwnEventsFragment;
import es.achraf.deventer.restApi.RestApiConstants;
import es.achraf.deventer.viewmodel.ViewModelHome;


public class HomeActivity extends AppCompatActivity {

    // Fields
    private ViewModelHome vmh;

    // Methods

    /**
     * Primer método ejecutado por la actividad. Inicializa los elmentos de la actividad.
     *
     * @param savedInstanceState es el bundle que almacena los datos del estado de la actividad
     *                           cuando se produce un cambio como rotaciones de la pantalla.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
    }

    /**
     * Inicializa los elementos de la actividad.
     */
    private void init() {
        vmh = new ViewModelHome();
        vmh.setSignOutListener(this::startSignInActivity);

        setSupportActionBar(findViewById(R.id.toolbar));

        BottomBar bottomBar = findViewById(R.id.bottom_bar);
        bottomBar.setOnTabSelectListener(tabId -> {
            switch (tabId) {
                case R.id.events_tab:
                    EventsFragment frgEvents = new EventsFragment();
                    loadFragment(frgEvents);
                    break;
                case R.id.own_events_tab:
                    OwnEventsFragment frgOwnEvents = new OwnEventsFragment();
                    loadFragment(frgOwnEvents);
                    break;
                case R.id.chat_tab:
                    ChatFragment frgChat = new ChatFragment();
                    loadFragment(frgChat);
                    break;
                default:
                    break;
            }
        });
    }

    /**
     * Carga el Fragment que debe visualizarse.
     *
     * @param fragment es el Fragment a visualizar.
     */
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frgContainer, fragment).commit();
        getToken();
    }

    /**
     * Obtiene el Token de Firebase para las notificaciones.
     */
    private void getToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult ->
                RestApiConstants.TOKEN = instanceIdResult.getToken());
    }

    /**
     * Establece el menú de la aplicación.
     *
     * @param menu es el menú que se va a cargar.
     * @return true, pues se ha creado el menú.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Handler que ejecuta la acción requerida según el ítem seleccionado en el menú.
     *
     * @param item es el ítem selecciconado.
     * @return la opción seleccionada.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.iProfile:
                startProfileActivity();
                break;
            case R.id.iSignOut:
                showSignOutConfirm();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Muestra el diálogo de confirmación para cerrar la sesión.
     */
    private void showSignOutConfirm() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.sign_out)
                .setMessage(R.string.confirm_sign_out)
                .setPositiveButton(R.string.sign_out, (showDialog, which) -> vmh.signOut())
                .setNegativeButton(R.string.cancel, (dialog, which) -> {

                })
                .show();
    }

    /**
     * Lanza la actividad para ver el perfil del user.
     */
    private void startProfileActivity() {
        Intent profileIntent = new Intent(this, ProfileActivity.class);
        startActivity(profileIntent);
    }

    /**
     * Lanza la actividad de sign in para volver a iniciar sesión.
     */
    private void startSignInActivity() {
        Intent signInIntent = new Intent(this, SignInActivity.class);
        startActivity(signInIntent);

        finish();
    }
}
