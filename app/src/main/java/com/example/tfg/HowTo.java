package com.example.tfg;

public class HowTo implements Comparable {


    private String uid;
    private String creador;
    private String titulo;
    private String descripcion;
    private String url;
    private boolean archivado = false;
    private long fecha_hora;

    /**
     * Constructor
     * @param uid
     * @param creador
     * @param titulo
     * @param descripcion
     * @param url
     * @param archivado
     */
    public HowTo(String uid, String creador, String titulo, String descripcion, String url, boolean archivado, long fecha_hora) {
        this.uid = uid;
        this.creador = creador;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.url = url;
        this.archivado = archivado;
        this.fecha_hora = fecha_hora;
    }

    /**
     * Constructor
     * @param uid
     * @param creador
     * @param titulo
     * @param descripcion
     * @param url
     */
    public HowTo(String uid, String creador, String titulo, String descripcion, String url) {
        this.uid = uid;
        this.creador = creador;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.url = url;
    }

    public HowTo(String uid, String creador, String titulo, String descripcion, String url, long fecha_hora) {
        this.uid = uid;
        this.creador = creador;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.url = url;
        this.fecha_hora = fecha_hora;
    }

    /**
     * Getter de titulo
     * @return
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Setter de titulo
     * @param titulo
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Getter de descripcion
     * @return
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Setter de descripcion
     * @param descripcion
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
     * Getter de creador
     * @return
     */
    public String getCreador() {
        return creador;
    }

    /**
     * Setter de creador
     * @param creador
     */
    public void setCreador(String creador) {
        this.creador = creador;
    }

    /**
     * Getter de archivado
     * @return
     */
    public boolean isArchivado() {
        return archivado;
    }

    /**
     * Setter de archivado
     * @param archivado
     */
    public void setArchivado(boolean archivado) {
        this.archivado = archivado;
    }

    /**
     * Getter de fecha_hora
     * @return
     */
    public long getFecha_hora() {
        return fecha_hora;
    }

    /**
     * Setter de fecha_hora
     * @param fecha_hora
     */
    public void setFecha_hora(long fecha_hora) {
        this.fecha_hora = fecha_hora;
    }

    /**
     * Ordenamos el HowTo por la fecha
     * @param o
     * @return
     */
    @Override
    public int compareTo(Object o) {
        HowTo howto = null;
        try {
            howto = (HowTo) Class.forName("com.example.tfg.HowTo").cast(o);
        } catch (ClassNotFoundException e) {
            e.getMessage();
        }

        return (int) (this.fecha_hora - howto.getFecha_hora());
    }

    /**
     * Convertimos el objeto en un string
     * @return
     */
    @Override
    public String toString() {
        return "HowTo{" +
                "uid='" + uid + '\'' +
                ", creador='" + creador + '\'' +
                ", titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", url='" + url + '\'' +
                ", archivado=" + archivado +
                ", fecha_hora=" + fecha_hora +
                '}';
    }
}
