package com.example.tfg;

public class MeGusta_Archivar {

    private String uid_usuario;
    private String uid_howto;

    /**
     * Constructor
     * @param uid_usuario
     * @param uid_howto
     */
    public MeGusta_Archivar(String uid_usuario, String uid_howto) {
        this.uid_usuario = uid_usuario;
        this.uid_howto = uid_howto;
    }

    /**
     * Getter del uid del usuario
     * @return
     */
    public String getUid_usuario() {
        return uid_usuario;
    }

    /**
     * Setter del uid del usuario
     * @param uid_usuario
     */
    public void setUid_usuario(String uid_usuario) {
        this.uid_usuario = uid_usuario;
    }

    /**
     * Getter del uid del howto
     * @return
     */
    public String getUid_howto() {
        return uid_howto;
    }

    /**
     * Setter del uid del howto
     * @param uid_howto
     */
    public void setUid_howto(String uid_howto) {
        this.uid_howto = uid_howto;
    }
}
