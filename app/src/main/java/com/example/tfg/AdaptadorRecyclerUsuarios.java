package com.example.tfg;

import android.content.Context;
import android.util.Log;
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

public class AdaptadorRecyclerUsuarios extends RecyclerView.Adapter<UsuarioHolder> implements View.OnClickListener {

    private int primera_posicion = 0;
    private View.OnClickListener listener;

    public int position_borrada;

    private List<Usuario> usuarioList;
    private Context context;

    // Inicializacimos la Autentificación de Firebase
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    /**
     * Constructor
     * @param usuarioList
     * @param context
     */
    public AdaptadorRecyclerUsuarios(List<Usuario> usuarioList, Context context) {
        this.usuarioList = usuarioList;
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
    public UsuarioHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_usuarios,parent,false);
        UsuarioHolder usuarioHolder = new UsuarioHolder(v);
        v.setOnClickListener(this);
        return usuarioHolder;
    }

    /**
     * Metodo que se encagar de controlar la vista actual
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull final UsuarioHolder holder, final int position) {

        //Cargamos en la bbdd los datos del usuario
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Usuarios");
        Query query = myRef.child(usuarioList.get(position).getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url = dataSnapshot.child("url").getValue().toString();
                Log.d("URL",url);
                String nombre = dataSnapshot.child("nombre").getValue().toString();
                Log.d("Nombre",nombre);
                String username = dataSnapshot.child("username").getValue().toString();

                Picasso.get().load(url).placeholder(R.drawable.user).fit()
                        .centerCrop().into(holder.getFoto_usuario());
                holder.getNombre_completo().setText(nombre);
                holder.getNombre_usuario().setText("@"+username);

                //Si no es el usuario actual mostramos el boton de seguir
                if(!usuarioList.get(position).getUid().equals(mAuth.getCurrentUser().getUid())){
                    holder.getSeguir().setVisibility(View.VISIBLE);


                    //Buscamos en la bbdd
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Follow");
                    Query query = myRef.orderByChild("uid_follower").equalTo(mAuth.getCurrentUser().getUid());
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            //Buscamos si tiene dado follow el usuario
                            for (DataSnapshot datos: dataSnapshot.getChildren()) {
                                if(datos.child("uid_following").getValue().toString().equals(usuarioList.get(position).getUid())){
                                    holder.getSeguir().setBackgroundResource(R.drawable.button);
                                    holder.getSeguir().setText("Siguiendo");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    holder.getSeguir().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Si no le sigue el usuario y pulsa el boton
                            if(holder.getSeguir().getText().equals("Seguir")){
                                holder.getSeguir().setBackgroundResource(R.drawable.button);
                                holder.getSeguir().setText("Siguiendo");

                                //Guardamos el follow en la base de datos
                                String uid_seguir = FirebaseDatabase.getInstance().getReference("Follow").push().getKey();
                                Seguidor follow = new Seguidor(usuarioList.get(position).getUid(),mAuth.getCurrentUser().getUid());
                                FirebaseDatabase.getInstance().getReference("Follow").child(uid_seguir).setValue(follow);
                            }
                            //Si le sigue el usuario y pulsa el boton
                            else {
                                position_borrada = position;
                                holder.getSeguir().setBackgroundResource(R.drawable.button_social_media);
                                holder.getSeguir().setText("Seguir");

                                //Buscamos en la bbdd
                                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Follow");
                                Query query = myRef.orderByChild("uid_follower").equalTo(mAuth.getCurrentUser().getUid());
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        //Hacemos unfollow del usuario
                                        for (DataSnapshot datos: dataSnapshot.getChildren()) {
                                            if(datos.child("uid_following").getValue().toString().equals(usuarioList.get(position).getUid())){
                                                FirebaseDatabase.getInstance().getReference("Follow").child(datos.getKey()).removeValue();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    });
                } else {
                    holder.getSeguir().setVisibility(View.GONE);
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
     * Obtenemos todos los items que hay
     * @return
     */
    @Override
    public int getItemCount() {
        return usuarioList.size();
    }

    @Override
    public void onClick(View v) {
        if(listener!=null){
            listener.onClick(v);
        }
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
     * Setter de usuarioList
     * @param usuarios
     */
    public void setItems(List<Usuario> usuarios) {
        this.usuarioList = usuarios;
    }
}
