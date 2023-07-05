package com.example.tfg;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MostrarHowto extends AppCompatActivity implements View.OnClickListener {

    private String uid,titulo,descripcion,uid_creador,nombre_completo,nombre_usuario,foto_howto,foto_usuario;
    private TextView txt_nombre_completo,txt_nombre_usuario,txt_titulo_howto,txt_descripcion_howto,no_comments,numero_comentarios,numero_likes;
    private ImageView iv_foto_usuario,iv_foto_howto,iv_foto_usuario_actual;
    private Button enviar;
    private EditText escribir_comentario;
    private SparkButton me_gusta,archivar;
    private ExpandableListView pasos;
    private Map<Integer, Paso> listaPasos;
    AdaptadorExpandableListView adaptador;
    RecyclerView comentarios;
    AdaptadorRecyclerComentarios adaptador_comentarios;
    List<Comentario> comentarioList;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.mostrar_howto);

        uid = getIntent().getExtras().getString("uid");

        txt_nombre_completo = findViewById(R.id.nombre_completo);
        txt_nombre_usuario = findViewById(R.id.nombre_usuario);
        txt_titulo_howto = findViewById(R.id.titulo_howto);
        txt_descripcion_howto = findViewById(R.id.descripcion_howto);
        iv_foto_usuario = findViewById(R.id.foto_perfil);
        iv_foto_usuario.setOnClickListener(this);
        iv_foto_howto = findViewById(R.id.foto_howto);
        iv_foto_usuario_actual = findViewById(R.id.foto_perfil_escribir);
        pasos = findViewById(R.id.pasos);
        enviar = findViewById(R.id.enviar);
        escribir_comentario = findViewById(R.id.escribir_comentario);
        comentarios = findViewById(R.id.lista_comentario);
        no_comments = findViewById(R.id.no_comments);
        numero_comentarios = findViewById(R.id.numero_comentarios);
        numero_likes = findViewById(R.id.numero_likes);
        me_gusta = findViewById(R.id.like);
        archivar = findViewById(R.id.archivar);

        no_comments.setVisibility(View.VISIBLE);

        //Le indicamos que tipo de layout tiene para que se agilice la carga de datos
        comentarios.setLayoutManager(new LinearLayoutManager(this));
        comentarios.setNestedScrollingEnabled(false);

        enviar.setOnClickListener(this);


        comentarioList = new ArrayList<Comentario>();

        adaptador_comentarios = new AdaptadorRecyclerComentarios(comentarioList,this);
        adaptador_comentarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comentario comentario = comentarioList.get(comentarios.getChildAdapterPosition(v));
                startActivity(new Intent(getApplicationContext(),MostrarComentario.class)
                        .putExtra("uid",comentario.getUid())
                        .putExtra("comentario",comentario.getComentario())
                        .putExtra("uid_creador",comentario.getUid_creador()));
            }
        });
        comentarios.setAdapter(adaptador_comentarios);

        // Inicializacimos la Autentificación de Firebase
        mAuth = FirebaseAuth.getInstance();

        Picasso.get().load(mAuth.getCurrentUser().getPhotoUrl()).placeholder(R.drawable.user).fit()
                .centerCrop().into(iv_foto_usuario_actual);

        //Buscamos en la bbdd
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("HowTo");
        Query query = myRef.child(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Obtenemos todos los datos necesarios del howto
                titulo = dataSnapshot.child("titulo").getValue().toString();
                descripcion = dataSnapshot.child("descripcion").getValue().toString();
                uid_creador = dataSnapshot.child("creador").getValue().toString();


                listaPasos = new HashMap<>();

                //Ponemos el adaptador
                adaptador = new AdaptadorExpandableListView(MostrarHowto.this,listaPasos);
                pasos.setAdapter(adaptador);

                Iterator<DataSnapshot> iterador = dataSnapshot.child("pasos").getChildren().iterator();
                while(iterador.hasNext()){
                    DataSnapshot actual = iterador.next();
                    String url;
                    //Comprobamos si el paso actual tiene imagen
                    if(actual.child("imagen").getValue()!=null){
                        url = actual.child("imagen").getValue().toString();
                    } else {
                        url = null;
                    }
                    Paso paso = new Paso(actual.child("titulo").getValue().toString(),actual.child("comentario").getValue().toString(),url);
                    listaPasos.put(Integer.parseInt(actual.getKey()),paso);
                }

                //Actualizamos el adaptador
                adaptador = new AdaptadorExpandableListView(MostrarHowto.this,listaPasos);
                pasos.setAdapter(adaptador);
                setExpandableListViewHeight(pasos, -1);

                //Comprobamos si el howto tiene imagen
                if(dataSnapshot.child("url").getValue() != null){
                    foto_howto = dataSnapshot.child("url").getValue().toString();
                    iv_foto_howto.setVisibility(View.VISIBLE);
                    Picasso.get().load(foto_howto).placeholder(R.drawable.placeholder_image).fit()
                            .centerCrop().into(iv_foto_howto);
                } else {
                    iv_foto_howto.setVisibility(View.GONE);
                }

                //Le damos los valores correspondientes
                txt_titulo_howto.setText(titulo);
                txt_descripcion_howto.setText(descripcion);

                //Creamos una toolbar personalizada
                Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setTitle(titulo);
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);

                //Buscamos en la bbdd
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Usuarios");
                Query query = myRef.child(uid_creador);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        //Obtenemos los datos del usuario
                        nombre_completo = dataSnapshot.child("nombre").getValue().toString();
                        nombre_usuario = dataSnapshot.child("username").getValue().toString();
                        foto_usuario = dataSnapshot.child("url").getValue().toString();
                        Picasso.get().load(foto_usuario).placeholder(R.drawable.user).fit()
                                .centerCrop().into(iv_foto_usuario);
                        txt_nombre_completo.setText(nombre_completo);
                        txt_nombre_usuario.setText("@"+nombre_usuario);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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



        //Buscamos en la bbdd
        DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("Comentarios_Respuestas");
        Query query2 = myRef2.orderByChild("uid_padre").equalTo(uid);
        query2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Comentario comentario = new Comentario(dataSnapshot.getKey(),dataSnapshot.child("uid_creador").getValue().toString(),
                        dataSnapshot.child("comentario").getValue().toString(),dataSnapshot.child("uid_padre").getValue().toString(),Long.parseLong(dataSnapshot.child("fecha_creacion").getValue().toString()));
                comentarioList.add(0,comentario);

                adaptador_comentarios.setItems(comentarioList);
                adaptador_comentarios.notifyDataSetChanged();

                //Si no hay comentarios, mostramos el texto de que no hay comentarios
                if(comentarioList.size()>0){
                    no_comments.setVisibility(View.GONE);
                } else {
                    no_comments.setVisibility(View.VISIBLE);
                }

                //Actualizamos el numero de comentarios
                numero_comentarios.setText(""+comentarioList.size());
                FirebaseDatabase.getInstance().getReference("Estadisticas").child(uid).child("comments").setValue(comentarioList.size());

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






        //Buscamos en la bbdd si el usuario ya le ha dado a me gusta
        DatabaseReference myRef4 = FirebaseDatabase.getInstance().getReference("Likes");
        Query query4 = myRef4.orderByChild("uid_howto").equalTo(uid);
        query4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                numero_likes.setText(""+dataSnapshot.getChildrenCount());
                FirebaseDatabase.getInstance().getReference("Estadisticas").child(uid).child("likes").setValue(dataSnapshot.getChildrenCount());

                Iterator<DataSnapshot> iterador = dataSnapshot.getChildren().iterator();
                while (iterador.hasNext()){
                    DataSnapshot datos = iterador.next();
                    if(datos.child("uid_usuario").getValue().toString().equals(mAuth.getCurrentUser().getUid())){
                        me_gusta.setChecked(true);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        me_gusta.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                //Le ha dado me gusta
                if(buttonState){
                    //Guardamos el like en la BBDD
                    String uid_comentario = FirebaseDatabase.getInstance().getReference("Likes").push().getKey();
                    MeGusta_Archivar like = new MeGusta_Archivar(mAuth.getCurrentUser().getUid(),uid);
                    FirebaseDatabase.getInstance().getReference("Likes").child(uid_comentario).setValue(like);

                    //Creamos una notificacion si no es el propio usuario
                    if(!uid_creador.equals(mAuth.getCurrentUser().getUid())){
                        String uid_notificacion = FirebaseDatabase.getInstance().getReference("Notificaciones").push().getKey();
                        Notificacion notificacion = new Notificacion(0,uid_notificacion,uid_creador,mAuth.getCurrentUser().getUid());
                        notificacion.setUid_howto(uid);
                        FirebaseDatabase.getInstance().getReference("Notificaciones").child(uid_notificacion).setValue(notificacion);
                    }
                }
                //Le quita el me gusta
                else {
                    //Buscamos en la bbdd
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Likes");
                    Query query = myRef.orderByChild("uid_howto").equalTo(uid);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            //Borramos el like del usuario
                            Iterator<DataSnapshot> iterador = dataSnapshot.getChildren().iterator();
                            while (iterador.hasNext()){
                                DataSnapshot datos = iterador.next();
                                if(datos.child("uid_usuario").getValue().toString().equals(mAuth.getCurrentUser().getUid())){
                                    FirebaseDatabase.getInstance().getReference("Likes").child(datos.getKey()).removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {

            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {

            }
        });

        //Buscamos en la bbdd si el usuario ya ha archivado el howto
        DatabaseReference myRef5 = FirebaseDatabase.getInstance().getReference("Archivados");
        Query query5 = myRef5.orderByChild("uid_howto").equalTo(uid);
        query5.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterator<DataSnapshot> iterador = dataSnapshot.getChildren().iterator();
                while (iterador.hasNext()){
                    DataSnapshot datos = iterador.next();
                    if(datos.child("uid_usuario").getValue().toString().equals(mAuth.getCurrentUser().getUid())){
                        archivar.setChecked(true);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        archivar.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                //Le ha dado a archivar
                if(buttonState) {
                    //Guardamos el howto archivado en la BBDD
                    String uid_comentario = FirebaseDatabase.getInstance().getReference("Archivados").push().getKey();
                    MeGusta_Archivar archivado = new MeGusta_Archivar(mAuth.getCurrentUser().getUid(), uid);
                    FirebaseDatabase.getInstance().getReference("Archivados").child(uid_comentario).setValue(archivado);

                    String uid_howtoarchive = FirebaseDatabase.getInstance().getReference("HowToAndArchivados").child(mAuth.getCurrentUser().getUid()).push().getKey();


                    HowToAndArchivado howToAndArchivado = new HowToAndArchivado(uid, true, Long.parseLong("9999999999") - Timestamp.now().getSeconds());
                    FirebaseDatabase.getInstance().getReference("HowToAndArchivados").child(mAuth.getCurrentUser().getUid()).child(uid_howtoarchive).setValue(howToAndArchivado);

                    if(!uid_creador.equals(mAuth.getCurrentUser().getUid())){
                        String uid_notificacion = FirebaseDatabase.getInstance().getReference("Notificaciones").push().getKey();
                        Notificacion notificacion = new Notificacion(1,uid_notificacion,uid_creador,mAuth.getCurrentUser().getUid());
                        notificacion.setUid_howto(uid);
                        FirebaseDatabase.getInstance().getReference("Notificaciones").child(uid_notificacion).setValue(notificacion);
                    }
                } else {
                    //Buscamos en la bbdd
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Archivados");
                    Query query = myRef.orderByChild("uid_howto").equalTo(uid);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            //Borramos el howto archivado del usuario
                            Iterator<DataSnapshot> iterador = dataSnapshot.getChildren().iterator();
                            while (iterador.hasNext()){
                                DataSnapshot datos = iterador.next();
                                if(datos.child("uid_usuario").getValue().toString().equals(mAuth.getCurrentUser().getUid())){
                                    FirebaseDatabase.getInstance().getReference("Archivados").child(datos.getKey()).removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    //Buscamos en la bbdd
                    DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("HowToAndArchivados");
                    Query query2 = myRef2.child(mAuth.getCurrentUser().getUid()).orderByChild("howTo").equalTo(uid);
                    query2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            //Borramos el howto archivado del usuario
                            Iterator<DataSnapshot> iterador = dataSnapshot.getChildren().iterator();
                            while (iterador.hasNext()){
                                DataSnapshot datos = iterador.next();
                                if(Boolean.parseBoolean(datos.child("archivado").getValue().toString())){
                                    FirebaseDatabase.getInstance().getReference("HowToAndArchivados").child(mAuth.getCurrentUser().getUid()).child(datos.getKey()).removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {

            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {

            }
        });


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


        params.height = height;

        //Le damos la altura necesaria
        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.enviar:
                //Comprobamos que no este vacio el comentario
                if(!TextUtils.isEmpty(escribir_comentario.getText().toString().trim())){
                    guardar_comentario();
                }
                break;
            case R.id.foto_perfil:
                startActivity(new Intent(getApplicationContext(),MostrarPerfil.class)
                        .putExtra("uid",uid_creador));
        }
    }

    /**
     * Guarda el comentario en la bbdd y limpia su contenido
     */
    private void guardar_comentario(){
        String comentario = escribir_comentario.getText().toString().trim();

        //Guardamos el comentario en la BBDD
        String uid_comentario = FirebaseDatabase.getInstance().getReference("Comentarios_Respuestas").push().getKey();
        Comentario comment = new Comentario(uid_comentario,mAuth.getCurrentUser().getUid(),comentario,uid,Long.parseLong("9999999999")- Timestamp.now().getSeconds());
        FirebaseDatabase.getInstance().getReference("Comentarios_Respuestas").child(uid_comentario).setValue(comment);

        //Creamos una notificacion
        if(!uid_creador.equals(mAuth.getCurrentUser().getUid())){
            String uid_notificacion = FirebaseDatabase.getInstance().getReference("Notificaciones").push().getKey();
            Notificacion notificacion = new Notificacion(2,uid_notificacion,uid_creador,mAuth.getCurrentUser().getUid());
            notificacion.setUid_comentario(uid_comentario);

            FirebaseDatabase.getInstance().getReference("Notificaciones").child(uid_notificacion).setValue(notificacion);
        }

        //Limpiamos el campo de texto
        escribir_comentario.setText("");

    }
}
