package com.example.tfg;

public class HowToAndArchivado {
    private String howto;
    private boolean archivado;
    private long fecha_hora;

    /**
     * Constructor
     * @param howto
     * @param archivado
     * @param fecha_hora
     */
    public HowToAndArchivado(String howto, boolean archivado, long fecha_hora) {
        this.howto = howto;
        this.archivado = archivado;
        this.fecha_hora = fecha_hora;
    }

    /**
     * Getter de HowTo
     * @return
     */
    public String getHowTo() {
        return howto;
    }

    /**
     * Setter de HowTo
     * @param howto
     */
    public void setHowTo(String howto) {
        this.howto = howto;
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
     * Getter de fecha y hora
     * @return
     */
    public long getFecha_hora() {
        return fecha_hora;
    }

    /**
     * Setter de fecha y hora
     * @param fecha_hora
     */
    public void setFecha_hora(long fecha_hora) {
        this.fecha_hora = fecha_hora;
    }
}
