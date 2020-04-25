package es.achraf.deventer;

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

import com.facebook.login.LoginManager;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private LoginButton btnEntrarFacebook;
    private SignInButton btnEntrarGoogle;

    private TextView txtCrearCuenta;
    private TextInputEditText txtCorreo;
    private TextInputEditText txtPassword;
    private MaterialButton btnEntrar;

    private ImageButton btnHuella;

    private ProgressBar progressBar;
    private TextView txtCargando;

    private FirebaseAuth mAuth;

    //huella biometrica
    private Handler handler;
    private Executor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnEntrar = findViewById(R.id.mbtnEntrar);
        btnEntrar.setOnClickListener(this);

        btnEntrarFacebook = findViewById(R.id.lgnBtnLoginFb);
        this.btnEntrarFacebook.setOnClickListener(this);

        btnEntrarGoogle = findViewById(R.id.btnEntrarGoogle);
        this.btnEntrarGoogle.setOnClickListener(this);

        txtCorreo = findViewById(R.id.tietEmail);
        txtPassword = findViewById(R.id.tietPassword);

        txtCrearCuenta = findViewById(R.id.tvCrearCuenta);
        this.txtCrearCuenta.setOnClickListener(this);

        this.progressBar = findViewById(R.id.progressBar);
        this.txtCargando = findViewById(R.id.tvCarga);

        this.btnHuella = findViewById(R.id.ibtnHuella);
        this.btnHuella.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    public void guardarInicioRapido(String correo, String pass) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Inicio rápido")
                .setMessage("¿Desea la proxima vez acceder mediante sus datos biométricos?")
                .setPositiveButton("DE ACUERDO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("email", correo);
                        editor.putString("pass", pass);
                        editor.commit();

                        mAuth.signInWithEmailAndPassword(correo, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                txtCargando.setVisibility(View.GONE);
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
                .setNegativeButton("NO, GRACIAS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signInWithEmailAndPassword(correo, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                txtCargando.setVisibility(View.GONE);
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

    public void recuperarInicioRapido() {
        SharedPreferences sharedPreferences = this.getPreferences(MODE_PRIVATE);
        String correo = sharedPreferences.getString("email", "");
        String pass = sharedPreferences.getString("pass", "");
        txtCorreo.setText(correo);
        txtPassword.setText(pass);

        if (TextUtils.isEmpty(correo) || TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Debe acceder almenos una vez para poder guardar sus datos de acceso.", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.signInWithEmailAndPassword(correo, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                    txtCargando.setVisibility(View.GONE);
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

    public void entrarEmail() {
        String email = txtCorreo.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            guardarInicioRapido(email, password);
            progressBar.setVisibility(View.VISIBLE);
            txtCargando.setVisibility(View.VISIBLE);
        } else
            Toast.makeText(this, "Por favor, rellene todos los campos para continuar.", Toast.LENGTH_SHORT).show();
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
        this.handler = new Handler();
        this.executor = new Executor() {
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

    @Override
    public void onClick(View v) {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        switch (v.getId()) {
            case R.id.mbtnEntrar:
                entrarEmail();
                break;

            case R.id.tvCrearCuenta:
                irRegistro();
                break;

            case R.id.lgnBtnLoginFb:
                entrarFacebook();
                break;

            case R.id.btnEntrarGoogle:
                entrarGoogle();
                break;

            case R.id.ibtnHuella:
                comprobarHuella();
                break;
        }
    }


    private void entrarFacebook() {

    }

    private void entrarGoogle() {

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
