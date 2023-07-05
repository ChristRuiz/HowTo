package com.example.tfg;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.List;

public class MostrarComentario extends AppCompatActivity implements View.OnClickListener {

    private ImageView foto_perfil_comentario,foto_perfil_escribir;
    private TextView nombre_completo, nombre_usuario, comentario_respuesta, no_respuestas, numero_respuestas;
    private EditText escribir_respuesta;
    private Button enviar;
    String uid_creador,comentario,uid;
    RecyclerView respuestas;
    AdaptadorRecyclerComentarios adaptador_respuestas;
    List<Comentario> respuestasList;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mostrar_comentario);

        //Obtenemos los datos de la activity anterior
        uid = getIntent().getExtras().getString("uid");
        uid_creador = getIntent().getExtras().getString("uid_creador");
        comentario = getIntent().getExtras().getString("comentario");

        foto_perfil_comentario = findViewById(R.id.foto_perfil);
        nombre_completo = findViewById(R.id.nombre_completo);
        nombre_usuario = findViewById(R.id.nombre_usuario);
        comentario_respuesta = findViewById(R.id.comentario);
        foto_perfil_escribir = findViewById(R.id.foto_perfil_escribir);
        escribir_respuesta = findViewById(R.id.escribir_respuesta);
        respuestas = findViewById(R.id.lista_respuestas);
        no_respuestas = findViewById(R.id.no_respuestas);
        numero_respuestas = findViewById(R.id.numero_respuestas);
        enviar = findViewById(R.id.enviar);

        enviar.setOnClickListener(this);
        comentario_respuesta.setText(comentario);

        no_respuestas.setVisibility(View.VISIBLE);

        //Le indicamos que tipo de layout tiene para que se agilice la carga de datos
        respuestas.setLayoutManager(new LinearLayoutManager(this));
        respuestas.setNestedScrollingEnabled(false);

        respuestasList = new ArrayList<Comentario>();

        adaptador_respuestas = new AdaptadorRecyclerComentarios(respuestasList,this);
        adaptador_respuestas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comentario respuesta = respuestasList.get(respuestas.getChildAdapterPosition(v));
                startActivity(new Intent(getApplicationContext(),MostrarComentario.class)
                        .putExtra("uid",respuesta.getUid())
                        .putExtra("comentario",respuesta.getComentario())
                        .putExtra("uid_creador",respuesta.getUid_creador()));
            }
        });
        respuestas.setAdapter(adaptador_respuestas);


        // Inicializacimos la Autentificación de Firebase
        mAuth = FirebaseAuth.getInstance();

        Picasso.get().load(mAuth.getCurrentUser().getPhotoUrl()).placeholder(R.drawable.user).fit()
                .centerCrop().into(foto_perfil_escribir);



        //Creamos una toolbar personalizada
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Respuestas");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(this);

        //Buscamos en la bbdd
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Usuarios");
        Query query = myRef.child(uid_creador);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Picasso.get().load(dataSnapshot.child("url").getValue().toString()).placeholder(R.drawable.user).fit()
                        .centerCrop().into(foto_perfil_comentario);
                nombre_completo.setText(dataSnapshot.child("nombre").getValue().toString());
                nombre_usuario.setText("@"+dataSnapshot.child("username").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                respuestasList.add(0,comentario);

                adaptador_respuestas.setItems(respuestasList);
                adaptador_respuestas.notifyDataSetChanged();

                //Si no hay comentarios, mostramos el texto de que no hay comentarios
                if(respuestasList.size()>0){
                    no_respuestas.setVisibility(View.GONE);
                } else {
                    no_respuestas.setVisibility(View.VISIBLE);
                }

                //Actualizamos el numero de comentarios
                numero_respuestas.setText(""+respuestasList.size());

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

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.enviar:
                //Comprobamos que no este vacio el comentario
                if(!TextUtils.isEmpty(escribir_respuesta.getText().toString().trim())){
                    guardar_respuesta();
                }
                break;
            default:
                onBackPressed();
                break;
        }
    }


    /**
     * Guarda el comentario en la bbdd y limpia su contenido
     */
    private void guardar_respuesta(){
        String respuesta = escribir_respuesta.getText().toString().trim();

        //Guardamos el comentario en la BBDD
        String uid_respuesta = FirebaseDatabase.getInstance().getReference("Comentarios_Respuestas").push().getKey();
        Comentario comment = new Comentario(uid_respuesta,mAuth.getCurrentUser().getUid(),respuesta,uid,Long.parseLong("9999999999")- Timestamp.now().getSeconds());
        FirebaseDatabase.getInstance().getReference("Comentarios_Respuestas").child(uid_respuesta).setValue(comment);

        //Comprobamos que el uid del creador no es del usuario actual
        if(!uid_creador.equals(mAuth.getCurrentUser().getUid())){
            String uid_notificacion = FirebaseDatabase.getInstance().getReference("Notificaciones").push().getKey();
            Notificacion notificacion = new Notificacion(2,uid_notificacion,uid_creador,mAuth.getCurrentUser().getUid());
            notificacion.setUid_comentario(uid_respuesta);
            FirebaseDatabase.getInstance().getReference("Notificaciones").child(uid_notificacion).setValue(notificacion);
        }

        //Limpiamos el campo de texto
        escribir_respuesta.setText("");

    }

}
