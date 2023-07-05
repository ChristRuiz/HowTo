package com.example.tfg;


import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentoGuiasArchivadas extends Fragment {

    private TextView no_archivados;
    List<MeGusta_Archivar> archivarList;
    FirebaseAuth mAuth;
    RecyclerView archivados;
    AdaptadorRecyclerArchivados adaptador_archivados;


    public FragmentoGuiasArchivadas() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Cagamos el layout de este fragment
        View vista = inflater.inflate(R.layout.fragmento_guias_archivadas, container, false);
        archivados = vista.findViewById(R.id.lista_archivados);
        no_archivados = vista.findViewById(R.id.no_archivados);

        // Inicializacimos la Autentificación de Firebase
        mAuth = FirebaseAuth.getInstance();

        no_archivados.setVisibility(View.VISIBLE);

        //Le indicamos que tipo de layout tiene para que se agilice la carga de datos
        archivados.setLayoutManager(new LinearLayoutManager(getContext()));
        archivados.setNestedScrollingEnabled(false);

        archivarList = new ArrayList<MeGusta_Archivar>();

        adaptador_archivados = new AdaptadorRecyclerArchivados(archivarList,getContext());
        adaptador_archivados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeGusta_Archivar archivado = archivarList.get(archivados.getChildAdapterPosition(v));
                startActivity(new Intent(getContext(),MostrarHowto.class)
                        .putExtra("uid",archivado.getUid_howto()));
            }
        });
        archivados.setAdapter(adaptador_archivados);

        //Buscamos en la bbdd los HowTo que ha archivado el usuario
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Archivados");
        Query query = myRef.orderByChild("uid_usuario").equalTo(mAuth.getCurrentUser().getUid());
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MeGusta_Archivar archivado = new MeGusta_Archivar(dataSnapshot.child("uid_usuario").getValue().toString(),dataSnapshot.child("uid_howto").getValue().toString());
                archivarList.add(0,archivado);

                adaptador_archivados.setItems(archivarList);
                adaptador_archivados.notifyDataSetChanged();

                //Si no hay comentarios, mostramos el texto de que no hay comentarios
                if(archivarList.size()>0){
                    no_archivados.setVisibility(View.GONE);
                } else {
                    no_archivados.setVisibility(View.VISIBLE);
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

        return vista;
    }

}
