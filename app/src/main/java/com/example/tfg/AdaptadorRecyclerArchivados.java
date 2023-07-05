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

public class AdaptadorRecyclerArchivados extends RecyclerView.Adapter<ArchivadosHolder> implements View.OnClickListener {

    private int primera_posicion = 0;
    private View.OnClickListener listener;

    private List<MeGusta_Archivar> archivarList;
    private Context context;

    /**
     * Constructor
     * @param archivarList
     * @param context
     */
    public AdaptadorRecyclerArchivados(List<MeGusta_Archivar> archivarList, Context context) {
        this.archivarList = archivarList;
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
    public ArchivadosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_archivados,parent,false);
        ArchivadosHolder archivadoHolder = new ArchivadosHolder(v);
        v.setOnClickListener(this);
        return archivadoHolder;
    }

    /**
     * Metodo que se encagar de controlar la vista actual
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull final ArchivadosHolder holder, final int position) {

        //Se busca en la bbdd
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("HowTo");
        Query query = myRef.child(archivarList.get(position).getUid_howto());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                holder.getTitulo_howto().setText(""+dataSnapshot.child("titulo").getValue());

                //Se busca en la bbdd los datos del usuario creador del HowTo
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Usuarios");
                Query query = myRef.child(dataSnapshot.child("creador").getValue().toString());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        String url = dataSnapshot.child("url").getValue().toString();
                        String nombre = dataSnapshot.child("nombre").getValue().toString();
                        String username = dataSnapshot.child("username").getValue().toString();

                        Picasso.get().load(url).placeholder(R.drawable.user).fit()
                                .centerCrop().into(holder.getFoto_usuario());
                        holder.getFoto_usuario().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                context.startActivity(new Intent(context,MostrarPerfil.class)
                                        .putExtra("uid",dataSnapshot.getKey()));
                            }
                        });
                        holder.getNombre_completo().setText(nombre);
                        holder.getNombre_usuario().setText("@"+username);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



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
        return archivarList.size();
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
        // Si es el primer item, le damos le animamos
        if (position==primera_posicion)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
        }
    }

    /**
     * Setter de archivarList
     * @param archivarList
     */
    public void setItems(List<MeGusta_Archivar> archivarList) {
        this.archivarList = archivarList;
    }
}
