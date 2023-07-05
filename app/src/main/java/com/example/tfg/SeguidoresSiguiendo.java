package com.example.tfg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class SeguidoresSiguiendo extends AppCompatActivity implements View.OnClickListener {

    TextView no_usuarios;
    FirebaseAuth mAuth;
    RecyclerView usuarios;
    AdaptadorRecyclerUsuarios adaptador_usuarios;
    List<Usuario> usuarioList;
    private String uid, seguidores_siguiendo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seguidores_siguiendo);

        uid = getIntent().getExtras().getString("uid");
        seguidores_siguiendo = getIntent().getExtras().getString("seguidores_siguiendo");

        //Creamos una toolbar personalizada
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(seguidores_siguiendo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(this);

        no_usuarios = findViewById(R.id.no_usuarios);
        usuarios = findViewById(R.id.lista_usuarios);

        // Inicializacimos la Autentificación de Firebase
        mAuth = FirebaseAuth.getInstance();

        no_usuarios.setVisibility(View.VISIBLE);

        //Le indicamos que tipo de layout tiene para que se agilice la carga de datos
        usuarios.setLayoutManager(new LinearLayoutManager(this));
        usuarios.setNestedScrollingEnabled(false);

        usuarioList = new ArrayList<Usuario>();

        adaptador_usuarios = new AdaptadorRecyclerUsuarios(usuarioList,this);
        adaptador_usuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Usuario usuario = usuarioList.get(usuarios.getChildAdapterPosition(v));
                startActivity(new Intent(getApplicationContext(),MostrarPerfil.class)
                        .putExtra("uid",usuario.getUid()));
            }
        });
        usuarios.setAdapter(adaptador_usuarios);

        //Buscamos en la bbdd
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Follow");
        Query query;

        //Comprobamos que lista es, la de seguidores o siguiendo
        if(seguidores_siguiendo.equals("Siguiendo")){
            no_usuarios.setText("No está siguiendo a nadie aun!");
            query = myRef.orderByChild("uid_follower").equalTo(uid);
        } else {
            no_usuarios.setText("No tiene seguidores aun!");
            query = myRef.orderByChild("uid_following").equalTo(uid);
        }
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Usuario usuario;

                //Comprobamos que lista es, la de seguidores o siguiendo
                if(seguidores_siguiendo.equals("Siguiendo")) {
                    usuario = new Usuario(dataSnapshot.child("uid_following").getValue().toString());
                } else {
                    usuario = new Usuario(dataSnapshot.child("uid_follower").getValue().toString());
                }
                usuarioList.add(0,usuario);

                adaptador_usuarios.setItems(usuarioList);
                adaptador_usuarios.notifyDataSetChanged();

                //Si no hay usuarios, mostramos el texto de que no hay usuarios
                if(usuarioList.size()>0){
                    no_usuarios.setVisibility(View.GONE);
                } else {
                    no_usuarios.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            /**
             * Si se quita un follow se borra de la lista
             * @param dataSnapshot
             */
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                usuarioList.remove(adaptador_usuarios.position_borrada);

                adaptador_usuarios.setItems(usuarioList);
                adaptador_usuarios.notifyDataSetChanged();

                //Si no hay usuarios, mostramos el texto de que no hay usuarios
                if(usuarioList.size()>0){
                    no_usuarios.setVisibility(View.GONE);
                } else {
                    no_usuarios.setVisibility(View.VISIBLE);
                }
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
            default:
                onBackPressed();
                break;
        }
    }
}
