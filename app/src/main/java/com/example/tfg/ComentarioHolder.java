package com.example.tfg;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



public class ComentarioHolder extends RecyclerView.ViewHolder {

    private ImageView foto_usuario;
    private TextView comentario,nombre_completo,nombre_usuario,numero_respuestas;


    /**
     * Constructor
     * @param itemView
     */
    public ComentarioHolder(@NonNull View itemView) {
        super(itemView);
        foto_usuario = itemView.findViewById(R.id.foto_perfil_comentario);
        comentario = itemView.findViewById(R.id.comentario);
        nombre_completo = itemView.findViewById(R.id.nombre_completo);
        nombre_usuario = itemView.findViewById(R.id.nombre_usuario);
        numero_respuestas = itemView.findViewById(R.id.numero_respuestas);

    }

    /**
     * Getter del numero de respuestas
     * @return
     */
    public TextView getNumero_respuestas() {
        return numero_respuestas;
    }

    /**
     * Setter del numero de respuestas
     * @param numero_respuestas
     */
    public void setNumero_respuestas(TextView numero_respuestas) {
        this.numero_respuestas = numero_respuestas;
    }

    /**
     * Getter de foto de usuario
     * @return
     */
    public ImageView getFoto_usuario() {
        return foto_usuario;
    }

    /**
     * Setter de foto de usuario
     * @param foto_usuario
     */
    public void setFoto_usuario(ImageView foto_usuario) {
        this.foto_usuario = foto_usuario;
    }

    /**
     * Getter de comentario
     * @return
     */
    public TextView getComentario() {
        return comentario;
    }

    /**
     * Setter de comentario
     * @param comentario
     */
    public void setComentario(TextView comentario) {
        this.comentario = comentario;
    }

    /**
     * Getter del nombre completo
     * @return
     */
    public TextView getNombre_completo() {
        return nombre_completo;
    }

    /**
     * Setter del nombre completo
     * @param nombre_completo
     */
    public void setNombre_completo(TextView nombre_completo) {
        this.nombre_completo = nombre_completo;
    }

    /**
     * Getter del nombre de usuario
     * @return
     */
    public TextView getNombre_usuario() {
        return nombre_usuario;
    }

    /**
     * Setter del nombre de usuario
     * @param nombre_usuario
     */
    public void setNombre_usuario(TextView nombre_usuario) {
        this.nombre_usuario = nombre_usuario;
    }
}
