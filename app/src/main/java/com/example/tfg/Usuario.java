package com.example.tfg;

public class Usuario  {



    private String email;
    private String nombre;
    private String url;
    private String proveedor;
    private String username;
    private String uid;

    /**
     * Constructor
     * @param email
     * @param nombre
     * @param url
     * @param proveedor
     * @param username
     */
    public Usuario(String email, String nombre, String url, String proveedor, String username) {
        this.email = email;
        this.nombre = nombre;
        this.url = url;
        this.proveedor = proveedor;
        this.username = username;
    }

    public Usuario(String uid) {
        this.uid = uid;
    }

    /**
     * Getter de email
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter de email
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Getter de nombre
     * @return
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Setter de nombre
     * @param nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Getter de url
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     * Setter de url
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Getter de proveedor
     * @return
     */
    public String getProveedor() {
        return proveedor;
    }

    /**
     * Setter de proveedor
     * @param proveedor
     */
    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    /**
     * Getter de username
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter de username
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
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

    /**
     * Pasamos el objeto a un string
     * @return
     */
    @Override
    public String toString() {
        return "Usuario{" +
                "email='" + email + '\'' +
                ", nombre='" + nombre + '\'' +
                ", url='" + url + '\'' +
                ", proveedor='" + proveedor + '\'' +
                ", username='" + username + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }

    /**
     * Comprobamos si el usuario pasado por parametro tiene el mismo uid que el usuario actual
     * @param usuario
     * @return
     */
    public boolean equalsUid(Usuario usuario) {
        return this.uid.equals(usuario.uid);
    }
}

