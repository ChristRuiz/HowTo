package com.example.tfg;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class AsignarPerfil extends AppCompatActivity implements View.OnClickListener, KeyEvent.Callback, View.OnFocusChangeListener {

    ImageView foto;
    String proveedor = "";
    String url = "https://firebasestorage.googleapis.com/v0/b/tfg-social-media.appspot.com/o/user.png?alt=media&token=8b56312b-02f7-4e63-9841-87d9857591f4";
    boolean valido = true;
    RelativeLayout vista;
    FirebaseAuth mAuth;
    EditText nombre_completo, username;
    Button cancelar, aceptar;
    private GoogleSignInClient mGoogleSignInClient;
    private static String APP_DIRECTORY = "HowTo/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "HowTo";
    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE = 300;
    private String mPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.asignar_perfil);

        vista = findViewById(R.id.relative);
        foto = findViewById(R.id.foto_perfil);
        cancelar = findViewById(R.id.cancelar);
        aceptar = findViewById(R.id.aceptar);
        nombre_completo = findViewById(R.id.nombre_completo);
        username = findViewById(R.id.nombre_usuario);
        nombre_completo.setOnFocusChangeListener(this);
        username.setOnFocusChangeListener(this);
        cancelar.setOnClickListener(this);
        aceptar.setOnClickListener(this);
        nombre_completo.addTextChangedListener(comprobar_validacion);
        username.addTextChangedListener(comprobar_validacion);
        foto.setOnClickListener(this);

        //Permitimos cambiar la foto si ha aceptado los permisos
        if (mayRequestStoragePermission()) {
            foto.setEnabled(true);
        } else {
            foto.setEnabled(false);
        }

        // Inicializacimos la Autentificación de Firebase
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        cargar_datos_usuario();

        //Validamos los datos, para indicar que campos tiene que corregir o rellenar
        validar();
    }


    /**
     * Cargamos todos los datos del usuario que necesitamos
     */
    private void cargar_datos_usuario() {
        FirebaseUser usuario = mAuth.getCurrentUser();

        Uri url = usuario.getPhotoUrl();
        String nombre = usuario.getDisplayName();

        if (url != null) {
            //Cargamos su foto
            Picasso.get().load(usuario.getPhotoUrl()).placeholder(R.drawable.user).into(foto);
            this.url = url.toString();
        }

        if (nombre != null) {
            //Cargamos su nombre
            nombre_completo.setText(nombre);
        }
    }

    /**
     * Detecta cada vez que se escribe un caracter
     */
    private final TextWatcher comprobar_validacion = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //Valida los datos de los campos de texto
            validar();
        }

        public void afterTextChanged(Editable s) {
        }
    };

    /**
     * Valida todos los campos de texto que tiene que rellenar el usuario
     */
    private void validar() {

        String nombre_usuario = username.getText().toString();

        valido = true;
        aceptar.setEnabled(false);

        //Realizamos una consulta a la bbdd
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Usuarios");
        Query query = myRef.orderByChild("username").equalTo(nombre_usuario);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Validaciones necesarias de nombre y nickname
                Object value = dataSnapshot.getValue();

                //Comprobamos si existe ese username
                if (value != null) {
                    username.setError("Ya esta registrado ese nombre de usuario!");

                    aceptar.setEnabled(false);
                } else {
                    String nombre = nombre_completo.getText().toString();
                    String nombre_usuario = username.getText().toString();

                    //Validaciones de que no este vacio y de espacios
                    if (TextUtils.isEmpty(nombre)) {
                        nombre_completo.setError("Debes rellenar este campo!");
                    } else if (TextUtils.isEmpty(nombre_usuario)) {
                        username.setError("Debes rellenar este campo!");
                    } else if (nombre.charAt(0) == ' ') {
                        nombre_completo.setError("No puede contener espacios al principio!");
                    } else if (nombre_usuario.matches(".*\\s.*")) {
                        username.setError("No puede tener espacios!");
                    }

                    //Comprueba si ha habido algun error durante la validacion
                    if (username.getError() == null && nombre_completo.getError() == null) {
                        aceptar.setEnabled(true);
                    } else {
                        aceptar.setEnabled(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                valido = false;
            }
        });

    }


    /**
     * Comprueba que tienes requisitos para utilizar la camara y acceder al almacenamiento del dispositivo
     * @return
     */
    private boolean mayRequestStoragePermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        if ((checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED))
            return true;

        if ((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) || (shouldShowRequestPermissionRationale(CAMERA))) {
            Snackbar.make(vista, "Los permisos son necesarios para poder usar la aplicación",
                    Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
                }
            });
        } else {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
        }

        return false;
    }

    /**
     * Comprueba si has aceptado los permisos
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(AsignarPerfil.this, "Permisos aceptados", Toast.LENGTH_SHORT).show();
                foto.setEnabled(true);
            }
        } else {
            showExplanation();
        }
    }

    /**
     * Te muestra que tienes los permisos denegados y te habre la ventana para acceder a los permisos de la app
     */
    private void showExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AsignarPerfil.this);
        builder.setTitle("Permisos denegados");
        builder.setMessage("Para usar las funciones de la app necesitas aceptar los permisos");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        builder.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.foto_perfil:
                //Muestra un menu con las opciones para cargar una foto
                showOptions();
                break;
            case R.id.cancelar:
                //Cerramos la sesión del usuario
                mAuth.signOut();
                mGoogleSignInClient.signOut();

                //Vuelve a la pestaña de login
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            case R.id.aceptar:
                //Comprueba que esta activado el boton
                if (aceptar.isEnabled()) {

                    //Obtenemos el usuario actual
                    FirebaseUser user = mAuth.getCurrentUser();

                    //Obtenemos el proveedor del login
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Usuarios");
                    Query query = myRef.child(user.getUid()).child("proveedor");
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Object value = dataSnapshot.getValue();

                            //Comprobamos si esta puesto el proveedor
                            if (value != null) {
                                proveedor = value.toString();
                            } else {
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                        }
                    });

                    //Se sube la foto del usuario a la bbdd
                    subir_foto();


                }
                break;
        }
    }

    /**
     * Mostramos un dialogo emergente con las opciones para subir una foto
     */
    private void showOptions() {
        final CharSequence[] option = {"Tomar foto", "Elegir de galeria", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(AsignarPerfil.this);
        builder.setTitle("Elige una opción");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (option[which] == "Tomar foto") {
                    openCamera();
                } else if (option[which] == "Elegir de galeria") {
                    //Abre la galeria
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent, "Selecciona una imagen"), SELECT_PICTURE);
                } else {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }

    /**
     * Metodo que comprueba si hay conexion a internet
     * @return
     */
    private boolean conexion_internet() {
        ConnectivityManager cm;
        NetworkInfo ni;
        cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        ni = cm.getActiveNetworkInfo();
        boolean tipoConexion1 = false;
        boolean tipoConexion2 = false;
        boolean conexion;

        //Comprueba de primeras si recibe algo de conexión
        if (ni != null) {
            ConnectivityManager connManager1 = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager1.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            ConnectivityManager connManager2 = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobile = connManager2.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            //Comprueba si hay wifi
            if (mWifi.isConnected()) {
                tipoConexion1 = true;
            }

            //Comprueba si hay datos
            if (mMobile.isConnected()) {
                tipoConexion2 = true;
            }

            //Si hay datos o wifi, significa que hay conexion a internet
            if (tipoConexion1 == true || tipoConexion2 == true) {
                conexion = true;
            } else {
                conexion = false;
            }
        } else {
            conexion = false;
        }

        return conexion;
    }


    /**
     * Se sube la foto del usuario
     */
    private void subir_foto() {
        // Estas conectado a internet usando wifi o redes moviles, puedes enviar tus datos
        if (conexion_internet()) {
            //Entramos en el servidor de firebase de almacenamiento
            FirebaseStorage storage = FirebaseStorage.getInstance();

            //Creamos una carpeta y generamos un archivo
            final StorageReference referencia = storage.getReference().child("usuarios/" + mAuth.getCurrentUser().getUid() + "/foto.jpg");
            foto.setDrawingCacheEnabled(true);
            foto.buildDrawingCache();

            //Obtenemos la imagen del imageview
            Bitmap bitmap = ((BitmapDrawable) foto.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            //Comprimimos la imagen e indicamos con que calidad queremos guardarla (cuanta mas calidad, mas rendimiento requiere)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);

            //Pasamos la imagen a bytes
            byte[] data = baos.toByteArray();

            //Subimos la imagen
            UploadTask uploadTask = referencia.putBytes(data);
            final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
            progressDialog.setMessage("Subiendo archivo...");
            progressDialog.show();
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        //Si no se ha podido subir por algun error se vuelve a intentar hasta que se suba
                        subir_foto();
                    }

                    referencia.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //Obtenemos la url del archivo subido
                            url = uri.toString();
                            progressDialog.dismiss();

                            //Despues de obtener la url llamamos al metodo que se encarga de actualizar el perfil del usuario
                            actualizar_perfil();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //Si no se ha podido subir por algun error se vuelve a intentar hasta que se suba
                            subir_foto();
                            progressDialog.dismiss();
                        }
                    });
                    return referencia.getDownloadUrl();
                }
            });
        } else {
            /* No estas conectado a internet */
            Snackbar.make(this.vista, "No tienes conexion a Internet", 5000).show();
        }
    }

    /**
     * Actualizamos el perfil del usuario
     */
    private void actualizar_perfil() {
        //Actualizamos el perfil
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Completando tu perfil...");
        progressDialog.show();

        //Actualizamos el perfil
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(nombre_completo.getText().toString()).setPhotoUri(Uri.parse(url)).build();
        FirebaseUser user = mAuth.getCurrentUser();
        user.updateProfile(profileUpdate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Guardamos en la base de datos todos los datos del usuario
                            FirebaseUser user = mAuth.getCurrentUser();
                            String correo;
                            if (user.getEmail() == null) {
                                correo = user.getProviderData().get(1).getEmail();
                            } else {
                                correo = user.getEmail();
                            }
                            FirebaseDatabase.getInstance().getReference("Usuarios")
                                    .child(user.getUid()).setValue(new Usuario(correo, nombre_completo.getText().toString(), user.getPhotoUrl().toString(), proveedor, username.getText().toString()));
                            progressDialog.dismiss();

                            //Quitamos todos los errores
                            username.setError(null);
                            nombre_completo.setError(null);

                            //Pasamos a la siguiente ventana
                            Intent i = new Intent(AsignarPerfil.this, Inicio.class);
                            startActivity(i);
                            finish();
                        }
                    }
                });
    }

    /**
     * Metodo que se encarga de abrir la camara y guardar la foto en una carpeta
     */
    private void openCamera() {
        File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
        boolean isDirectoryCreated = file.exists();

        //Si el directorio no se ha creado, se crea
        if (!isDirectoryCreated) {
            isDirectoryCreated = file.mkdirs();
        }

        if (isDirectoryCreated) {
            Long timestamp = System.currentTimeMillis() / 1000;
            String imageName = timestamp.toString() + ".jpg";

            mPath = Environment.getExternalStorageDirectory() + File.separator + MEDIA_DIRECTORY
                    + File.separator + imageName;

            File newFile = new File(mPath);
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
            startActivityForResult(intent, PHOTO_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_CODE:
                    //Abre la camara
                    MediaScannerConnection.scanFile(this,
                            new String[]{mPath}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("ExternalStorage", "Scanned " + path + ":");
                                    Log.i("ExternalStorage", "-> Uri = " + uri);
                                }
                            });


                    //Ponemos en ImageView la imagen realizada
                    Bitmap bitmap = BitmapFactory.decodeFile(mPath);
                    foto.setImageBitmap(bitmap);
                    break;
                case SELECT_PICTURE:
                    //Ponemos en ImageView la imagen seleccionada
                    Uri path = data.getData();
                    foto.setImageURI(path);
                    break;

            }
        }
    }

    /**
     * Despues de presionar una tecla se valida los datos
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        validar();
        return true;
    }

    /**
     * Cuando se cambie de campo se valida los datos
     * @param v
     * @param hasFocus
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        validar();
    }
}
