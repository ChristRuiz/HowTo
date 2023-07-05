package com.example.tfg;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentoTendencia extends Fragment implements View.OnClickListener {

    FirebaseAuth mAuth;
    TextView no_howto_comentarios,no_howto_likes;
    RecyclerView howtos_comentarios,howtos_likes;
    AdaptadorRecyclerHowTo adaptador_howtos_comentarios,adaptador_howtos_likes;
    List<HowTo> howToComentariosList,howToLikesList;
    HowTo actual;
    EditText buscador;
    ImageButton buscar;

    public FragmentoTendencia() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragmento_tendencia, container, false);

        // Inicializacimos la Autentificación de Firebase
        mAuth = FirebaseAuth.getInstance();

        no_howto_comentarios = vista.findViewById(R.id.no_howto_comentarios);
        no_howto_likes = vista.findViewById(R.id.no_howto_likes);
        howtos_comentarios = vista.findViewById(R.id.lista_howto_comentarios);
        howtos_likes = vista.findViewById(R.id.lista_howto_likes);
        buscador = vista.findViewById(R.id.buscador);
        buscar = vista.findViewById(R.id.buscar);
        buscar.setOnClickListener(this);


        no_howto_comentarios.setVisibility(View.VISIBLE);

        //Le indicamos que tipo de layout tiene para que se agilice la carga de datos
        howtos_comentarios.setLayoutManager(new LinearLayoutManager(getContext()));
        howtos_comentarios.setNestedScrollingEnabled(true);

        howToComentariosList = new ArrayList<HowTo>();

        adaptador_howtos_comentarios = new AdaptadorRecyclerHowTo(howToComentariosList,getContext());
        adaptador_howtos_comentarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HowTo howto = howToComentariosList.get(howtos_comentarios.getChildAdapterPosition(v));
                startActivity(new Intent(getContext(),MostrarHowto.class)
                        .putExtra("uid",howto.getUid()));
            }
        });
        howtos_comentarios.setAdapter(adaptador_howtos_comentarios);

        //Buscamos en la bbdd
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Estadisticas");

        //Cargamos solo 5 howtos
        Query query = myRef.orderByChild("comments").limitToLast(5);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()) {

                    //Cargamos los datos de cada HowTo
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("HowTo");
                    Query query = myRef.child(data.getKey());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {

                            //Comprobamos que tenga imagen de portada el HowTo
                            if(dataSnapshot2.child("url").getValue() != null){
                                actual = new HowTo(dataSnapshot2.getKey(),
                                        dataSnapshot2.child("creador").getValue().toString(),
                                        dataSnapshot2.child("titulo").getValue().toString(),
                                        dataSnapshot2.child("descripcion").getValue().toString(),
                                        dataSnapshot2.child("url").getValue().toString(),
                                        false,
                                        Long.parseLong(dataSnapshot2.child("fecha_hora").getValue().toString()));
                            } else {
                                actual = new HowTo(dataSnapshot2.getKey(),
                                        dataSnapshot2.child("creador").getValue().toString(),
                                        dataSnapshot2.child("titulo").getValue().toString(),
                                        dataSnapshot2.child("descripcion").getValue().toString(),
                                        null,
                                        false,
                                        Long.parseLong(dataSnapshot2.child("fecha_hora").getValue().toString()));
                            }

                            howToComentariosList.add(0,actual);

                            adaptador_howtos_comentarios.setItems(howToComentariosList);
                            adaptador_howtos_comentarios.notifyDataSetChanged();

                            //Si no hay usuarios, mostramos el texto de que no hay usuarios
                            if(howToComentariosList.size()>0){
                                no_howto_comentarios.setVisibility(View.GONE);
                            } else {
                                no_howto_comentarios.setVisibility(View.VISIBLE);
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


        no_howto_likes.setVisibility(View.VISIBLE);

        //Le indicamos que tipo de layout tiene para que se agilice la carga de datos
        howtos_likes.setLayoutManager(new LinearLayoutManager(getContext()));
        howtos_likes.setNestedScrollingEnabled(true);

        howToLikesList = new ArrayList<HowTo>();

        adaptador_howtos_likes = new AdaptadorRecyclerHowTo(howToLikesList,getContext());
        adaptador_howtos_likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HowTo howto = howToLikesList.get(howtos_likes.getChildAdapterPosition(v));
                startActivity(new Intent(getContext(),MostrarHowto.class)
                        .putExtra("uid",howto.getUid()));
            }
        });
        howtos_likes.setAdapter(adaptador_howtos_likes);

        //Buscamos en la bbdd
        DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("Estadisticas");
        Query query2 = myRef2.orderByChild("likes").limitToLast(5);
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("HowTo");
                    Query query = myRef.child(data.getKey());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                            //Comprobamos que tenga imagen de portada el HowTo
                            if(dataSnapshot2.child("url").getValue() != null){
                                actual = new HowTo(dataSnapshot2.getKey(),
                                        dataSnapshot2.child("creador").getValue().toString(),
                                        dataSnapshot2.child("titulo").getValue().toString(),
                                        dataSnapshot2.child("descripcion").getValue().toString(),
                                        dataSnapshot2.child("url").getValue().toString(),
                                        false,
                                        Long.parseLong(dataSnapshot2.child("fecha_hora").getValue().toString()));
                            } else {
                                actual = new HowTo(dataSnapshot2.getKey(),
                                        dataSnapshot2.child("creador").getValue().toString(),
                                        dataSnapshot2.child("titulo").getValue().toString(),
                                        dataSnapshot2.child("descripcion").getValue().toString(),
                                        null,
                                        false,
                                        Long.parseLong(dataSnapshot2.child("fecha_hora").getValue().toString()));
                            }

                            howToLikesList.add(0,actual);

                            adaptador_howtos_likes.setItems(howToLikesList);
                            adaptador_howtos_likes.notifyDataSetChanged();

                            //Si no hay usuarios, mostramos el texto de que no hay usuarios
                            if(howToLikesList.size()>0){
                                no_howto_likes.setVisibility(View.GONE);
                            } else {
                                no_howto_likes.setVisibility(View.VISIBLE);
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

        return vista;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buscar:
                if(!TextUtils.isEmpty(buscador.getText().toString().trim())){
                    startActivity(new Intent(getContext(),ResultadosBuscador.class)
                            .putExtra("buscar",buscador.getText().toString().trim()));
                }
                break;
        }
    }
}
