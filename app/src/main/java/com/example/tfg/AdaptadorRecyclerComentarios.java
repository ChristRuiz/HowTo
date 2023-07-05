package com.example.tfg;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdaptadorRecyclerComentarios extends RecyclerView.Adapter<ComentarioHolder> implements View.OnClickListener {

    private int primera_posicion = 0;
    private View.OnClickListener listener;

    private List<Comentario> comentarioList;
    private Context context;

    /**
     * Constructor
     * @param comentarioList
     * @param context
     */
    public AdaptadorRecyclerComentarios(List<Comentario> comentarioList, Context context) {
        this.comentarioList = comentarioList;
        this.context = context;
    }

    /**
     * Metodo llamado al crear la vista
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ComentarioHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_comentario,parent,false);
        ComentarioHolder comentarioHolder = new ComentarioHolder(v);
        v.setOnClickListener(this);
        return comentarioHolder;
    }

    /**
     * Metodo que se encagar de controlar la vista actual
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull final ComentarioHolder holder, final int position) {

        //Cargamos los datos del creador del comentario
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Usuarios");
        Query query = myRef.child(comentarioList.get(position).getUid_creador());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url = dataSnapshot.child("url").getValue().toString();
                String nombre = dataSnapshot.child("nombre").getValue().toString();
                String username = dataSnapshot.child("username").getValue().toString();

                Picasso.get().load(url).placeholder(R.drawable.user).fit()
                        .centerCrop().into(holder.getFoto_usuario());
                holder.getFoto_usuario().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context,MostrarPerfil.class)
                                .putExtra("uid",comentarioList.get(position).getUid_creador()));
                    }
                });
                holder.getNombre_completo().setText(nombre);
                holder.getNombre_usuario().setText("@"+username);
                holder.getComentario().setText(comentarioList.get(position).getComentario());
                holder.getNumero_respuestas().setText(""+comentarioList.size());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Averiguamos el numero de respuestas que tiene dicho comentario
        DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("Comentarios_Respuestas");
        Query query2 = myRef2.orderByChild("uid_padre").equalTo(comentarioList.get(position).getUid());
        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                holder.getNumero_respuestas().setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Le ponemos la animation
        setAnimation(holder.itemView, position);
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    /**
     * Le damos una pequeña animacion al primer item
     * @param viewToAnimate
     * @param position
     */
    private void setAnimation(View viewToAnimate, int position)
    {
        // Si es el primer item, le damos le animamos
        if (position==primera_posicion)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
        }
    }

    /**
     * Setter de comentarioList
     * @param comentarios
     */
    public void setItems(List<Comentario> comentarios) {
        this.comentarioList = comentarios;
    }

    /**
     * Obtenemos el numero de items que hay
     * @return
     */
    @Override
    public int getItemCount() {
        return comentarioList.size();
    }


    @Override
    public void onClick(View v) {
        if(listener!=null){
            listener.onClick(v);
        }
    }
}
