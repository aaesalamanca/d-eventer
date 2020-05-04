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

import java.util.Calendar;

import es.achraf.deventer.R;
import es.achraf.deventer.viewmodel.IViewModel;
import es.achraf.deventer.viewmodel.ViewModelSignUp;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    // Fields
    private static final int AGE_LIMIT = 18;

    private IViewModel.SignUp vmsu;

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
        setContentView(R.layout.activity_sign_up);
        init();
    }

    /**
     * Inicializa los elementos de la actividad.
     */
    private void init() {
        vmsu = new ViewModelSignUp();
        vmsu.setSignUpCompleteListener(signedUp -> {
            loadingMessage(false);

            if (signedUp) {
                Toast.makeText(this,
                        R.string.succeeded_sign_up, Toast.LENGTH_SHORT).show();
                startHomeActivity();
            } else {
                Toast.makeText(this,
                        R.string.failed_sign_up, Toast.LENGTH_SHORT).show();
            }
        });

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
                emailSignUp();
                break;
            case R.id.mbtnGoBack:
                startSignInActivity();
                break;
        }
    }

    /**
     * Muestra un DatePickerDialog para elegir la fecha de nacimiento del user.
     * <p>
     * Comprueba que el user es mayor de age —muestra un Snackbar en caso contrario— y muestra
     * esa fecha en el TextInputEditText de la fecha de nacimiento.
     */
    private void showBirthDialog() {
        // Obtención de la fecha actual para comparar con la fecha de nacimiento del user.
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

    private void emailSignUp() {
        String name = tietName.getText().toString();
        String email = tietEmail.getText().toString();
        String password = tietPassword.getText().toString();
        String repeatPassword = tietRepeatPassword.getText().toString();
        String postalCode = tietPostalCode.getText().toString();
        String birth = tietBirth.getText().toString();

        if (isValidForm(name, email, password, repeatPassword, postalCode, birth)) {
            if (isValidPassword(password, repeatPassword)) {
                if (isValidRadioGroup()) {
                    loadingMessage(true);

                    String sex = "";
                    if (rbMan.isChecked()) {
                        sex = getString(R.string.man);
                    } else if (rbWoman.isChecked()) {
                        sex = getString(R.string.woman);
                    } else {
                        sex = getString(R.string.any);
                    }

                    vmsu.emailSignUp(email, password, name, String.valueOf(userAge),
                            sex, postalCode);
                } else {
                    Toast.makeText(this, R.string.sex_selection, Toast.LENGTH_SHORT).show();
                }
            } else {
                tietRepeatPassword.setError(getString(R.string.no_password_match));
            }
        } else {
            Toast.makeText(this, R.string.empty_fields, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Comprueba que el formulario es válido, es decir, que los campos de nombre, email, contraseña,
     * repetir contraseña y código postal no están vacíos.
     *
     * @param name           es el nombre del user.
     * @param email          es el email del user.
     * @param password       es la contraseña del user.
     * @param repeatPassword es la contraseña del user.
     * @param postalCode     es el código postal del user.
     * @param birth          es la fecha de nacimiento del user.
     * @return true si el formulario es válido y false en caso contrario.
     */
    private boolean isValidForm(String name, String email, String password, String repeatPassword,
                                String postalCode, String birth) {
        return !(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(repeatPassword) || TextUtils.isEmpty(postalCode)
                || TextUtils.isEmpty(birth));
    }

    /**
     * Comprueba que la contraseña es válida y coincide con la repetición.
     *
     * @param password       es la contraseña.
     * @param passwordRepeat es la contraseña repetida.
     * @return true si la contraseña es válida, false en caso contrario.
     */
    private boolean isValidPassword(String password, String passwordRepeat) {
        return password.equals(passwordRepeat);
    }

    /**
     * Comprueba que se ha seleccionado uno de los RadioButton de la elección de sex_stylized.
     *
     * @return true si se ha seleccionado un RadioButton, false si no hay ninguno seleccionado.
     */
    private boolean isValidRadioGroup() {
        return rbMan.isChecked() || rbWoman.isChecked() || rbAny.isChecked();
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
     * Lanza la actividad de login de la aplicación.
     */
    private void startSignInActivity() {
        Intent signUpIntent = new Intent(this, SignInActivity.class);
        startActivity(signUpIntent);

        overridePendingTransition(R.anim.anim, R.anim.zoom_back);

        finish();
    }

    /**
     * Lanza la actividad de inicio de la aplicación.
     */
    private void startHomeActivity() {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        startActivity(homeIntent);

        overridePendingTransition(R.anim.anim, R.anim.zoom_back);

        finish();
    }
}


