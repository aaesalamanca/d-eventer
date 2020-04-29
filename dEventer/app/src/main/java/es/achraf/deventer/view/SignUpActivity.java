package es.achraf.deventer.view;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import es.achraf.deventer.R;
import es.achraf.deventer.model.User;
import es.achraf.deventer.viewmodel.IViewModel;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    // Fields
    private static final int AGE_LIMIT = 18;

    private int userAge;

    private TextInputEditText tietName;
    private TextInputEditText tietEmail;
    private TextInputEditText tietPassword;
    private TextInputEditText tietRepeatPassword;
    private TextInputEditText tietPostalCode;
    private TextInputEditText tietBirth;

    private RadioButton rbMan;
    private RadioButton rbWoman;
    private RadioButton rbAny;

    private ProgressBar pbLoading;
    private TextView tvLoading;

    private MaterialButton mbtnSignUp;

    private FirebaseAuth mAuth;

    private FirebaseDatabase firebaseDatabase;

    private IViewModel viewModel;

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
        setContentView(R.layout.activity_sign_up);
        init();
    }

    /**
     * Inicializa los elementos de la actividad.
     */
    private void init() {
        viewModel = getIntent().getParcelableExtra(IViewModel.K_VIEWMODEL);

        tietName = findViewById(R.id.tietName);
        tietEmail = findViewById(R.id.tietEmail);
        tietPassword = findViewById(R.id.tietPassword);
        tietRepeatPassword = findViewById(R.id.tietRepeatPassword);
        tietPostalCode = findViewById(R.id.tietPostalCode);
        tietBirth = findViewById(R.id.tietBirth);
        tietBirth.setOnClickListener(this);

        rbMan = findViewById(R.id.rbMan);
        rbWoman = findViewById(R.id.rbWoman);
        rbAny = findViewById(R.id.rbAny);

        pbLoading = findViewById(R.id.pbLoading);
        tvLoading = findViewById(R.id.tvLoading);

        findViewById(R.id.mbtnGoBack).setOnClickListener(this);
        mbtnSignUp = findViewById(R.id.mbtnSignUp);
        mbtnSignUp.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
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
            case R.id.tietBirth:
                showBirthDialog();
                break;
            case R.id.mbtnSignUp:
                startHomeActivity();
                break;
            case R.id.mbtnGoBack:
                startSignInActivity();
                break;
        }
    }

    /**
     * Muestra un DatePickerDialog para elegir la fecha de nacimiento del usuario.
     * <p>
     * Comprueba que el usuario es mayor de edad —muestra un Snackbar en caso contrario— y muestra
     * esa fecha en el TextInputEditText de la fecha de nacimiento.
     */
    public void showBirthDialog() {
        // Obtención de la fecha actual para comparar con la fecha de nacimiento del usuario.
        // Además, se utilizan para establecer la fecha inicial con la que se muestra el
        // DatePickerDialog.
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                SignUpActivity.this, R.style.datepicker, (view, year, month,
                                                          dayOfMonth) -> {
            userAge = currentYear - year;
            if (userAge < AGE_LIMIT) {
                mbtnSignUp.setEnabled(false);

                tietBirth.setError(getString(R.string.under_age));

                Snackbar.make(getWindow().getDecorView().getRootView(), tietBirth.getError(),
                        Snackbar.LENGTH_INDEFINITE)
                        .setActionTextColor(Color.RED).setBackgroundTint(Color.RED)
                        .setAction(R.string.agree, v -> Toast.makeText(this,
                                R.string.see_you, Toast.LENGTH_SHORT).show()).show();
            } else {
                mbtnSignUp.setEnabled(true);
                tietBirth.setError(null);
            }

            tietBirth.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
        }, currentYear, currentMonth, currentDay);
        datePickerDialog.setMessage(getString(R.string.birth));
        datePickerDialog.setIcon(R.drawable.ic_home_black_24dp);
        datePickerDialog.show();
    }

    public void startSignInActivity() {
        Intent intentLogin = new Intent(SignUpActivity.this, SignInActivity.class);
        startActivity(intentLogin);

        overridePendingTransition(R.anim.anim, R.anim.zoom_back);

        finish();
    }

    public boolean comprobarContrasenas(String pass, String repitePass) {
        return pass.equals(repitePass);
    }

    public boolean comprbarRadioButtons(RadioButton rbHombre, RadioButton rbMujer) {
        return rbHombre.isChecked() || rbMujer.isChecked();
    }

    public void startHomeActivity() {
        pbLoading.setVisibility(View.VISIBLE);
        tvLoading.setVisibility(View.VISIBLE);

        final String nombreYapellidos = tietName.getText().toString();
        final String email = tietEmail.getText().toString();
        final String password = tietPassword.getText().toString();
        final String cp = tietPostalCode.getText().toString();

        String passwordRepetida = tietRepeatPassword.getText().toString();

        if (!TextUtils.isEmpty(nombreYapellidos) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)
                && !TextUtils.isEmpty(passwordRepetida) && !TextUtils.isEmpty(tietBirth.getText())) {

            if (comprobarContrasenas(password, passwordRepetida)) {

                if (comprbarRadioButtons(rbMan, rbWoman)) {

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                        pbLoading.setVisibility(View.GONE);
                        tvLoading.setVisibility(View.GONE);

                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            //codigo de registro

                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            updateUI(firebaseUser, nombreYapellidos);

                            String id = mAuth.getUid();
                            String eddad = String.valueOf(userAge);

                            String sexo = "";
                            if (rbMan.isChecked()) {
                                sexo = "man";
                            } else if (rbWoman.isChecked())
                                sexo = "woman";
                            else if (rbAny.isChecked())
                                sexo = "any";

                            ArrayList<String> planesApuntados = new ArrayList<>();//array list de planes(ID) inicialmente vacío

                            User usuario = new User(id, nombreYapellidos, email, eddad, sexo, cp, planesApuntados);

                            DatabaseReference referenceDb = firebaseDatabase.getReference();
                            DatabaseReference crearUsuario = referenceDb.child(usuario.getID());
                            crearUsuario.setValue(usuario);

                        }
                    });

                } else {
                    Toast.makeText(this, "Debe seleccionar el sexo", Toast.LENGTH_SHORT).show();

                    pbLoading.setVisibility(View.GONE);
                    tvLoading.setVisibility(View.GONE);
                }


            } else {

                tietRepeatPassword.setError("Las contraseñas no son iguales");

                pbLoading.setVisibility(View.GONE);
                tvLoading.setVisibility(View.GONE);
            }


        } else {
            Toast.makeText(this, "Por favor, rellene todos los campos para continuar", Toast.LENGTH_SHORT).show();

            pbLoading.setVisibility(View.GONE);
            tvLoading.setVisibility(View.GONE);
        }

    }

    private void updateUI(FirebaseUser user, String nombre) {
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nombre)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful())
                            Toast.makeText(SignUpActivity.this, "Registrado con éxito", Toast.LENGTH_SHORT).show();
                    });

            Intent intentInicio = new Intent(SignUpActivity.this, HomeActivity.class);
            startActivity(intentInicio);
            overridePendingTransition(R.anim.anim, R.anim.zoom_back);
            finish();
        }
    }
}


