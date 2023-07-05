package com.example.tfg;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdaptadorRecyclerNotificaciones extends RecyclerView.Adapter<NotificacionHolder> implements View.OnClickListener {

    private int primera_posicion = 0;
    private View.OnClickListener listener;

    private List<Notificacion> notificacionList;
    private Context context;
    String nombre;

    // Inicializacimos la Autentificación de Firebase
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    /**
     * Constructor
     * @param notificacionList
     * @param context
     */
    public AdaptadorRecyclerNotificaciones(List<Notificacion> notificacionList, Context context) {
        this.notificacionList = notificacionList;
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
    public NotificacionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_notificacion,parent,false);
        NotificacionHolder notificacionHolder = new NotificacionHolder(v);
        v.setOnClickListener(this);
        return notificacionHolder;
    }

    /**
     * Metodo que se encagar de controlar la vista actual
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull final NotificacionHolder holder, final int position) {

        //Buscamos en la bbdd los datos del usuario
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Usuarios");
        Query query = myRef.child(notificacionList.get(position).getUid_usuario_notifica());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.get().load(dataSnapshot.child("url").getValue().toString()).placeholder(R.drawable.user).fit()
                        .centerCrop().into(holder.getFoto_perfil());
                holder.getFoto_perfil().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context,MostrarPerfil.class)
                                .putExtra("uid",notificacionList.get(position).getUid_usuario_notifica()));
                    }
                });
                nombre = dataSnapshot.child("nombre").getValue().toString();

                String texto;
                //Realizamos distintas acciones dependiendo de la notificacion que sea
                switch (notificacionList.get(position).getTipo_notificacion()){
                    //0 = Me gusta
                    case 0:
                        //Icono de Like
                        holder.getTipo_notificacion().setImageResource(R.drawable.ic_like);

                        //Ponemos negrita al texto
                        texto = "<b>"+nombre+"</b> ha indicado que le gusta tu HowTo";
                        holder.getNombre_usuario().setText(Html.fromHtml(texto));
                        holder.getTexto_notificacion().setTextColor(context.getColor(R.color.colorAccent));

                        DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("HowTo");
                        Query query2 = myRef2.child(notificacionList.get(position).getUid_howto());
                        query2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                holder.getTexto_notificacion().setText(dataSnapshot.child("titulo").getValue().toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        break;
                    //1 = Archivar
                    case 1:
                        //Icono de Archivado
                        holder.getTipo_notificacion().setImageResource(R.drawable.ic_archive_on);

                        //Ponemos negrita al texto
                        texto = "<b>"+nombre+"</b> ha archivado tu HowTo";
                        holder.getNombre_usuario().setText(Html.fromHtml(texto));

                        holder.getTexto_notificacion().setTextColor(context.getColor(R.color.colorAccent));

                        DatabaseReference myRef3 = FirebaseDatabase.getInstance().getReference("HowTo");
                        Query query3 = myRef3.child(notificacionList.get(position).getUid_howto());
                        query3.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                holder.getTexto_notificacion().setText(dataSnapshot.child("titulo").getValue().toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        break;
                    //2 = Comentario
                    case 2:
                        //Icono de comentario
                        holder.getTipo_notificacion().setImageResource(R.drawable.ic_chat_white_24dp);

                        //Ponemos negrita al texto
                        texto = "<b>"+nombre+"</b>";
                        holder.getNombre_usuario().setText(Html.fromHtml(texto));

                        DatabaseReference myRef4 = FirebaseDatabase.getInstance().getReference("Comentarios_Respuestas");
                        Query query4 = myRef4.child(notificacionList.get(position).getUid_comentario());
                        query4.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                holder.getTexto_notificacion().setText(dataSnapshot.child("comentario").getValue().toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        // Le ponemos la animation
        setAnimation(holder.itemView, position);
    }

    /**
     * Obtenemos el numero de items que hay
     * @return
     */
    @Override
    public int getItemCount() {
        return notificacionList.size();
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
        // Si es el primer item, le damos la animacion
        if (position==primera_posicion)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
        }
    }

    /**
     * Setter de notificacionList
     * @param notificaciones
     */
    public void setItems(List<Notificacion> notificaciones) {
        this.notificacionList = notificaciones;
    }


    @Override
    public void onClick(View v) {
        if(listener!=null){
            listener.onClick(v);
        }
    }
}
