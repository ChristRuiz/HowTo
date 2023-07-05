package com.example.tfg;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UsuarioHolder extends RecyclerView.ViewHolder {

    private ImageView foto_usuario;
    private TextView nombre_completo,nombre_usuario;
    private Button seguir;

    /**
     * Constructor
     * @param itemView
     */
    public UsuarioHolder(@NonNull View itemView) {
        super(itemView);
        foto_usuario = itemView.findViewById(R.id.foto_perfil);
        nombre_completo = itemView.findViewById(R.id.nombre_completo);
        nombre_usuario = itemView.findViewById(R.id.nombre_usuario);
        seguir = itemView.findViewById(R.id.seguir);

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
     * Getter de nombre completo
     * @return
     */
    public TextView getNombre_completo() {
        return nombre_completo;
    }

    /**
     * Setter de nombre completo
     * @param nombre_completo
     */
    public void setNombre_completo(TextView nombre_completo) {
        this.nombre_completo = nombre_completo;
    }

    /**
     * Getter de nombre usuario
     * @return
     */
    public TextView getNombre_usuario() {
        return nombre_usuario;
    }

    /**
     * Setter de nombre usuario
     * @param nombre_usuario
     */
    public void setNombre_usuario(TextView nombre_usuario) {
        this.nombre_usuario = nombre_usuario;
    }

    /**
     * Getter de seguir
     * @return
     */
    public Button getSeguir() {
        return seguir;
    }

    /**
     * Setter de seguir
     * @param seguir
     */
    public void setSeguir(Button seguir) {
        this.seguir = seguir;
    }
}
