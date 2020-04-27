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
import es.achraf.deventer.viewmodel.ViewModel;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    // Fields
    private int edad = 0;

    private MaterialButton btnLogin;
    private MaterialButton btnRegistrarse;

    private TextInputEditText txtNombre;
    private TextInputEditText txtEmailRegistro;
    private TextInputEditText txtPasswordRegistro;
    private TextInputEditText txtRepetirContrasena;
    private TextInputEditText txtCp;
    private TextInputEditText txtFechaNacimiento;

    private ProgressBar progressBarRegistro;
    private TextView cargandoRegistro;

    private RadioButton rbHombre;
    private RadioButton rbMujer;
    private RadioButton rbOtro;

    private FirebaseAuth mAuth;

    private FirebaseDatabase firebaseDatabase;

    private IViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        this.btnLogin = findViewById(R.id.btnLogin);
        this.btnLogin.setOnClickListener(this);

        this.btnRegistrarse = findViewById(R.id.btnRegistrarse);
        this.btnRegistrarse.setOnClickListener(this);

        this.rbHombre = findViewById(R.id.rbHombre);
        this.rbMujer = findViewById(R.id.rbMujer);
        this.rbOtro = findViewById(R.id.rbOtro);


        this.txtNombre = findViewById(R.id.txtNombre);
        this.txtEmailRegistro = findViewById(R.id.txtEmailRegistro);
        this.txtPasswordRegistro = findViewById(R.id.txtPasswordRegistro);
        this.txtRepetirContrasena = findViewById(R.id.txtRepetirContrasena);
        this.txtCp = findViewById(R.id.txtCpRegistro);

        this.progressBarRegistro = findViewById(R.id.progressbarRegistro);
        this.cargandoRegistro = findViewById(R.id.cargandoRegistro);

        this.txtFechaNacimiento = findViewById(R.id.txtFechaNacimiento);
        this.txtFechaNacimiento.setOnClickListener(this);

        this.mAuth = FirebaseAuth.getInstance();
        this.firebaseDatabase = FirebaseDatabase.getInstance();

        viewModel = getIntent().getParcelableExtra(IViewModel.K_VIEWMODEL);
    }


    public void irLogin() {
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

    public void registrarse() {
        progressBarRegistro.setVisibility(View.VISIBLE);
        cargandoRegistro.setVisibility(View.VISIBLE);

        final String nombreYapellidos = txtNombre.getText().toString();
        final String email = txtEmailRegistro.getText().toString();
        final String password = txtPasswordRegistro.getText().toString();
        final String cp = txtCp.getText().toString();

        String passwordRepetida = txtRepetirContrasena.getText().toString();

        if (!TextUtils.isEmpty(nombreYapellidos) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)
                && !TextUtils.isEmpty(passwordRepetida) && !TextUtils.isEmpty(txtFechaNacimiento.getText())) {

            if (comprobarContrasenas(password, passwordRepetida)) {

                if (comprbarRadioButtons(rbHombre, rbMujer)) {

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                        progressBarRegistro.setVisibility(View.GONE);
                        cargandoRegistro.setVisibility(View.GONE);

                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            //codigo de registro

                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            updateUI(firebaseUser, nombreYapellidos);

                            String id = mAuth.getUid();
                            String eddad = String.valueOf(edad);

                            String sexo = "";
                            if (rbHombre.isChecked()) {
                                sexo = "hombre";
                            } else if (rbMujer.isChecked())
                                sexo = "mujer";
                            else if (rbOtro.isChecked())
                                sexo = "otro";

                            ArrayList<String> planesApuntados = new ArrayList<>();//array list de planes(ID) inicialmente vacío

                            User usuario = new User(id, nombreYapellidos, email, eddad, sexo, cp, planesApuntados);

                            DatabaseReference referenceDb = firebaseDatabase.getReference();
                            DatabaseReference crearUsuario = referenceDb.child(usuario.getID());
                            crearUsuario.setValue(usuario);

                        }
                    });

                } else {
                    Toast.makeText(this, "Debe seleccionar el sexo", Toast.LENGTH_SHORT).show();

                    progressBarRegistro.setVisibility(View.GONE);
                    cargandoRegistro.setVisibility(View.GONE);
                }


            } else {

                txtRepetirContrasena.setError("Las contraseñas no son iguales");

                progressBarRegistro.setVisibility(View.GONE);
                cargandoRegistro.setVisibility(View.GONE);
            }


        } else {
            Toast.makeText(this, "Por favor, rellene todos los campos para continuar", Toast.LENGTH_SHORT).show();

            progressBarRegistro.setVisibility(View.GONE);
            cargandoRegistro.setVisibility(View.GONE);
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


    public void dialogFechaNacimiento() {
        Calendar calendar = Calendar.getInstance();

        final int ano = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        final int dia = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                SignUpActivity.this, R.style.datepicker, (view, year, month, dayOfMonth) -> {
            int anoUser = year - ano;
            if (anoUser < 0)
                anoUser *= -1;
            edad = anoUser;
            if (anoUser < 18) {
                btnRegistrarse.setEnabled(false);
                txtFechaNacimiento.setError("Lo sentimos, pero la aplicación requiere que seas mayor de edad.");
                Snackbar.make(getWindow().getDecorView().getRootView(), txtFechaNacimiento.getError(), Snackbar.LENGTH_INDEFINITE)
                        .setActionTextColor(Color.RED).setBackgroundTint(Color.RED)
                        .setAction("Entendido", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(SignUpActivity.this, "Gracias por entendernos, HASTA PRONTO!", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            } else {
                btnRegistrarse.setEnabled(true);
                txtFechaNacimiento.setError(null);
            }


            Date fecha;
            try {
                fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).parse(dayOfMonth + "/" + (month + 1) + "/" + year);
                if (fecha != null) {
                    txtFechaNacimiento.setText(new SimpleDateFormat("dd/MM/yyyy").format(fecha));
                }


            } catch (ParseException e) {
                e.printStackTrace();
            }


        }, ano, mes, dia);

        datePickerDialog.setMessage("Fecha de nacimiento");
        datePickerDialog.setIcon(R.drawable.ic_home_black_24dp);
        datePickerDialog.show();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnLogin:
                irLogin();
                break;

            case R.id.btnRegistrarse:
                registrarse();
                break;

            case R.id.txtFechaNacimiento:
                dialogFechaNacimiento();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim, R.anim.zoom_back);
    }
}


