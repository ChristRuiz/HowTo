package com.example.tfg;

public class Notificacion {

    //O = Me Gusta, 1 = Archivar, 2 = Comentario
    private int tipo_notificacion;
    private String uid_notificacion, uid_usuario_notificado, uid_usuario_notifica, uid_howto, uid_comentario;

    /**
     * Constructor
     * @param tipo_notificacion
     * @param uid_notificacion
     * @param uid_usuario_notificado
     * @param uid_usuario_notifica
     */
    public Notificacion(int tipo_notificacion, String uid_notificacion, String uid_usuario_notificado, String uid_usuario_notifica) {
        this.tipo_notificacion = tipo_notificacion;
        this.uid_notificacion = uid_notificacion;
        this.uid_usuario_notificado = uid_usuario_notificado;
        this.uid_usuario_notifica = uid_usuario_notifica;
    }

    /**
     * Getter de tipo de notificacion
     * @return
     */
    public int getTipo_notificacion() {
        return tipo_notificacion;
    }

    /**
     * Setter de tipo de notificacion
     * @param tipo_notificacion
     */
    public void setTipo_notificacion(int tipo_notificacion) {
        this.tipo_notificacion = tipo_notificacion;
    }

    /**
     * Getter de uid de notificacion
     * @return
     */
    public String getUid_notificacion() {
        return uid_notificacion;
    }

    /**
     * Setter de uid de notificacion
     * @param uid_notificacion
     */
    public void setUid_notificacion(String uid_notificacion) {
        this.uid_notificacion = uid_notificacion;
    }

    /**
     * Getter de uid del usuario notificado
     * @return
     */
    public String getUid_usuario_notificado() {
        return uid_usuario_notificado;
    }

    /**
     * Setter de uid del usuario notificado
     * @param uid_usuario_notificado
     */
    public void setUid_usuario_notificado(String uid_usuario_notificado) {
        this.uid_usuario_notificado = uid_usuario_notificado;
    }

    /**
     * Getter de uid del usuario que crea la notificacion
     * @return
     */
    public String getUid_usuario_notifica() {
        return uid_usuario_notifica;
    }

    /**
     * Setter de uid del usuario que crea la notificacion
     * @param uid_usuario_notifica
     */
    public void setUid_usuario_notifica(String uid_usuario_notifica) {
        this.uid_usuario_notifica = uid_usuario_notifica;
    }

    /**
     * Getter de uid del howto
     * @return
     */
    public String getUid_howto() {
        return uid_howto;
    }

    /**
     * Setter de uid del howto
     * @param uid_howto
     */
    public void setUid_howto(String uid_howto) {
        this.uid_howto = uid_howto;
    }

    /**
     * Getter del uid del comentario
     * @return
     */
    public String getUid_comentario() {
        return uid_comentario;
    }

    /**
     * Setter del uid del comentario
     * @param uid_comentario
     */
    public void setUid_comentario(String uid_comentario) {
        this.uid_comentario = uid_comentario;
    }
}
