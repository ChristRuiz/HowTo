package com.example.tfg;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.varunest.sparkbutton.SparkEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class AdaptadorRecyclerHowTo extends RecyclerView.Adapter<HowToHolder> implements View.OnClickListener {

    private int primera_posicion = 0;
    private View.OnClickListener listener;

    public int position_borrada;

    private List<HowTo> howToList;
    private Context context;

    // Inicializacimos la Autentificación de Firebase
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    /**
     * Constructor
     * @param howToList
     * @param context
     */
    public AdaptadorRecyclerHowTo(List<HowTo> howToList, Context context) {
        this.howToList = howToList;
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
    public HowToHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_howto,parent,false);
        HowToHolder comentarioHolder = new HowToHolder(v);
        v.setOnClickListener(this);
        return comentarioHolder;
    }

    /**
     * Metodo que se encagar de controlar la vista actual
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull final HowToHolder holder, final int position) {

        //Buscamos en la bbdd los datos del usuario
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Usuarios");
        Query query = myRef.child(howToList.get(position).getCreador());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.get().load(dataSnapshot.child("url").getValue().toString()).placeholder(R.drawable.user).fit()
                        .centerCrop().into(holder.getFoto_perfil());
                holder.getFoto_perfil().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context,MostrarPerfil.class)
                                .putExtra("uid",howToList.get(position).getCreador()));
                    }
                });
                Log.d("Nombre",dataSnapshot.child("nombre").getValue().toString());
                Log.d("Position",String.valueOf(position));
                holder.getNombre_completo().setText(dataSnapshot.child("nombre").getValue().toString());
                holder.getNombre_usuario().setText("@" + dataSnapshot.child("username").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Asignamos el titulo, la descripcion y la imagen principal del HowTo
        if(howToList.get(position).getUrl() != null){
            holder.getFoto_howto().setVisibility(View.VISIBLE);
            Picasso.get().load(howToList.get(position).getUrl()).placeholder(R.drawable.placeholder_image).fit()
                    .centerCrop().into(holder.getFoto_howto());
        } else {
            holder.getFoto_howto().setVisibility(View.GONE);
        }
        holder.getTitulo_howto().setText(howToList.get(position).getTitulo());
        holder.getDescripcion_howto().setText(howToList.get(position).getDescripcion());

        //Damos formato a la fecha que se creo el HowTo
        Timestamp date = new Timestamp((howToList.get(position).getFecha_hora()-Long.parseLong("9999999999"))*-1,0);
        Date sin_formato = date.toDate();
        Locale spanish = new Locale("es","ES");
        String fecha_str = new SimpleDateFormat("EEEE dd 'de' MMMM 'del' yyyy 'a las' HH:mm:ss",spanish).format(sin_formato);

        //Cada palabra hacemos que inicie con una mayuscula
        char[] caracteres = fecha_str.toCharArray();
        caracteres[0] = Character.toUpperCase(caracteres[0]);
        for (int i = 0; i < fecha_str.length()-2; i++){
            // Es 'palabra'
            if (caracteres[i] == ' ' || caracteres[i] == '.' || caracteres[i] == ','){
                // Reemplazamos
                caracteres[i + 1] = Character.toUpperCase(caracteres[i + 1]);
            }
        }

        holder.getFecha().setText(new String(caracteres));

        //Mostramos si es un HowTo archivado
        if(howToList.get(position).isArchivado()){
            holder.getArchivado().setVisibility(View.VISIBLE);
        } else {
            holder.getArchivado().setVisibility(View.GONE);
        }

        //Buscamos en la bbdd el numero de comentarios del howto
        DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("Comentarios_Respuestas");
        Query query2 = myRef2.orderByChild("uid_padre").equalTo(howToList.get(position).getUid());
        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Obtenemos el numero de comentarios que tiene el HowTo
                holder.getNumero_comentarios().setText("" + dataSnapshot.getChildrenCount());

                FirebaseDatabase.getInstance().getReference("Estadisticas").child(howToList.get(position).getUid()).child("comments").setValue(dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Buscamos en la bbdd si el usuario ya le ha dado a me gusta
        DatabaseReference myRef4 = FirebaseDatabase.getInstance().getReference("Likes");
        Query query4 = myRef4.orderByChild("uid_howto").equalTo(howToList.get(position).getUid());
        query4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Obtenemos el numero de likes que tiene el HowTo
                holder.getNumero_likes().setText("" + dataSnapshot.getChildrenCount());
                FirebaseDatabase.getInstance().getReference("Estadisticas").child(howToList.get(position).getUid()).child("likes").setValue(dataSnapshot.getChildrenCount());

                Iterator<DataSnapshot> iterador = dataSnapshot.getChildren().iterator();
                while (iterador.hasNext()) {
                    DataSnapshot datos = iterador.next();
                    if (datos.child("uid_usuario").getValue().toString().equals(mAuth.getCurrentUser().getUid())) {
                        holder.getMe_gusta().setChecked(true);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Buscamos en la bbdd si el usuario ya ha archivado el howto
        DatabaseReference myRef5 = FirebaseDatabase.getInstance().getReference("Archivados");
        Query query5 = myRef5.orderByChild("uid_howto").equalTo(howToList.get(position).getUid());
        query5.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterator<DataSnapshot> iterador = dataSnapshot.getChildren().iterator();
                while (iterador.hasNext()) {
                    DataSnapshot datos = iterador.next();
                    if (datos.child("uid_usuario").getValue().toString().equals(mAuth.getCurrentUser().getUid())) {
                        holder.getArchivar().setChecked(true);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        holder.getArchivar().setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                //Le ha dado a archivar
                if (buttonState) {
                    //Guardamos el howto archivado en la BBDD
                    String uid_comentario = FirebaseDatabase.getInstance().getReference("Archivados").push().getKey();
                    MeGusta_Archivar archivado = new MeGusta_Archivar(mAuth.getCurrentUser().getUid(), howToList.get(position).getUid());
                    FirebaseDatabase.getInstance().getReference("Archivados").child(uid_comentario).setValue(archivado);

                    String uid_howtoarchive = FirebaseDatabase.getInstance().getReference("HowToAndArchivados").child(mAuth.getCurrentUser().getUid()).push().getKey();

                    HowToAndArchivado howToAndArchivado = new HowToAndArchivado(howToList.get(position).getUid(), true, Long.parseLong("9999999999") - Timestamp.now().getSeconds());
                    FirebaseDatabase.getInstance().getReference("HowToAndArchivados").child(mAuth.getCurrentUser().getUid()).child(uid_howtoarchive).setValue(howToAndArchivado);

                    //Creamos una notificacion si no es el propio usuario
                    if(!howToList.get(position).getCreador().equals(mAuth.getCurrentUser().getUid())){
                        String uid_notificacion = FirebaseDatabase.getInstance().getReference("Notificaciones").push().getKey();
                        Notificacion notificacion = new Notificacion(1,uid_notificacion,howToList.get(position).getCreador(),mAuth.getCurrentUser().getUid());
                        notificacion.setUid_howto(howToList.get(position).getUid());
                        FirebaseDatabase.getInstance().getReference("Notificaciones").child(uid_notificacion).setValue(notificacion);
                    }
                }
                //Le ha dado a desarchivar
                else {
                    position_borrada = position;
                    //Buscamos en la bbdd
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Archivados");
                    Query query = myRef.orderByChild("uid_howto").equalTo(howToList.get(position).getUid());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            //Borramos el howto archivado del usuario
                            Iterator<DataSnapshot> iterador = dataSnapshot.getChildren().iterator();
                            while (iterador.hasNext()) {
                                DataSnapshot datos = iterador.next();
                                if (datos.child("uid_usuario").getValue().toString().equals(mAuth.getCurrentUser().getUid())) {
                                    FirebaseDatabase.getInstance().getReference("Archivados").child(datos.getKey()).removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    //Buscamos en la bbdd
                    DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("HowToAndArchivados");
                    Query query2 = myRef2.child(mAuth.getCurrentUser().getUid()).orderByChild("howTo").equalTo(howToList.get(position).getUid());
                    query2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            //Borramos el howto archivado del usuario
                            Iterator<DataSnapshot> iterador = dataSnapshot.getChildren().iterator();
                            while (iterador.hasNext()){
                                DataSnapshot datos = iterador.next();
                                if(Boolean.parseBoolean(datos.child("archivado").getValue().toString())){
                                    FirebaseDatabase.getInstance().getReference("HowToAndArchivados").child(mAuth.getCurrentUser().getUid()).child(datos.getKey()).removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {

            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {

            }
        });

        holder.getMe_gusta().setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                //Le ha dado me gusta
                if(buttonState){
                    //Guardamos el like en la BBDD
                    String uid_comentario = FirebaseDatabase.getInstance().getReference("Likes").push().getKey();
                    MeGusta_Archivar like = new MeGusta_Archivar(mAuth.getCurrentUser().getUid(),howToList.get(position).getUid());
                    FirebaseDatabase.getInstance().getReference("Likes").child(uid_comentario).setValue(like);

                    //Creamos una notificacion si no es el propio usuario
                    if(!howToList.get(position).getCreador().equals(mAuth.getCurrentUser().getUid())){
                        String uid_notificacion = FirebaseDatabase.getInstance().getReference("Notificaciones").push().getKey();
                        Notificacion notificacion = new Notificacion(0,uid_notificacion,howToList.get(position).getCreador(),mAuth.getCurrentUser().getUid());
                        notificacion.setUid_howto(howToList.get(position).getUid());
                        FirebaseDatabase.getInstance().getReference("Notificaciones").child(uid_notificacion).setValue(notificacion);
                    }
                }
                //Le quita el me gusta
                else {
                    //Buscamos en la bbdd
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Likes");
                    Query query = myRef.orderByChild("uid_howto").equalTo(howToList.get(position).getUid());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            //Borramos el like del usuario
                            Iterator<DataSnapshot> iterador = dataSnapshot.getChildren().iterator();
                            while (iterador.hasNext()){
                                DataSnapshot datos = iterador.next();
                                if(datos.child("uid_usuario").getValue().toString().equals(mAuth.getCurrentUser().getUid())){
                                    FirebaseDatabase.getInstance().getReference("Likes").child(datos.getKey()).removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {

            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {

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
        return howToList.size();
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
        //Si es el primer item, le damos la animacion
        if (position==primera_posicion)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
        }
    }

    /**
     * Setter de howToList
     * @param howtos
     */
    public void setItems(List<HowTo> howtos) {
        this.howToList = howtos;
    }


    @Override
    public void onClick(View v) {
        if(listener!=null){
            listener.onClick(v);
        }
    }
}
