package com.example.tfg;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NotificacionHolder extends RecyclerView.ViewHolder {

    private ImageView tipo_notificacion, foto_perfil;
    private TextView nombre_usuario, texto_notificacion;

    /**
     * Constructor
     * @param itemView
     */
    public NotificacionHolder(@NonNull View itemView) {
        super(itemView);
        tipo_notificacion = itemView.findViewById(R.id.tipo_notificacion);
        foto_perfil = itemView.findViewById(R.id.foto_perfil);
        nombre_usuario = itemView.findViewById(R.id.nombre_completo);
        texto_notificacion = itemView.findViewById(R.id.texto_notificacion);

    }

    /**
     * Getter del tipo de notificacion
     * @return
     */
    public ImageView getTipo_notificacion() {
        return tipo_notificacion;
    }

    /**
     * Setter del tipo de notificacion
     * @param tipo_notificacion
     */
    public void setTipo_notificacion(ImageView tipo_notificacion) {
        this.tipo_notificacion = tipo_notificacion;
    }

    /**
     * Getter de la foto de perfil
     * @return
     */
    public ImageView getFoto_perfil() {
        return foto_perfil;
    }

    /**
     * Setter de la foto de perfil
     * @param foto_perfil
     */
    public void setFoto_perfil(ImageView foto_perfil) {
        this.foto_perfil = foto_perfil;
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

    /**
     * Getter del texto de la notificacion
     * @return
     */
    public TextView getTexto_notificacion() {
        return texto_notificacion;
    }

    /**
     * Setter del texto de la notificacion
     * @param texto_notificacion
     */
    public void setTexto_notificacion(TextView texto_notificacion) {
        this.texto_notificacion = texto_notificacion;
    }
}
