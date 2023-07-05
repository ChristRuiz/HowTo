package com.example.tfg;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ArchivadosHolder  extends RecyclerView.ViewHolder {

    private ImageView foto_usuario;
    private TextView titulo_howto,nombre_completo,nombre_usuario;

    /**
     * Constructor
     * @param itemView
     */
    public ArchivadosHolder(@NonNull View itemView) {
        super(itemView);
        foto_usuario = itemView.findViewById(R.id.foto_perfil_comentario);
        titulo_howto = itemView.findViewById(R.id.titulo_howto);
        nombre_completo = itemView.findViewById(R.id.nombre_completo);
        nombre_usuario = itemView.findViewById(R.id.nombre_usuario);
    }

    /**
     * Getter de la foto del usuario
     * @return
     */
    public ImageView getFoto_usuario() {
        return foto_usuario;
    }

    /**
     * Setter de la foto de usuario
     * @param foto_usuario
     */
    public void setFoto_usuario(ImageView foto_usuario) {
        this.foto_usuario = foto_usuario;
    }

    /**
     * Getter del titulo del HowTo
     * @return
     */
    public TextView getTitulo_howto() {
        return titulo_howto;
    }

    /**
     * Setter del titulo del HowTo
     * @param titulo_howto
     */
    public void setTitulo_howto(TextView titulo_howto) {
        this.titulo_howto = titulo_howto;
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
     * Setter del nombre del usuario
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
