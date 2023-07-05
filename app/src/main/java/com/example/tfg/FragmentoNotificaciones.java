package com.example.tfg;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class FragmentoNotificaciones extends Fragment {

    FirebaseAuth mAuth;
    TextView no_notificacion;
    RecyclerView notificaciones;
    AdaptadorRecyclerNotificaciones adaptador_notificaciones;
    List<Notificacion> notificacionList;
    String uid;

    public FragmentoNotificaciones() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista =  inflater.inflate(R.layout.fragmento_notificaciones, container, false);
        // Inicializacimos la Autentificación de Firebase
        mAuth = FirebaseAuth.getInstance();

        uid = mAuth.getCurrentUser().getUid();

        no_notificacion = vista.findViewById(R.id.no_notificaciones);
        notificaciones = vista.findViewById(R.id.lista_notificaciones);

        no_notificacion.setVisibility(View.VISIBLE);

        //Le indicamos que tipo de layout tiene para que se agilice la carga de datos
        notificaciones.setLayoutManager(new LinearLayoutManager(getContext()));
        notificaciones.setNestedScrollingEnabled(true);

        notificacionList = new ArrayList<Notificacion>();

        adaptador_notificaciones = new AdaptadorRecyclerNotificaciones(notificacionList,getContext());
        adaptador_notificaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Notificacion notificacion = notificacionList.get(notificaciones.getChildAdapterPosition(v));

                //Si es un comentario, si pulsa, cargamos el comentario
                if(notificacion.getTipo_notificacion()!=2){
                    startActivity(new Intent(getContext(),MostrarHowto.class)
                            .putExtra("uid",notificacion.getUid_howto()));
                }
                //Si se ha archivado o si le han dado like, si pulsa, cargamos el howto
                else {
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Comentarios_Respuestas");
                    Query query = myRef.orderByChild("uid").equalTo(notificacion.getUid_comentario());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            dataSnapshot = dataSnapshot.child(notificacion.getUid_comentario());
                            startActivity(new Intent(getContext(),MostrarComentario.class)
                                    .putExtra("uid",notificacion.getUid_comentario())
                                    .putExtra("comentario",dataSnapshot.child("comentario").getValue().toString())
                                    .putExtra("uid_creador",dataSnapshot.child("uid_creador").getValue().toString()));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

            }
        });
        notificaciones.setAdapter(adaptador_notificaciones);

        //Buscamos en la bbdd
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Notificaciones");
        Query query = myRef.orderByChild("uid_usuario_notificado").equalTo(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren()) {
                    Notificacion notificacion = new Notificacion(Integer.parseInt(data.child("tipo_notificacion").getValue().toString()),data.getKey(),data.child("uid_usuario_notificado").getValue().toString(),data.child("uid_usuario_notifica").getValue().toString());
                    //Si es un comentario
                    if(notificacion.getTipo_notificacion()!=2){
                        notificacion.setUid_howto(data.child("uid_howto").getValue().toString());
                    }
                    //Si se ha archivado o si se le ha dado like
                    else {
                        notificacion.setUid_comentario(data.child("uid_comentario").getValue().toString());
                    }
                    notificacionList.add(0,notificacion);
                }

                adaptador_notificaciones.setItems(notificacionList);
                adaptador_notificaciones.notifyDataSetChanged();

                //Si no hay notificaciones, mostramos el texto de que no hay notificaciones
                if(notificacionList.size()>0){
                    no_notificacion.setVisibility(View.GONE);
                } else {
                    no_notificacion.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return vista;
    }

}
