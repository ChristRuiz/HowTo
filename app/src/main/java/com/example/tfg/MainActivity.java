package com.example.tfg;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView registrarse;
    EditText email,password;
    Button login;
    ImageButton google,facebook,twitter;
    private final int RC_SIGN_IN = 0;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    private boolean registrado = false;
    String username = null;
    OAuthProvider.Builder provider;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registrarse = findViewById(R.id.registrarse);
        login = findViewById(R.id.login);
        email = findViewById(R.id.email);
        google = findViewById(R.id.google);
        twitter = findViewById(R.id.twitter);
        password = findViewById(R.id.password);
        facebook = findViewById(R.id.facebook);

        //Ponemos negrita al texto
        String registro = "¿No tienes cuenta? <b>Registrate</b>";
        registrarse.setText(Html.fromHtml(registro));

        registrarse.setOnClickListener(this);
        login.setOnClickListener(this);
        twitter.setOnClickListener(this);
        google.setOnClickListener(this);
        facebook.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Indicamos el proveedor de twitter
        provider = OAuthProvider.newBuilder("twitter.com");
        provider.addCustomParameter("lang", "es");
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(MainActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        // Inicializacimos la Autentificación de Firebase
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.registrarse:
                //Abrimos la activity del registro
                Intent i = new Intent(MainActivity.this,Registrarse.class);
                startActivity(i);
                finish();
                break;
            case R.id.login:
                //Obtenemos el email y la contraseña introducidos
                String email = this.email.getText().toString().trim();
                String password = this.password.getText().toString().trim();
                final ProgressDialog progressDialog = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
                progressDialog.setMessage("Iniciando Sesión...");
                progressDialog.show();
                //Comprobamos si el login es valido o no
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Se ha podido iniciar sesion
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    progressDialog.dismiss();
                                    subir_datos("Email");
                                    perfil_registrado();
                                } else {
                                    // No se ha podido iniciar sesion
                                    Log.w("Login", "signInWithEmail:failure", task.getException());
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "No se ha podido iniciar sesión",
                                            Toast.LENGTH_SHORT).show();
                                }

                                // ...
                            }
                        });

                break;
            case R.id.google:
                //Iniciamos el metodo que se encarga del inicio de sesion con google
                signInGoogle();
                break;
            case R.id.facebook:
                //Iniciamos el inicio de sesion con Facebook con los permisos necesarios de la app
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
                break;
            case R.id.twitter:
                //Realizamos el login con twitter
                mAuth.startActivityForSignInWithProvider(this, provider.build())
                        .addOnSuccessListener(
                                new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {

                                        //Subimos los datos del usuario
                                        subir_datos("Twitter");

                                        //Comprobamos si se habia registrado antes
                                        perfil_registrado();
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                break;
        }
    }

    /**
     * Este metodo se llama al iniciar la actiivity
     */
    @Override
    public void onStart() {
        super.onStart();
        //Obtenemos el usuario actuak
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //Comprobamos si el usario actual se habia registrado
        if(currentUser != null){
            perfil_registrado();
        }
    }

    /**
     * Comprueba si el usuario esta registrado
     */
    public void perfil_registrado(){
        //Obtenemos el uid del usuario
        String uid = mAuth.getCurrentUser().getUid();

        //Realizamos una consulta a la bbdd
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Usuarios");
        Query query = myRef.child(uid).child("username");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue();

                //Comprobamos si existe el campo username
                if(value!=null) {
                    //Le mandamos a la pantalla principal
                    Intent i = new Intent(MainActivity.this,Inicio.class);
                    startActivity(i);
                    finish();
                } else {
                    //Le mandamos a la pantalla para comletar su perfil
                    Intent i = new Intent(MainActivity.this,AsignarPerfil.class);
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    /**
     * Subimos los datos del usuario a la bbdd
     * @param proveedor
     */
    public void subir_datos(final String proveedor){
        //Obtenemos el uid del usuario
        String uid = mAuth.getCurrentUser().getUid();

        //Realizamos una consulta en la bbdd
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Usuarios");
        Query query = myRef.child(uid).child("username");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Object value = dataSnapshot.getValue();
                String correo;

                //Comprobamos si existe el campo username
                if(value!=null) {
                    username = value.toString();

                    FirebaseUser user = mAuth.getCurrentUser();
                    //Si es un login de twitter, debemos obtener el email de otra forma
                    if(user.getEmail()==null){
                        correo = user.getProviderData().get(1).getEmail();
                    } else {
                        correo = user.getEmail();
                    }

                    String url;
                    if(user.getPhotoUrl()==null){
                        url = null;
                    } else {
                        url = user.getPhotoUrl().toString();
                    }

                    //Creamos un usuario
                    Usuario usuario_nuevo = new Usuario(correo,user.getDisplayName(),url,proveedor,username);

                    //Subimos el usuario a la bbdd
                    FirebaseDatabase.getInstance().getReference("Usuarios")
                            .child(user.getUid())
                            .setValue(usuario_nuevo);
                } else {

                    FirebaseUser user = mAuth.getCurrentUser();

                    if(user.getEmail()==null){
                        correo = user.getProviderData().get(1).getEmail();
                    } else {
                        correo = user.getEmail();
                    }

                    String url;
                    if(user.getPhotoUrl()==null){
                        url = null;
                    } else {
                        url = user.getPhotoUrl().toString();
                    }

                    //Creamos un usuario
                    Usuario usuario_nuevo = new Usuario(correo,user.getDisplayName(),url,proveedor,null);

                    //Subimos el usuario a la bbdd
                    FirebaseDatabase.getInstance().getReference("Usuarios")
                            .child(user.getUid())
                            .setValue(usuario_nuevo);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    /**
     * Login de Google
     */
    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Resultado del intent de login con Google
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // El login funciono correctamente
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                //El login no funciono correctamente
                Log.w("Error", "Google sign in failed", e);
                // ...
            }
        }
        if(mCallbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

    /**
     * Obtenemos los datos de google
     * @param acct
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //Subimos los datos del login a la bbdd
                            subir_datos("Google");

                            //Comprobamos si el perfil esta registrado o no
                            perfil_registrado();

                        }

                    }
                });
    }

    /**
     * Obtenemos los datos del login de Facebook
     * @param token
     */
    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            //Subimos los datos del login a la bbdd
                            subir_datos("Facebook");
                            //Comprobamos si el perfil esta registrado o no
                            perfil_registrado();

                        } else {
                            // El login ha fallado
                            Log.w("Facebook", "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
}
