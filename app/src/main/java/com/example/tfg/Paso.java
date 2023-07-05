package com.example.tfg;

public class Paso {

    private String titulo,comentario,imagen;

    /**
     * Constructor
     * @param titulo
     * @param comentario
     * @param imagen
     */
    public Paso(String titulo, String comentario, String imagen) {
        this.titulo = titulo;
        this.comentario = comentario;
        this.imagen = imagen;
    }

    /**
     * Getter del titulo
     * @return
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Setter del titulo
     * @param titulo
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
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
     * Getter de la imagen
     * @return
     */
    public String getImagen() {
        return imagen;
    }

    /**
     * Setter de la imagen
     * @param imagen
     */
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
