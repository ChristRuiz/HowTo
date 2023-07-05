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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class CrearHowTo extends AppCompatActivity implements View.OnClickListener{

    ExpandableListView pasos;
    TextView contador_titulo,contador_descripcion,añadir_paso;
    EditText titulo_howto,descripcion_howto;
    String texto_titulo = "";
    String texto_descripcion = "";
    RelativeLayout vista;
    Button crear;
    private ImageView foto;
    FirebaseAuth mAuth;

    private Map<Integer, Paso> listaPasos;
    private int paso_actual = 1;
    AdaptadorExpandableListView adaptador;
    String uid;
    private static String APP_DIRECTORY = "HowTo/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "HowTo";
    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE = 300;
    private String mPath;
    private boolean foto_subida = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_crear_how_to);

        crear = findViewById(R.id.crear);
        foto = findViewById(R.id.foto_howto);
        titulo_howto = findViewById(R.id.titulo_howto);
        contador_titulo = findViewById(R.id.contador_titulo_howto);
        descripcion_howto = findViewById(R.id.descripcion_howto);
        contador_descripcion = findViewById(R.id.contador_descripcion_howto);
        añadir_paso = findViewById(R.id.añadir_paso);
        vista = findViewById(R.id.vista);
        añadir_paso.setOnClickListener(this);
        crear.setOnClickListener(this);
        foto.setOnClickListener(this);
        pasos = findViewById(R.id.pasos);

        //Permitimos cambiar la foto si ha aceptado los permisos
        if (mayRequestStoragePermission()) {
            foto.setEnabled(true);
        } else {
            foto.setEnabled(false);
        }

        // Inicializacimos la Autentificación de Firebase
        mAuth = FirebaseAuth.getInstance();

        //Creamos un identificador unico
        uid = FirebaseDatabase.getInstance().getReference("HowTo").push().getKey();

        titulo_howto.addTextChangedListener(visualizador_titulo);
        descripcion_howto.addTextChangedListener(visualizador_descripcion);

        listaPasos = new HashMap<>();

        adaptador = new AdaptadorExpandableListView(this,listaPasos);
        pasos.setAdapter(adaptador);

        //Realizamos una consulta a la bbdd
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Borradores");
        Query query = myRef.child(uid).child("pasos");
        query.addChildEventListener(new ChildEventListener() {

            List<String> childItem;

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                //Actualizamos el listView si hay un nuevo paso
                childItem = new ArrayList<>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String campos = (String) ds.getValue();
                    childItem.add(campos);
                }
                Paso paso;
                //Comprobamos si hay imagen o no
                if(childItem.size()==3){
                    paso = new Paso(childItem.get(2),childItem.get(0),childItem.get(1));
                } else {
                    paso = new Paso(childItem.get(1),childItem.get(0),null);
                }


                listaPasos.put(Integer.parseInt(dataSnapshot.getKey()),paso);

                //Aumentamos en 1 para el siguiente paso
                paso_actual++;

                //Actualizamos el adaptador
                adaptador = new AdaptadorExpandableListView(CrearHowTo.this,listaPasos);
                pasos.setAdapter(adaptador);

                setExpandableListViewHeight(pasos, -1);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        pasos.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int position, long id) {

                //Calculamos la altura del ListView
                setExpandableListViewHeight(parent, position);
                return false;
            }
        });

        //Establecemos un toolbar personalizado
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Le metemos un boton de cerrar
        Drawable cerrar = ContextCompat.getDrawable(this,R.drawable.ic_close_white_24dp);
        getSupportActionBar().setHomeAsUpIndicator(cerrar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(this);
    }
    /**
     * Detecta cada vez que se escribe un caracter
     */
    private final TextWatcher visualizador_titulo = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //Actualizamos el contador
            contador_titulo.setText(String.valueOf(s.length())+"/45");

            //Ponemos colores para avisar mejor al usuario
            if(s.length()>45){
                String registro = "<font color=\"red\">"+String.valueOf(s.length())+"</font>/45";
                contador_titulo.setText(Html.fromHtml(registro));
            } else if(s.length()==45){
                String registro = "<font color=\"green\">"+String.valueOf(s.length())+"</font>/45";
                contador_titulo.setText(Html.fromHtml(registro));
            } else if(s.length()>=36){
                String registro = "<font color=\"yellow\">"+String.valueOf(s.length())+"</font>/45";
                contador_titulo.setText(Html.fromHtml(registro));
            }
        }

        public void afterTextChanged(Editable s) {
            texto_titulo = s.toString();

        }
    };



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
     * Detecta cada vez que se escribe un caracter
     */
    private final TextWatcher visualizador_descripcion = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //Actualizamos el contador
            contador_descripcion.setText(String.valueOf(s.length())+"/200");

            //Ponemos colores para avisar mejor al usuario
            if(s.length()>200){
                String registro = "<font color=\"red\">"+String.valueOf(s.length())+"</font>/200";
                contador_descripcion.setText(Html.fromHtml(registro));
            } else if(s.length()==200){
                String registro = "<font color=\"green\">"+String.valueOf(s.length())+"</font>/200";
                contador_descripcion.setText(Html.fromHtml(registro));
            } else if(s.length()>=160){
                String registro = "<font color=\"yellow\">"+String.valueOf(s.length())+"</font>/200";
                contador_descripcion.setText(Html.fromHtml(registro));
            }
        }

        public void afterTextChanged(Editable s) {
            texto_descripcion = s.toString();
        }
    };

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.añadir_paso:
                //Abrimos la activity para crear un paso
                startActivity(new Intent(this,CrearPaso.class).putExtra("uid",uid).putExtra("paso",paso_actual));
                break;
            case R.id.crear:
                if(!comprobar_vacios()){
                    //Llamamos al metodo encargado de subir la imagen
                    subir_foto();
                }
                break;
            case R.id.foto_howto:
                //Abrimos un menu para las imagenes
                showOptions();
                break;
            default:
                //Si estan vacios los campos, volvemos hacia atras
                if(TextUtils.isEmpty(texto_titulo) && TextUtils.isEmpty(texto_descripcion) && listaPasos.size()==0){
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
                            FirebaseDatabase.getInstance().getReference("Borradores").child(uid).removeValue();
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
     * Algoritmo que establece la altura que debe tener el listview
     * @param listView
     * @param group
     */
    private void setExpandableListViewHeight(ExpandableListView listView,
                                             int group) {
        ExpandableListAdapter listAdapter = (ExpandableListAdapter) listView.getExpandableListAdapter();
        //Establecemos un minimo que va a dejar de espacio
        int totalHeight = 0;
        //Obtenemos el ancho del listView
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);

        //Calculamos el tamaño de cada group del ListView
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            totalHeight += groupItem.getMeasuredHeight();

            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                    totalHeight += listItem.getMeasuredHeight();

                }
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        //Agregamos a la altura el tamaño de los divider
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));

        //Establecemos una altura minima
        if (height < 10)
            height = 200;
        params.height = height;

        //Le damos la altura necesaria
        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    /**
     * Mostramos un dialogo emergente con las opciones para subir una foto
     */
    private void showOptions() {
        final CharSequence[] option = {"Tomar foto", "Elegir de galeria", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(CrearHowTo.this);
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
                Toast.makeText(CrearHowTo.this, "Permisos aceptados", Toast.LENGTH_SHORT).show();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(CrearHowTo.this);
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
                progressDialog.setMessage("Subiendo el HowTo...");
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
                                subir_howto(url);
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
                subir_howto("");
            }
        } else {
            /* No estas conectado a internet */
            Snackbar.make(this.vista, "No tienes conexion a Internet", 5000).show();
        }
    }

    /**
     * Se encarga de subir el howto con los datos a la bbddd
     * @param url
     */
    private void subir_howto(String url){

        HowTo howto;
        //Si no hay url, se crea el howto sin imagen
        if(TextUtils.isEmpty(url)){
            howto = new HowTo(uid,mAuth.getCurrentUser().getUid(),titulo_howto.getText().toString().trim(),descripcion_howto.getText().toString().trim(),null);
        } else {
            howto = new HowTo(uid,mAuth.getCurrentUser().getUid(),titulo_howto.getText().toString().trim(),descripcion_howto.getText().toString().trim(),url);
        }

        Map<String,Object> lista = new HashMap<>();
        //Actualizamos los pasos
        for (Integer i:listaPasos.keySet()) {
            lista.put(i.toString(),listaPasos.get(i));
        }

        //Se sube el how, indicando el usuario que lo creo y la hora a la que se ha subido
        FirebaseDatabase.getInstance().getReference("HowTo").child(uid).setValue(howto);
        FirebaseDatabase.getInstance().getReference("HowTo").child(uid).child("pasos").setValue(lista);
        FirebaseDatabase.getInstance().getReference("HowTo").child(uid).child("fecha_hora").setValue(Long.parseLong("9999999999")-Timestamp.now().getSeconds());
        FirebaseDatabase.getInstance().getReference("Borradores").child(uid).removeValue();

        String uid_howtoarchive = FirebaseDatabase.getInstance().getReference("HowToAndArchivados").child(mAuth.getCurrentUser().getUid()).push().getKey();

        HowToAndArchivado howToAndArchivado = new HowToAndArchivado(uid, false, Long.parseLong("9999999999") - Timestamp.now().getSeconds());
        FirebaseDatabase.getInstance().getReference("HowToAndArchivados").child(mAuth.getCurrentUser().getUid()).child(uid_howtoarchive).setValue(howToAndArchivado);

        Estadistica stats = new Estadistica(0,0);
        FirebaseDatabase.getInstance().getReference("Estadisticas").child(uid).setValue(stats);

        //Se vuelve a la activity anterior
        onBackPressed();
        finish();
    }

    /**
     * Comprueba si los campos de titulo y descripcion estan vacios
     * @return
     */
    private boolean comprobar_vacios(){
        boolean vacio = false;

        //Comprobamos que los campos no esten vacios
        if(TextUtils.isEmpty(texto_titulo.trim())) {
            vacio = true;
            titulo_howto.setError("Debes rellenar este campo!");
        } else if(texto_titulo.length()>45){
            vacio = true;
            titulo_howto.setError("No puede tener mas de 45 carácteres!");
        } else if(TextUtils.isEmpty(texto_descripcion.trim())){
            vacio = true;
            descripcion_howto.setError("Debes rellenar este campo!");
        } else if(descripcion_howto.length()>200){
            vacio = true;
            descripcion_howto.setError("No puede tener mas de 200 carácteres!");
        } else {
            titulo_howto.setError(null);
            descripcion_howto.setError(null);
        }

        return vacio;
    }
}
