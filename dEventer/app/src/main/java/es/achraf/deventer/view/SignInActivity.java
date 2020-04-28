package es.achraf.deventer.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.Executor;

import es.achraf.deventer.R;
import es.achraf.deventer.viewmodel.IViewModel;
import es.achraf.deventer.viewmodel.ViewModel;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener, IView {

    // Fields

    private TextInputEditText tietEmail;
    private TextInputEditText tietPassword;

    private Handler handler; // Necesario para utilizar la huella

    private ProgressBar progressBar;
    private TextView tvCarga;

    private IViewModel viewModel; // ViewModel para seguir el patrón MVVM

    // Methods

    /**
     * Primer método ejecutado por la actividad. Inicialización de los componentes de la actividad.
     *
     * @param savedInstanceState es el bundle que almacena los datos del estado de la actividad
     *                           cuando se produce un cambio como rotaciones.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        init();
    }

    /**
     * Inicializa los componentes de la actividad.
     */
    private void init() {
        tietEmail = findViewById(R.id.tietEmail);
        tietPassword = findViewById(R.id.tietPassword);

        findViewById(R.id.mbtnSignIn).setOnClickListener(this);

        findViewById(R.id.ibtnHuella).setOnClickListener(this);

        progressBar = findViewById(R.id.progressBar);
        tvCarga = findViewById(R.id.tvCarga);
        findViewById(R.id.tvCrear).setOnClickListener(this);

        findViewById(R.id.lbtnFb).setOnClickListener(this);
        findViewById(R.id.sbtnGoogle).setOnClickListener(this);

        viewModel = new ViewModel();
        viewModel.setView(this);

        // Comprueba que el usuario ya ha iniciado sesión previamente para lanzar la actividad
        // de inicio.
        if (viewModel.isSignedIn()) {
            startHomeActivity();
        }
    }

    /**
     * Invoca a los distintos métodos —acciones— que se pueden llevar a cabo en elementos
     * interactivos de la actividad.
     *
     * @param v view que invoca al método —handler—.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mbtnSignIn:
                emailSignIn();
                break;
            case R.id.ibtnHuella:
                testFingertip();
                break;
            case R.id.tvCrear:
                startSignUpActivity();
                break;
            case R.id.lbtnFb:
                signInFb();
                break;
            case R.id.sbtnGoogle:
                signInGoogle();
                break;
        }
    }

    /**
     * Inicia sesión en la aplicación con email y contraseña.
     */
    private void emailSignIn() {
        String email = tietEmail.getText().toString();
        String password = tietPassword.getText().toString();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            loadingMessage(true);
            saveBiometric(email, password);
        } else {
            Toast.makeText(this, R.string.empty_fields, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Guarda las SharedPreferences del email y la contraseña para poder iniciar sesión con la
     * huella y solicita iniciar sesión.
     *
     * @param email    es el email del usuario.
     * @param password es la contraseña del usuario.
     */
    private void saveBiometric(String email, String password) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.fast_start)
                .setMessage(R.string.fingertip_start)
                .setPositiveButton(R.string.afirmativo, new DialogInterface.OnClickListener() {
                    /**
                     * Handler de la acción que debe ejecutarse en un click afirmativo.
                     *
                     * Solicita el inicio de sesión con guardado de las SharedPreferences —con
                     * huella—.
                     *
                     * @param dialog
                     * @param which
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewModel.emailSignIn(email, password, true);
                    }
                })
                .setNegativeButton(R.string.negativo, new DialogInterface.OnClickListener() {
                    /**
                     * Handler de la acción que debe ejecutarse con un click negativo.
                     *
                     * Solicita el inicio de sesión sin guardar las SharedPreferences —sin
                     * huella —.
                     *
                     * @param dialog
                     * @param which
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewModel.emailSignIn(email, password, false);
                    }
                })
                .show();
    }

    /**
     * Comprueba la posibilidad de utilizar el sensor de huellas.
     */
    private void testFingertip() {
        loadingMessage(true);

        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                askForFingertip();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this,
                        R.string.disabled_biometric, Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this,
                        R.string.no_registered_fingertip, Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this,
                        R.string.no_biometric_hw, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * Una vez que es posible utilizar el sensor de huellas —porque está disponible—, solicita su
     * utilización para iniciar sesión.
     */
    private void askForFingertip() {
        handler = new Handler();
        Executor executor = new Executor() {
            @Override
            public void execute(Runnable command) {
                handler.post(command);
            }
        };

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.biometric_access))
                .setSubtitle(getString(R.string.use_fingertip))
                .setNegativeButtonText(getString(R.string.cancel))
                .setConfirmationRequired(false)
                .build();

        BiometricPrompt biometricPrompt = new BiometricPrompt(this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            /**
             * Handler que muestra un mensaje de error por pantalla cuando hay un error de
             * autenticación al utilizar el sensor de huellas.
             *
             * @param errorCode es el código de error.
             * @param errString es la secuencia de error.
             */
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(SignInActivity.this,
                        R.string.no_biometric_hw, Toast.LENGTH_SHORT).show();
            }

            /**
             * Handler que solicita el inicio de sesión con email cuando la autenticación
             * es exitosa.
             *
             * @param result
             */
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                fingertipSignIn();
            }

            /**
             * Handler que muestra un mensaje de error por pantalla cuando ha fallado la
             * autenticación.
             */
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(SignInActivity.this,
                        R.string.failed_sign_in, Toast.LENGTH_SHORT).show();
            }
        });

        biometricPrompt.authenticate(promptInfo);
    }

    /**
     * Obtiene el email y la contraseña de las SharedPreferences una vez el inicio de sesión con
     * el sensor de huellas ha sido exitoso y solicita iniciar sesión.
     */
    private void fingertipSignIn() {
        String email = viewModel.getEmail();
        String password = viewModel.getPassword();

        loadingMessage(false);

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this,
                    R.string.previous_access_fingertip, Toast.LENGTH_SHORT).show();
        } else {
            viewModel.emailSignIn(email, password, false);
        }
    }

    /**
     * Falta implementar.
     * <p>
     * Inicia sesión con Facebook.
     */
    private void signInFb() {

    }

    /**
     * Falta implementar.
     * <p>
     * Inicia sesión con Google.
     */
    private void signInGoogle() {

    }

    /**
     * Muestra o hace invisibles —GONE— distintos elementos de la actividad relacionados con la
     * espera e invocación de una nueva actividad.
     *
     * @param loading indica si los elementos deben desaparecer o verse.
     *                <p>
     *                - Deben verse -> True
     *                - No deben verse -> False
     */
    private void loadingMessage(boolean loading) {
        if (loading) {
            progressBar.setVisibility(View.VISIBLE);
            tvCarga.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            tvCarga.setVisibility(View.GONE);
        }
    }

    /**
     * Lanza la actividad de inicio de la aplicación.
     */
    private void startHomeActivity() {
        viewModel.setView(null);

        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.putExtra(IViewModel.K_VIEWMODEL, viewModel);
        startActivity(homeIntent);

        finish();

        overridePendingTransition(R.anim.anim, R.anim.zoom_back);
        // Corregir con los cambios a MVVM
        // Toast.makeText(this, "Bienvenido de nuevo " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Lanza la actividad de registro en la aplicación.
     */
    private void startSignUpActivity() {
        viewModel.setView(null);

        Intent signUpIntent = new Intent(this, SignUpActivity.class);
        signUpIntent.putExtra(IViewModel.K_VIEWMODEL, viewModel);
        startActivity(signUpIntent);

        finish();

        overridePendingTransition(R.anim.anim, R.anim.zoom_back);
    }

    /**
     * Obtiene las SharedPreferences de la Activity.
     *
     * @param mode es el modo de acceso a las SharedPreferences.
     * @return las SharedPreferences según mode.
     */
    @Override
    public SharedPreferences getPreferences(int mode) {
        return super.getPreferences(mode);
    }

    /**
     * Handler que ejecuta la acción requerida según el resultado del intento de inicio de sesión.
     * <p>
     * Si ha sido exitoso, lanza la actividad de inicio, si no, muestra un mensaje de error por
     * pantalla.
     *
     * @param signedIn es el resultado del intento de inicio de sesión.
     */
    @Override
    public void onSignInComplete(boolean signedIn) {
        loadingMessage(false);

        if (signedIn) {
            startHomeActivity();
        } else {
            Toast.makeText(SignInActivity.this,
                    R.string.failed_sign_in, Toast.LENGTH_SHORT).show();
        }
    }

}
