package com.example.tfg;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentoPerfilPropio extends Fragment implements View.OnClickListener  {

    RelativeLayout datos_perfil;
    ImageView foto_perfil;
    TextView nombre_completo, nombre_usuario, siguiendo, seguidores, no_howto;
    RecyclerView howtos;
    AdaptadorRecyclerHowTo adaptador_howtos;
    List<HowTo> howToList;
    FirebaseAuth mAuth;
    HowTo actual;
    Boolean actual_archivado;
    private String uid;

    public FragmentoPerfilPropio() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragmento_perfil_propio, container, false);


        datos_perfil = vista.findViewById(R.id.datos_perfil);
        foto_perfil = vista.findViewById(R.id.foto_perfil);
        nombre_completo = vista.findViewById(R.id.nombre_completo);
        nombre_usuario = vista.findViewById(R.id.nombre_usuario);
        siguiendo = vista.findViewById(R.id.siguiendo);
        seguidores = vista.findViewById(R.id.seguidores);
        no_howto = vista.findViewById(R.id.no_howto);
        howtos = vista.findViewById(R.id.lista_howto);

        // Inicializacimos la Autentificación de Firebase
        mAuth = FirebaseAuth.getInstance();

        uid = mAuth.getCurrentUser().getUid();

        no_howto.setVisibility(View.VISIBLE);

        //Le indicamos que tipo de layout tiene para que se agilice la carga de datos
        howtos.setLayoutManager(new LinearLayoutManager(getContext()));
        howtos.setNestedScrollingEnabled(true);

        howToList = new ArrayList<HowTo>();

        adaptador_howtos = new AdaptadorRecyclerHowTo(howToList,getContext());
        adaptador_howtos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HowTo howto = howToList.get(howtos.getChildAdapterPosition(v));
                startActivity(new Intent(getContext(),MostrarHowto.class)
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

                    //Obtenemos si el howto obtenido esta archivado
                    actual_archivado = Boolean.parseBoolean(dataSnapshot.child("archivado").getValue().toString());

                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("HowTo");
                    Query query = myRef.child(dataSnapshot.child("howTo").getValue().toString());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                            //Comprobamos si tiene imagen de portada el HowTo
                            if(dataSnapshot2.child("url").getValue() != null){
                                actual = new HowTo(dataSnapshot2.getKey(),
                                        dataSnapshot2.child("creador").getValue().toString(),
                                        dataSnapshot2.child("titulo").getValue().toString(),
                                        dataSnapshot2.child("descripcion").getValue().toString(),
                                        dataSnapshot2.child("url").getValue().toString(),
                                        Boolean.parseBoolean(dataSnapshot.child("archivado").getValue().toString())
                                ,Long.parseLong(dataSnapshot2.child("fecha_hora").getValue().toString()));
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
        siguiendo.setOnClickListener(this);
        seguidores.setOnClickListener(this);

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

        return vista;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.seguidores:
                startActivity(new Intent(getContext(),SeguidoresSiguiendo.class)
                        .putExtra("uid",uid)
                        .putExtra("seguidores_siguiendo","Seguidores"));
                break;
            case R.id.siguiendo:
                startActivity(new Intent(getContext(),SeguidoresSiguiendo.class)
                        .putExtra("uid",uid)
                        .putExtra("seguidores_siguiendo","Siguiendo"));
                break;
        }
    }
}
