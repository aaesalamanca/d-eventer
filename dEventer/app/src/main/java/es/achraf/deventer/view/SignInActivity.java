package es.achraf.deventer.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.Executor;

import es.achraf.deventer.R;
import es.achraf.deventer.viewmodel.ViewModelSignIn;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    // Fields
    private static final int RC_GOOGLE_SIGN_IN = 0; // Request code para lanzar el SignIn con Google

    private ViewModelSignIn vmsi;

    private TextInputEditText tietEmail;
    private TextInputEditText tietPassword;

    // Necesario para el sensor de huellas
    // https://developer.android.com/training/sign-in/biometric-auth#display-login-prompt
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    private ProgressBar pbLoading;
    private TextView tvLoading;

    // https://firebase.google.com/docs/auth/android/google-signin#authenticate_with_firebase
    // https://developers.google.com/identity/sign-in/android/sign-in#configure_google_sign-in_and_the_googlesigninclient_object
    private GoogleSignInClient gsc; // Necesario para Google Sign In

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
        setContentView(R.layout.activity_sign_in);
        init();
    }

    /**
     * Inicializa los elementos de la actividad.
     */
    private void init() {
        vmsi = new ViewModelSignIn();
        // Comprueba que el user ya ha iniciado sesión previamente para lanzar la actividad
        // de inicio.
        if (vmsi.isSignedIn()) {
            startHomeActivity();
        }
        // Establece los Listener para el ViewModelSignUp asociado a esta vista
        vmsi.setSignInCompleteListener(signedIn -> {
            loadingMessage(false);

            if (signedIn) {
                startHomeActivity();
            } else {
                Toast.makeText(this,
                        R.string.failed_sign_in, Toast.LENGTH_SHORT).show();
            }
        });
        vmsi.setGetPreferencesListener(super::getPreferences);

        tietEmail = findViewById(R.id.tietEmail);
        tietPassword = findViewById(R.id.tietPassword);

        findViewById(R.id.mbtnSignIn).setOnClickListener(this);

        findViewById(R.id.ibtnFingertip).setOnClickListener(this);

        pbLoading = findViewById(R.id.pbLoading);
        tvLoading = findViewById(R.id.tvLoading);
        findViewById(R.id.tvSignUp).setOnClickListener(this);

        findViewById(R.id.lbtnFb).setOnClickListener(this);
        findViewById(R.id.sbtnGoogle).setOnClickListener(this);

        loadBiometric();

        testFingertip();

        loadGoogleSignIn();
    }

    /**
     * Inicializa los componentes necesarios para utilizar el sensor de huellas.
     */
    private void loadBiometric() {
        // Utilizar el sensor de huellas
        // https://developer.android.com/training/sign-in/biometric-auth#display-login-prompt
        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this,
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
                        R.string.failed_sign_in, Toast.LENGTH_SHORT).show();
            }

            /**
             * Handler que solicita el inicio de sesión con email cuando la autenticación
             * es exitosa.
             *
             * @param result es el resultado de la autenticación exitosa.
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
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.biometric_access))
                .setSubtitle(getString(R.string.use_fingertip))
                .setNegativeButtonText(getString(R.string.cancel))
                .setConfirmationRequired(false)
                .build();
    }

    /**
     * Inicializa los elementos necesarios para poder iniciar sesión con Google.
     * <p>
     * https://firebase.google.com/docs/auth/android/google-signin#authenticate_with_firebase
     * https://developers.google.com/identity/sign-in/android/sign-in#configure_google_sign-in_and_the_googlesigninclient_object
     */
    private void loadGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);
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
            case R.id.ibtnFingertip:
                testFingertip();
                break;
            case R.id.tvSignUp:
                startSignUpActivity();
                break;
            case R.id.lbtnFb:
                facebookSignIn();
                break;
            case R.id.sbtnGoogle:
                googleSignIn();
                break;
        }
    }

    /**
     * Inicia sesión en la aplicación con email y contraseña.
     */
    private void emailSignIn() {
        String email = tietEmail.getText().toString();
        String password = tietPassword.getText().toString();

        if (isValidForm(email, password)) {
            saveBiometric(email, password);
        } else {
            Toast.makeText(this, R.string.empty_fields, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Guarda las SharedPreferences del email y la contraseña para poder iniciar sesión con la
     * huella y solicita iniciar sesión.
     *
     * @param email    es el email del user.
     * @param password es la contraseña del user.
     */
    private void saveBiometric(String email, String password) {
        loadingMessage(true);

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.fast_start)
                .setMessage(R.string.fingertip_start)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    /**
                     * Handler de la acción que debe ejecutarse en un click afirmativo.
                     *
                     * Solicita el inicio de sesión guardando las SharedPreferences —con huella—.
                     *
                     * @param dialog es el diálogo.
                     * @param which es dónde.
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        vmsi.emailSignIn(email, password, true);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    /**
                     * Handler de la acción que debe ejecutarse con un click negativo.
                     *
                     * Solicita el inicio de sesión sin guardar las SharedPreferences —sin
                     * huella —.
                     *
                     * @param dialog es el diálogo.
                     * @param which es dónde.
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        vmsi.emailSignIn(email, password, false);
                    }
                })
                .show();
    }

    /**
     * Comprueba la posibilidad de utilizar el sensor de huellas.
     * <p>
     * https://developer.android.com/training/sign-in/biometric-auth#available
     */
    private void testFingertip() {
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                // https://developer.android.com/training/sign-in/biometric-auth#display-login-prompt
                biometricPrompt.authenticate(promptInfo);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this,
                        R.string.no_biometric_hw, Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this,
                        R.string.disabled_biometric, Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this,
                        R.string.no_registered_fingertip, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * Obtiene el email y la contraseña de las SharedPreferences una vez el inicio de sesión con
     * el sensor de huellas ha sido exitoso y solicita iniciar sesión.
     */
    private void fingertipSignIn() {
        String email = vmsi.getEmail();
        String password = vmsi.getPassword();

        if (isValidForm(email, password)) {
            loadingMessage(true);
            vmsi.emailSignIn(email, password, false);
        } else {
            Toast.makeText(this,
                    R.string.previous_access_fingertip, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Falta implementar.
     * <p>
     * Inicia sesión con Facebook.
     */
    private void facebookSignIn() {

    }

    /**
     * Solicita iniciar sesión con Google.
     */
    private void googleSignIn() {
        loadingMessage(true);

        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    /**
     * Comprueba que el formulario es válido, es decir, que los campos de email y contraseña no
     * están vacíos.
     *
     * @return true si el formulario es válido y false en caso contrario.
     */
    private boolean isValidForm(String email, String password) {
        return !(TextUtils.isEmpty(email) || TextUtils.isEmpty(password));
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
     * Lanza la actividad de inicio de la aplicación.
     */
    private void startHomeActivity() {
        Intent homeIntent = new Intent(this, HomeActivity.class);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            homeIntent.putExtra(IView.K_EVENT_ID, bundle.getString(IView.K_EVENT_ID));
        }

        startActivity(homeIntent);

        overridePendingTransition(R.anim.anim, R.anim.zoom_back);

        finish();
    }

    /**
     * Lanza la actividad de registro en la aplicación.
     */
    private void startSignUpActivity() {
        Intent signUpIntent = new Intent(this, SignUpActivity.class);
        startActivity(signUpIntent);

        overridePendingTransition(R.anim.anim, R.anim.zoom_back);

        finish();
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

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                vmsi.googleSignIn(account);
            } catch (ApiException e) {
                Toast.makeText(this, R.string.failed_sign_in, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
