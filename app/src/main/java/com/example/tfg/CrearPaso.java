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
import android.graphics.drawable.Drawable;
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
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class CrearPaso extends AppCompatActivity implements View.OnClickListener {

    private TextView contador_titulo, contador_descripcion;
    private EditText titulo_paso, descripcion_paso;
    private ImageView foto;
    private Button crear;
    RelativeLayout vista;

    private static String APP_DIRECTORY = "HowTo/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "HowTo";
    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE = 300;
    private String mPath;
    private String uid;
    private int paso_actual;
    private FirebaseAuth mAuth;
    private boolean foto_subida = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crear_paso);

        //Obtenemos los datos de la activity anterior
        uid = getIntent().getExtras().getString("uid");
        paso_actual = getIntent().getExtras().getInt("paso");

        //Asignamos un toolbar personalizado
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Drawable cerrar = ContextCompat.getDrawable(this, R.drawable.ic_close_white_24dp);
        getSupportActionBar().setHomeAsUpIndicator(cerrar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(this);

        foto = findViewById(R.id.foto_paso);
        vista = findViewById(R.id.relative);
        contador_titulo = findViewById(R.id.contador_titulo_paso);
        crear = findViewById(R.id.crear);
        titulo_paso = findViewById(R.id.titulo_paso);
        contador_descripcion = findViewById(R.id.contador_descripcion_paso);
        descripcion_paso = findViewById(R.id.descripcion_paso);
        titulo_paso.addTextChangedListener(visualizador_titulo);
        descripcion_paso.addTextChangedListener(visualizador_descripcion);

        foto.setOnClickListener(this);
        crear.setOnClickListener(this);

        //Permitimos cambiar la foto si ha aceptado los permisos
        if (mayRequestStoragePermission()) {
            foto.setEnabled(true);
        } else {
            foto.setEnabled(false);
        }

        // Inicializacimos la Autentificación de Firebase
        mAuth = FirebaseAuth.getInstance();

    }
    /**
     * Detecta cada vez que se escribe un caracter
     */
    private final TextWatcher visualizador_titulo = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //Actualizamos el contador
            contador_titulo.setText(String.valueOf(s.length()) + "/50");

            //Ponemos colores para avisar mejor al usuario
            if (s.length() > 50) {
                String registro = "<font color=\"red\">" + String.valueOf(s.length()) + "</font>/50";
                contador_titulo.setText(Html.fromHtml(registro));
            } else if (s.length() == 50) {
                String registro = "<font color=\"green\">" + String.valueOf(s.length()) + "</font>/50";
                contador_titulo.setText(Html.fromHtml(registro));
            } else if (s.length() >= 40) {
                String registro = "<font color=\"yellow\">" + String.valueOf(s.length()) + "</font>/50";
                contador_titulo.setText(Html.fromHtml(registro));
            }
        }

        public void afterTextChanged(Editable s) {
        }
    };
    /**
     * Detecta cada vez que se escribe un caracter
     */
    private final TextWatcher visualizador_descripcion = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //Actualizamos el contador
            contador_descripcion.setText(String.valueOf(s.length()) + "/750");

            //Ponemos colores para avisar mejor al usuario
            if (s.length() > 750) {
                String registro = "<font color=\"red\">" + String.valueOf(s.length()) + "</font>/750";
                contador_descripcion.setText(Html.fromHtml(registro));
            } else if (s.length() == 750) {
                String registro = "<font color=\"green\">" + String.valueOf(s.length()) + "</font>/750";
                contador_descripcion.setText(Html.fromHtml(registro));
            } else if (s.length() >= 600) {
                String registro = "<font color=\"yellow\">" + String.valueOf(s.length()) + "</font>/750";
                contador_descripcion.setText(Html.fromHtml(registro));
            }
        }

        public void afterTextChanged(Editable s) {
        }
    };

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
                Toast.makeText(CrearPaso.this, "Permisos aceptados", Toast.LENGTH_SHORT).show();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(CrearPaso.this);
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
            case R.id.foto_paso:
                //Mostramos un menu para subir una foto
                showOptions();
                break;
            case R.id.crear:
                //Validamos los datos
                if (validar()) {
                    //Si estan bien subimos el foto para despues subir el paso
                    subir_foto();
                }
                break;
            default:
                //Si estan vacios los campos, volvemos hacia atras
                if(TextUtils.isEmpty(titulo_paso.getText().toString()) && TextUtils.isEmpty(descripcion_paso.getText().toString()) && !foto_subida){
                    onBackPressed();
                //Si no estan vacios mostramos un dialogo si quiere realmente salir o no
                } else {
                    AlertDialog dialog;
                    final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.AlertDialogCustom));
                    builder.setMessage("¿Deseas salir sin guardar los cambios?")
                            .setTitle("¿Estas seguro?");
                    builder.setPositiveButton("Salir sin Guardar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            onBackPressed();
                            finish();
                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    dialog = builder.create();
                    dialog.show();

                }
                break;
        }
    }

    /**
     * Mostramos un dialogo emergente con las opciones para subir una foto
     */
    private void showOptions() {
        final CharSequence[] option = {"Tomar foto", "Elegir de galeria", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(CrearPaso.this);
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
                    foto_subida = true;
                    break;
                case SELECT_PICTURE:
                    //Ponemos en ImageView la imagen seleccionada
                    Uri path = data.getData();
                    foto.setImageURI(path);
                    foto_subida = true;
                    break;

            }
        }
    }

    /**
     * Validamos los datos de los campos
     * @return
     */
    private boolean validar() {
        String titulo = titulo_paso.getText().toString().trim();
        String descripcion = descripcion_paso.getText().toString().trim();
        boolean valido = true;

        //Comprobamos que no sea vacio el titulo del paso y que no se pase de los caracteres indicados
        if (TextUtils.isEmpty(titulo)) {
            valido = false;
            titulo_paso.setError("Debes rellenar este campo!");
        } else if (titulo.length() > 50) {
            valido = false;
            titulo_paso.setError("Maximo 50 carácteres!");
        } else if (descripcion.length() > 750) {
            valido = false;
            descripcion_paso.setError("Maximo 750 carácteres!");
        }

        return valido;
    }

    /**
     * Sube la imagen al servidor
     */
    private void subir_foto() {
        // Estas conectado a internet usando wifi o redes moviles, puedes enviar tus datos
        if (conexion_internet()) {
            //Comprobamos si se ha subido alguna foto
            if (foto_subida) {
                //Abrimos el servidor de almacenamiento de firebase
                FirebaseStorage storage = FirebaseStorage.getInstance();

                //Creamos una carpeta
                final StorageReference referencia = storage.getReference().child("howto/" + mAuth.getCurrentUser().getUid() + "/" + uid + "/" + String.valueOf(paso_actual) + ".jpg");
                foto.setDrawingCacheEnabled(true);
                foto.buildDrawingCache();

                //Obtenemos la imagen del imageview
                Bitmap bitmap = ((BitmapDrawable) foto.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                //Comprimimos la imagen e indicamos con que calidad queremos guardarla (cuanta mas calidad, mas rendimiento requiere)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                //Pasamos la imagen a bytes
                byte[] data = baos.toByteArray();

                //Subimos la imagen
                UploadTask uploadTask = referencia.putBytes(data);
                final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
                progressDialog.setMessage("Subiendo el paso...");
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
                                //Obtenemos la url de la imagen subida
                                String url = uri.toString();

                                //Se sube el howto al servidor con imagen
                                subir_paso(url);
                                progressDialog.dismiss();
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
                //Se sube el howto al servidor sin imagen
                subir_paso("");
            }
        } else {
            /* No estas conectado a internet */
            Snackbar.make(this.vista, "No tienes conexion a Internet", 5000).show();
        }
    }

    /**
     * Subimos un paso a la bbdd
     * @param url
     */
    private void subir_paso(String url){

        Paso paso;
        //Creamos un paso sin imagen o con imagen, dependiendo de si ha subido alguna
        if(TextUtils.isEmpty(url)){
            paso = new Paso(titulo_paso.getText().toString().trim(),descripcion_paso.getText().toString().trim(),null);
        } else {
            paso = new Paso(titulo_paso.getText().toString().trim(),descripcion_paso.getText().toString().trim(),url);
        }
        //Guardamos el bbdd el paso
        FirebaseDatabase.getInstance().getReference("Borradores").child(uid).child("pasos").child(String.valueOf(paso_actual)).setValue(paso);

        //Volvemos a la activity anterior
        onBackPressed();
        finish();
    }
}
