package com.example.tfg;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.varunest.sparkbutton.SparkButton;

public class HowToHolder extends RecyclerView.ViewHolder {

    private TextView archivado, nombre_completo, nombre_usuario, titulo_howto, descripcion_howto, numero_comentarios, numero_likes, fecha;
    private ImageView foto_perfil, foto_howto;
    private SparkButton me_gusta,archivar;

    /**
     * Constructor
     * @param itemView
     */
    public HowToHolder(@NonNull View itemView) {
        super(itemView);
        archivado = itemView.findViewById(R.id.archivado);
        nombre_completo = itemView.findViewById(R.id.nombre_completo);
        nombre_usuario = itemView.findViewById(R.id.nombre_usuario);
        titulo_howto = itemView.findViewById(R.id.titulo_howto);
        descripcion_howto = itemView.findViewById(R.id.descripcion_howto);
        numero_comentarios = itemView.findViewById(R.id.numero_comentarios);
        numero_likes = itemView.findViewById(R.id.numero_likes);
        foto_perfil = itemView.findViewById(R.id.foto_perfil);
        foto_howto = itemView.findViewById(R.id.foto_howto);
        me_gusta = itemView.findViewById(R.id.like);
        archivar = itemView.findViewById(R.id.archivar);
        fecha = itemView.findViewById(R.id.fecha);
    }

    /**
     * Getter de archivado
     * @return
     */
    public TextView getArchivado() {
        return archivado;
    }

    /**
     * Setter de archivado
     * @param archivado
     */
    public void setArchivado(TextView archivado) {
        this.archivado = archivado;
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
     * Getter de nombre de usuario
     * @return
     */
    public TextView getNombre_usuario() {
        return nombre_usuario;
    }

    /**
     * Setter de nombre de usuario
     * @param nombre_usuario
     */
    public void setNombre_usuario(TextView nombre_usuario) {
        this.nombre_usuario = nombre_usuario;
    }

    /**
     * Getter de titulo del howto
     * @return
     */
    public TextView getTitulo_howto() {
        return titulo_howto;
    }

    /**
     * Setter de titulo del howto
     * @param titulo_howto
     */
    public void setTitulo_howto(TextView titulo_howto) {
        this.titulo_howto = titulo_howto;
    }

    /**
     * Getter de descripcion del howto
     * @return
     */
    public TextView getDescripcion_howto() {
        return descripcion_howto;
    }

    /**
     * Setter de descripcion del howto
     * @param descripcion_howto
     */
    public void setDescripcion_howto(TextView descripcion_howto) {
        this.descripcion_howto = descripcion_howto;
    }

    /**
     * Getter de numero de comentarios
     * @return
     */
    public TextView getNumero_comentarios() {
        return numero_comentarios;
    }

    /**
     * Setter de numero de comentarios
     * @param numero_comentarios
     */
    public void setNumero_comentarios(TextView numero_comentarios) {
        this.numero_comentarios = numero_comentarios;
    }

    /**
     * Getter de numero de likes
     * @return
     */
    public TextView getNumero_likes() {
        return numero_likes;
    }

    /**
     * Setter de numero de likes
     * @param numero_likes
     */
    public void setNumero_likes(TextView numero_likes) {
        this.numero_likes = numero_likes;
    }

    /**
     * Getter de foto de perfil
     * @return
     */
    public ImageView getFoto_perfil() {
        return foto_perfil;
    }

    /**
     * Setter de foto de perfil
     * @param foto_perfil
     */
    public void setFoto_perfil(ImageView foto_perfil) {
        this.foto_perfil = foto_perfil;
    }

    /**
     * Getter de foto del howto
     * @return
     */
    public ImageView getFoto_howto() {
        return foto_howto;
    }

    /**
     * Setter de foto del howto
     * @param foto_howto
     */
    public void setFoto_howto(ImageView foto_howto) {
        this.foto_howto = foto_howto;
    }

    /**
     * Getter de me gusta
     * @return
     */
    public SparkButton getMe_gusta() {
        return me_gusta;
    }

    /**
     * Setter de me gusta
     * @param me_gusta
     */
    public void setMe_gusta(SparkButton me_gusta) {
        this.me_gusta = me_gusta;
    }

    /**
     * Getter de archivar
     * @return
     */
    public SparkButton getArchivar() {
        return archivar;
    }

    /**
     * Setter de archivar
     * @param archivar
     */
    public void setArchivar(SparkButton archivar) {
        this.archivar = archivar;
    }

    /**
     * Getter de fecha
     * @return
     */
    public TextView getFecha() {
        return fecha;
    }

    /**
     * Setter de fecha
     * @param fecha
     */
    public void setFecha(TextView fecha) {
        this.fecha = fecha;
    }
}
