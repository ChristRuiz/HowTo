package com.example.tfg;

public class Comentario {

    private String uid,uid_creador,comentario,uid_padre;
    private long fecha_creacion;

    /**
     * Constructor
     * @param uid
     * @param uid_creador
     * @param comentario
     * @param uid_padre
     * @param fecha_creacion
     */
    public Comentario(String uid, String uid_creador, String comentario, String uid_padre, long fecha_creacion) {
        this.uid = uid;
        this.uid_creador = uid_creador;
        this.comentario = comentario;
        this.uid_padre = uid_padre;
        this.fecha_creacion = fecha_creacion;
    }

    /**
     * Getter de uid
     * @return
     */
    public String getUid() {
        return uid;
    }

    /**
     * Setter de uid
     * @param uid
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    //Getter del uid del creador
    public String getUid_creador() {
        return uid_creador;
    }

    /**
     * Setter del uid del creador
     * @param uid_creador
     */
    public void setUid_creador(String uid_creador) {
        this.uid_creador = uid_creador;
    }

    /**
     * Getter del comentario
     * @return
     */
    public String getComentario() {
        return comentario;
    }

    /**
     * Setter del comentario
     * @param comentario
     */
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    /**
     * Getter del uid padre
     * @return
     */
    public String getUid_padre() {
        return uid_padre;
    }

    /**
     * Setter del uid padre
     * @param uid_padre
     */
    public void setUid_padre(String uid_padre) {
        this.uid_padre = uid_padre;
    }

    /**
     * Getter de la fecha de creacion
     * @return
     */
    public long getFecha_creacion() {
        return fecha_creacion;
    }

    /**
     * Setter de la fecha de creacion
     * @param fecha_creacion
     */
    public void setFecha_creacion(long fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }
}
