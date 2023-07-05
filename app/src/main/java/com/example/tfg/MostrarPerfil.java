package com.example.tfg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MostrarPerfil extends AppCompatActivity implements View.OnClickListener {

    RelativeLayout datos_perfil;
    ImageView foto_perfil;
    TextView nombre_completo, nombre_usuario, siguiendo, seguidores, no_howto, te_sigue;
    RecyclerView howtos;
    AdaptadorRecyclerHowTo adaptador_howtos;
    List<HowTo> howToList;
    FirebaseAuth mAuth;
    HowTo actual;
    Button seguir;
    Boolean actual_archivado;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mostrar_perfil);

        uid = getIntent().getExtras().getString("uid");


        //Creamos una toolbar personalizada
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Usuario");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(this);


        datos_perfil = findViewById(R.id.datos_perfil);
        foto_perfil = findViewById(R.id.foto_perfil);
        nombre_completo = findViewById(R.id.nombre_completo);
        nombre_usuario = findViewById(R.id.nombre_usuario);
        siguiendo = findViewById(R.id.siguiendo);
        seguidores = findViewById(R.id.seguidores);
        seguir = findViewById(R.id.seguir);
        te_sigue = findViewById(R.id.te_sigue);
        te_sigue.setVisibility(View.GONE);
        no_howto = findViewById(R.id.no_howto);
        howtos = findViewById(R.id.lista_howto);

        // Inicializacimos la Autentificación de Firebase
        mAuth = FirebaseAuth.getInstance();

        no_howto.setVisibility(View.VISIBLE);

        //Le indicamos que tipo de layout tiene para que se agilice la carga de datos
        howtos.setLayoutManager(new LinearLayoutManager(this));
        howtos.setNestedScrollingEnabled(true);

        howToList = new ArrayList<HowTo>();

        adaptador_howtos = new AdaptadorRecyclerHowTo(howToList,this);
        adaptador_howtos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HowTo howto = howToList.get(howtos.getChildAdapterPosition(v));
                startActivity(new Intent(getApplicationContext(),MostrarHowto.class)
                        .putExtra("uid",howto.getUid()));
            }
        });
        howtos.setAdapter(adaptador_howtos);

        //Buscamos en la bbdd
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("HowToAndArchivados");
        Query query = myRef.child(uid).orderByChild("fecha_hora");
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot data) {

                for (final DataSnapshot dataSnapshot:data.getChildren()) {
                    //Obtenemos si esta archivado
                    actual_archivado = Boolean.parseBoolean(dataSnapshot.child("archivado").getValue().toString());

                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("HowTo");
                    Query query = myRef.child(dataSnapshot.child("howTo").getValue().toString());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                            //Comprobamos si tiene foto de portada el howto
                            if(dataSnapshot2.child("url").getValue() != null){
                                actual = new HowTo(dataSnapshot2.getKey(),
                                        dataSnapshot2.child("creador").getValue().toString(),
                                        dataSnapshot2.child("titulo").getValue().toString(),
                                        dataSnapshot2.child("descripcion").getValue().toString(),
                                        dataSnapshot2.child("url").getValue().toString(),
                                        Boolean.parseBoolean(dataSnapshot.child("archivado").getValue().toString()),
                                        Long.parseLong(dataSnapshot2.child("fecha_hora").getValue().toString()));
                            } else {
                                actual = new HowTo(dataSnapshot2.getKey(),
                                        dataSnapshot2.child("creador").getValue().toString(),
                                        dataSnapshot2.child("titulo").getValue().toString(),
                                        dataSnapshot2.child("descripcion").getValue().toString(),
                                        null,
                                        Boolean.parseBoolean(dataSnapshot.child("archivado").getValue().toString()),
                                        Long.parseLong(dataSnapshot2.child("fecha_hora").getValue().toString()));
                            }

                            howToList.add(actual);

                            adaptador_howtos.setItems(howToList);
                            adaptador_howtos.notifyDataSetChanged();

                            //Si no hay usuarios, mostramos el texto de que no hay usuarios
                            if(howToList.size()>0){
                                no_howto.setVisibility(View.GONE);
                            } else {
                                no_howto.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seguir.setOnClickListener(this);
        siguiendo.setOnClickListener(this);
        seguidores.setOnClickListener(this);

        //Si el usuario del perfil es el mismo que el usuario actual no cargamos el boton de seguir
        if(uid.equals(mAuth.getCurrentUser().getUid())){
            seguir.setVisibility(View.GONE);
        }

        //Buscamos en la bbdd
        DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("Usuarios");
        Query query2 = myRef2.child(uid);
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Obtenemos los datos del usuario
                Picasso.get().load(dataSnapshot.child("url").getValue().toString()).placeholder(R.drawable.user).fit()
                        .centerCrop().into(foto_perfil);
                nombre_completo.setText(dataSnapshot.child("nombre").getValue().toString());
                nombre_usuario.setText("@"+dataSnapshot.child("username").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Si el usuario del perfil no es el mismo que el usuario actual
        if(!uid.equals(mAuth.getCurrentUser().getUid())) {

            //Buscamos en la bbdd
            DatabaseReference myRef3 = FirebaseDatabase.getInstance().getReference("Follow");
            Query query3 = myRef3.orderByChild("uid_follower").equalTo(mAuth.getCurrentUser().getUid());
            query3.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    boolean siguiendo = false;
                    //Buscamos si tiene dado follow el usuario
                    for (DataSnapshot datos : dataSnapshot.getChildren()) {
                        if (datos.child("uid_following").getValue().toString().equals(uid)) {
                            seguir.setBackgroundResource(R.drawable.button);
                            seguir.setText("Siguiendo");
                            siguiendo = true;
                        }
                    }

                    //Si no tiene dado el follow, cambiamos el diseño del boton al diseño de seguir
                    if (!siguiendo) {
                        seguir.setBackgroundResource(R.drawable.button_social_media);
                        seguir.setText("Seguir");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //Buscamos en la bbdd
            DatabaseReference myRef4= FirebaseDatabase.getInstance().getReference("Follow");
            Query query4 = myRef4.orderByChild("uid_following").equalTo(mAuth.getCurrentUser().getUid());
            query4.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    boolean siguiendo = false;
                    //Buscamos si tiene dado follow el usuario
                    for (DataSnapshot datos : dataSnapshot.getChildren()) {
                        if (datos.child("uid_follower").getValue().toString().equals(uid)) {
                            te_sigue.setVisibility(View.VISIBLE);
                            siguiendo = true;
                        }
                    }

                    if (!siguiendo) {
                        te_sigue.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }



        //Buscamos en la bbdd
        DatabaseReference myRef4 = FirebaseDatabase.getInstance().getReference("Follow");
        Query query4 = myRef4.orderByChild("uid_follower").equalTo(uid);
        query4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Obtenemos el numero de usuarios que esta siguiendo
                siguiendo.setText("Siguiendo: "+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Buscamos en la bbdd
        DatabaseReference myRef5 = FirebaseDatabase.getInstance().getReference("Follow");
        Query query5 = myRef5.orderByChild("uid_following").equalTo(uid);
        query5.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Obtenemos el numero de seguidores que tiene
                seguidores.setText("Seguidores: "+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.seguir:

                //Si no sigue al usuario y le da al boton
                if(seguir.getText().equals("Seguir")){
                    seguir.setBackgroundResource(R.drawable.button);
                    seguir.setText("Siguiendo");

                    //Guardamos el follow en la base de datos
                    String uid_seguir = FirebaseDatabase.getInstance().getReference("Follow").push().getKey();
                    Seguidor follow = new Seguidor(uid,mAuth.getCurrentUser().getUid());
                    FirebaseDatabase.getInstance().getReference("Follow").child(uid_seguir).setValue(follow);
                }
                //Si sigue al usuario y le da al boton
                else {
                    seguir.setBackgroundResource(R.drawable.button_social_media);
                    seguir.setText("Seguir");

                    //Buscamos en la bbdd
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Follow");
                    Query query = myRef.orderByChild("uid_follower").equalTo(mAuth.getCurrentUser().getUid());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            //Hacemos unfollow del usuario
                            for (DataSnapshot datos: dataSnapshot.getChildren()) {
                                if(datos.child("uid_following").getValue().toString().equals(uid)){
                                    FirebaseDatabase.getInstance().getReference("Follow").child(datos.getKey()).removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                break;
            case R.id.seguidores:
                startActivity(new Intent(getApplicationContext(),SeguidoresSiguiendo.class)
                        .putExtra("uid",uid)
                        .putExtra("seguidores_siguiendo","Seguidores"));
                break;
            case R.id.siguiendo:
                startActivity(new Intent(getApplicationContext(),SeguidoresSiguiendo.class)
                        .putExtra("uid",uid)
                        .putExtra("seguidores_siguiendo","Siguiendo"));
                break;
            default:
                onBackPressed();
                break;
        }
    }
}
