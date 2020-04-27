package es.achraf.deventer.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.google.android.material.button.MaterialButton;
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

    private MaterialButton mbtnEntrar;

    private ImageButton ibtnHuella;

    private Handler handler; // Necesario para utilizar la huella
    private Executor executor; // Necesario para utilizar la huella

    private ProgressBar progressBar;
    private TextView tvCarga;
    private TextView tvCrear;

    private LoginButton lbtnFb;
    private SignInButton sbtnGoogle;

    private IViewModel viewModel; // ViewModel para seguir el patrón MVVM

    // Methods

    /**
     * Primer método ejecutado por la actividad.
     *
     * @param savedInstanceState es el bundle que almacena los datos de la instantánea del estado
     *                           de la actividad cuando se produce un cambio como rotaciones.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        init();
    }

    /**
     * Inicializa los atributos.
     */
    private void init() {
        tietEmail = findViewById(R.id.tietEmail);
        tietPassword = findViewById(R.id.tietPassword);
        mbtnEntrar = findViewById(R.id.mbtnSignIn);
        mbtnEntrar.setOnClickListener(this);

        ibtnHuella = findViewById(R.id.ibtnHuella);
        ibtnHuella.setOnClickListener(this);

        progressBar = findViewById(R.id.progressBar);
        tvCarga = findViewById(R.id.tvCarga);
        tvCrear = findViewById(R.id.tvCrear);
        tvCrear.setOnClickListener(this);

        lbtnFb = findViewById(R.id.lbtnFb);
        lbtnFb.setOnClickListener(this);
        sbtnGoogle = findViewById(R.id.sbtnGoogle);
        sbtnGoogle.setOnClickListener(this);

        viewModel = new ViewModel();
        viewModel.setView(this);

        if (viewModel.isLogged()) {
            startHomeActivity();
        }
    }

    /**
     * Invoca a los distintos métodos (acciones) que se pueden llevar a cabo en elementos
     * interactivos de la actividad.
     *
     * @param v view que invoca al método (handler).
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
     * Guarda las SharedPreferences del email y la contraseña para poder iniciar sesión con la
     * huella y lanza la actividad de inicio de la aplicación
     *
     * @param email    es el email del usuario.
     * @param password es la contraseña del usuario.
     */
    private void saveBiometric(String email, String password) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.fast_start)
                .setMessage(R.string.fingertip_start)
                .setPositiveButton(R.string.afirmativo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewModel.emailSignIn(email, password, true);
                    }
                })
                .setNegativeButton(R.string.negativo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewModel.emailSignIn(email, password, false);
                    }
                })
                .show();
    }

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

    /**
     * Lanza la actividad de inicio de la aplicación.
     */
    private void startHomeActivity() {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.putExtra(IViewModel.K_VIEWMODEL, viewModel);
        startActivity(homeIntent);

        finish();

        overridePendingTransition(R.anim.anim, R.anim.zoom_back);
        // Corregir con los cambios a MVVM
        // Toast.makeText(this, "Bienvenido de nuevo " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
    }

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

    private void askForFingertip() {
        handler = new Handler();
        executor = new Executor() {
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
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(SignInActivity.this,
                        R.string.no_biometric_hw, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                BiometricPrompt.CryptoObject autenticacion = result.getCryptoObject();
                fingertipSignIn();
            }

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
     * Lanza la a
     */
    private void fingertipSignIn() {
        String email = viewModel.getEmail();
        String password = viewModel.getPassword();
        tietEmail.setText(email);
        tietPassword.setText(password);

        loadingMessage(false);

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this,
                    R.string.previous_access_fingertip, Toast.LENGTH_SHORT).show();
        } else {
            viewModel.emailSignIn(email, password, false);
        }
    }

    /**
     * Lanza la actividad de registro en la aplicación.
     */
    private void startSignUpActivity() {
        Intent signUpIntent = new Intent(this, SignUpActivity.class);
        signUpIntent.putExtra(IViewModel.K_VIEWMODEL, viewModel);
        startActivity(signUpIntent);

        finish();

        overridePendingTransition(R.anim.anim, R.anim.zoom_back);
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

    // IView implementation

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

}
