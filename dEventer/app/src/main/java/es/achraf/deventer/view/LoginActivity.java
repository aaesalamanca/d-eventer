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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
import java.util.concurrent.Executor;

import es.achraf.deventer.Inicio;
import es.achraf.deventer.R;
import es.achraf.deventer.Register;
import es.achraf.deventer.viewmodel.IViewModel;
import es.achraf.deventer.viewmodel.ViewModel;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, IView {

    // Fields
    private TextInputEditText tietEmail;
    private TextInputEditText tietPassword;

    private MaterialButton mbtnEntrar;

    private ImageButton ibtnHuella;

    private Handler handler;
    private Executor executor;

    private ProgressBar progressBar;
    private TextView tvCarga;
    private TextView tvCrear;

    private LoginButton lbtnFb;
    private SignInButton sbtnGoogle;

    private IViewModel viewModel;

    private FirebaseAuth mAuth;

    // Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

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

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mbtnSignIn:
                emailSignIn();
                break;
            case R.id.tvCrear:
                irRegistro();
                break;
            case R.id.lbtnFb:
                signInFb();
                break;
            case R.id.sbtnGoogle:
                signInGoogle();
                break;
            case R.id.ibtnHuella:
                comprobarHuella();
                break;
        }
    }

    public void emailSignIn() {
        String email = tietEmail.getText().toString();
        String password = tietPassword.getText().toString();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            saveBiometric(email, password);
            progressBar.setVisibility(View.VISIBLE);
            tvCarga.setVisibility(View.VISIBLE);
        } else
            Toast.makeText(this, R.string.faltan_campos, Toast.LENGTH_SHORT).show();
    }

    public void saveBiometric(String email, String password) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.inicio_rapido)
                .setMessage(R.string.msg_inicio_rapido)
                .setPositiveButton(R.string.afirmativo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(ViewModel.SPK_EMAIL, email);
                        editor.putString(ViewModel.SPK_PASSWORD, password);
                        editor.commit();

                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                tvCarga.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    updateUI(null);
                                    Toast.makeText(LoginActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }
                })
                .setNegativeButton(R.string.negativo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                tvCarga.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    updateUI(null);
                                    Toast.makeText(LoginActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .show();
    }

    @Override
    public SharedPreferences getPreferences(int mode) {
        return getPreferences(mode);
    }

    public void recuperarInicioRapido() {
        SharedPreferences sharedPreferences = this.getPreferences(MODE_PRIVATE);
        String correo = sharedPreferences.getString(ViewModel.SPK_EMAIL, "");
        String pass = sharedPreferences.getString(ViewModel.SPK_PASSWORD, "");
        tietEmail.setText(correo);
        tietPassword.setText(pass);

        if (TextUtils.isEmpty(correo) || TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Debe acceder almenos una vez para poder guardar sus datos de acceso.", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.signInWithEmailAndPassword(correo, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                    tvCarga.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);

                    } else {
                        updateUI(null);
                        Toast.makeText(LoginActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    public void irRegistro() {
        Intent intentRegistro = new Intent(LoginActivity.this, Register.class);
        startActivity(intentRegistro);
        overridePendingTransition(R.anim.anim, R.anim.zoom_back);
    }


    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intentInicio = new Intent(LoginActivity.this, Inicio.class);
            startActivity(intentInicio);
            finish();
            overridePendingTransition(R.anim.anim, R.anim.zoom_back);
            Toast.makeText(this, "Bienvenido de nuevo " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
        }
    }


    public void comprobarHuella() {
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                pedirHuella();
                break;

            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this, "Las características biométricas no están disponibles actualmente", Toast.LENGTH_SHORT).show();
                break;

            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "No hay ninguna huella registrada", Toast.LENGTH_SHORT).show();
                break;

            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "Este dispositivo no tiene huella", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void pedirHuella() {
        handler = new Handler();
        executor = new Executor() {
            @Override
            public void execute(Runnable command) {
                handler.post(command);
            }
        };

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Acceso biometrico dEventer")
                .setSubtitle("Introduzca su huella para acceder")
                .setNegativeButtonText("Cancelar")
                .setConfirmationRequired(false)
                .build();

        BiometricPrompt biometricPrompt = new BiometricPrompt(LoginActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);

                Toast.makeText(LoginActivity.this, "Este dispositivo no tiene huella", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                BiometricPrompt.CryptoObject autenticacion = result.getCryptoObject();
                recuperarInicioRapido();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(LoginActivity.this, "La autenticacion ha fallado.", Toast.LENGTH_SHORT).show();
            }
        });
        biometricPrompt.authenticate(promptInfo);
    }


    private void signInFb() {

    }

    private void signInGoogle() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
