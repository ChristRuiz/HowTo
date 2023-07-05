package com.example.tfg;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class Registrarse extends AppCompatActivity implements View.OnClickListener {

    TextView login;
    EditText correo;
    EditText pass;
    EditText confirmPass;
    Button registrarse;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrarse);

        login = findViewById(R.id.loguearse);
        correo = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        registrarse = findViewById(R.id.registrarse);
        confirmPass = findViewById(R.id.confirmpassword);

        //Ponemos negrito al texto
        String registro = "¿Tienes ya cuenta? <b>Inicia Sesión!</b>";
        login.setText(Html.fromHtml(registro));
        login.setOnClickListener(this);
        registrarse.setOnClickListener(this);

        // Inicializacimos la Autentificación de Firebase
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.loguearse:
                //Volvemos a la activity anterior
                Intent i = new Intent(Registrarse.this,MainActivity.class);
                startActivity(i);
                finish();
                break;
            case R.id.registrarse:
                //Validamos el correo y la contraseña
                validarCorreoPass();
                break;
        }
    }

    public void validarCorreoPass(){
        // Reseteamos los errores
        correo.setError(null);
        pass.setError(null);

        // Limpiamos los campos de texto y recogemos sus valores
        String email = correo.getText().toString().trim();
        String password = pass.getText().toString().trim();
        String confirmpassword = confirmPass.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Validaciones necesarias de correo y contraseña
        if (TextUtils.isEmpty(email)) {
            correo.setError("Debes rellenar este campo!");
            focusView = correo;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        else if (TextUtils.isEmpty(password)){
            pass.setError("Debes rellenar este campo!");
            focusView = pass;
            cancel = true;
        }

        else if (!password.matches(".*\\d.*")){
            pass.setError("Debe contener minúsculas, mayusculas y números!");
            focusView = pass;
            cancel = true;
        }

        else if (!password.matches(".*[a-z].*")){
            pass.setError("Debe contener minúsculas, mayusculas y números!");
            focusView = pass;
            cancel = true;
        }

        else if (!password.matches(".*[A-Z].*")){
            pass.setError("Debe contener minúsculas, mayusculas y números!");
            focusView = pass;
            cancel = true;
        }

        else if (!password.matches(".{8,15}")){
            pass.setError("Debe tener entre 8 y 16 carácteres");
            focusView = pass;
            cancel = true;
        }

        else if (password.matches(".*\\s.*")){
            pass.setError("No puede tener espacios!");
            focusView = pass;
            cancel = true;
        }

        else if (password.matches(".*\\s.*")){
            pass.setError("No puede tener espacios!");
            focusView = pass;
            cancel = true;
        }

        else if (TextUtils.isEmpty(confirmpassword)) {
            confirmPass.setError("Debes rellenar este campo!");
            focusView = confirmPass;
            cancel = true;
        }

        else if (!password.equals(confirmpassword)){
            confirmPass.setError("No coinciden las contraseñas!");
            focusView = confirmPass;
            cancel = true;
        }

        //Si ha habido algun error
        if (cancel) {
            focusView.requestFocus();
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
            progressDialog.setMessage("Cargando...");
            progressDialog.show();

            //Creamos un usuario con correo y contraseña
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //Si el registro funciona se guarda el usuario en la bbdd
                                FirebaseUser user = mAuth.getCurrentUser();

                                FirebaseDatabase.getInstance().getReference("Usuarios")
                                        .child(user.getUid()).setValue(new Usuario(user.getEmail(),null,null,"Email",null));
                                progressDialog.dismiss();
                                Intent i = new Intent(Registrarse.this, AsignarPerfil.class);
                                startActivity(i);
                                finish();
                            } else {
                                //Si el registro falla,a se envia un mensaje al usuario
                                Log.w("Registro", "createUserWithEmail:failure", task.getException());
                                progressDialog.dismiss();
                                if(task.getException().getMessage().equals("The email address is badly formatted.")){
                                    Toast.makeText(Registrarse.this, "El email es invalido!",
                                            Toast.LENGTH_SHORT).show();
                                }
                                else if (task.getException().getMessage().equals("The email address is already in use by another account.")){
                                    Toast.makeText(Registrarse.this, "El email ya esta siendo utilizado por otra cuenta!",
                                            Toast.LENGTH_SHORT).show();
                                }

                            }

                            // ...
                        }
                    });
        }
    }


}
