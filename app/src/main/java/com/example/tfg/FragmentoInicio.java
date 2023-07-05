package com.example.tfg;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentoInicio extends Fragment {

    FirebaseAuth mAuth;
    TextView no_howto;
    RecyclerView howtos;
    AdaptadorRecyclerHowTo adaptador_howtos;
    List<HowTo> howToList;
    List<String> seguidoresList;
    HowTo actual;
    String uid;

    public FragmentoInicio() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_inicio, container, false);
        // Inicializacimos la Autentificación de Firebase
        mAuth = FirebaseAuth.getInstance();

        //Obtenemos el uid del usuario actual
        uid = mAuth.getCurrentUser().getUid();

        no_howto = vista.findViewById(R.id.no_howto);
        howtos = vista.findViewById(R.id.lista_howto);

        no_howto.setVisibility(View.VISIBLE);

        //Le indicamos que tipo de layout tiene para que se agilice la carga de datos
        howtos.setLayoutManager(new LinearLayoutManager(getContext()));
        howtos.setNestedScrollingEnabled(true);

        howToList = new ArrayList<HowTo>();
        seguidoresList = new ArrayList<String>();
        seguidoresList.add(uid);

        adaptador_howtos = new AdaptadorRecyclerHowTo(howToList, getContext());
        adaptador_howtos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HowTo howto = howToList.get(howtos.getChildAdapterPosition(v));
                startActivity(new Intent(getContext(), MostrarHowto.class)
                        .putExtra("uid", howto.getUid()));
            }
        });
        howtos.setAdapter(adaptador_howtos);

        //Buscamos en la bbdd
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Follow");
        Query query = myRef.orderByChild("uid_follower").equalTo(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    //Obtenemos todos los usuarios que esta siguiendo
                    seguidoresList.add(data.child("uid_following").getValue().toString());
                }

                for (String uid_actual : seguidoresList) {
                    //Buscamos en la bbdd los HowTos de los usuarios que sigue
                    DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("HowTo");
                    Query query2 = myRef2.orderByChild("creador").equalTo(uid_actual);
                    query2.addChildEventListener(new ChildEventListener() {

                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot2, @Nullable String s) {
                            if (dataSnapshot2.child("url").getValue() != null) {
                                Log.d("Hola", dataSnapshot2.getKey());
                                actual = new HowTo(dataSnapshot2.getKey(),
                                        dataSnapshot2.child("creador").getValue().toString(),
                                        dataSnapshot2.child("titulo").getValue().toString(),
                                        dataSnapshot2.child("descripcion").getValue().toString(),
                                        dataSnapshot2.child("url").getValue().toString(),
                                        Long.parseLong(dataSnapshot2.child("fecha_hora").getValue().toString()));
                            } else {
                                Log.d("Hola", dataSnapshot2.getKey());
                                actual = new HowTo(dataSnapshot2.getKey(),
                                        dataSnapshot2.child("creador").getValue().toString(),
                                        dataSnapshot2.child("titulo").getValue().toString(),
                                        dataSnapshot2.child("descripcion").getValue().toString(),
                                        null,
                                        Long.parseLong(dataSnapshot2.child("fecha_hora").getValue().toString()));
                            }

                            howToList.add(actual);

                            //Los ordenamos por fecha de creacion
                            Collections.sort(howToList);

                            adaptador_howtos.setItems(howToList);
                            adaptador_howtos.notifyDataSetChanged();

                            //Si no hay howtos, mostramos el texto de que no hay howtos
                            if (howToList.size() > 0) {
                                no_howto.setVisibility(View.GONE);
                            } else {
                                no_howto.setVisibility(View.VISIBLE);
                            }
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return vista;
    }
}
