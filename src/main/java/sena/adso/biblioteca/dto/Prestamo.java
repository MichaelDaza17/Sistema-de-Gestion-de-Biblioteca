package sena.adso.biblioteca.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * DTO para la tabla prestamos
 * @author ADSO
 */
public class Prestamo {
    private int id;
    private int idLibro;
    private int idLector;
    private int idUsuarioSistema;
    private Date fechaPrestamo;
    private Date fechaDevolucionEsperada;
    private Date fechaDevolucionReal;
    private String estado; // ACTIVO, DEVUELTO, VENCIDO, PERDIDO
    private String observaciones;
    private BigDecimal multa;
    private Timestamp createdAt;
    
    // Campos adicionales para joins
    private String libroTitulo;
    private String libroAutor;
    private String lectorNombre;
    private String lectorDocumento;
    private String bibliotecarioNombre;

    // Constructor vacío
    public Prestamo() {
    }

    // Constructor completo
    public Prestamo(int id, int idLibro, int idLector, int idUsuarioSistema,
                   Date fechaPrestamo, Date fechaDevolucionEsperada, Date fechaDevolucionReal,
                   String estado, String observaciones, BigDecimal multa, Timestamp createdAt) {
        this.id = id;
        this.idLibro = idLibro;
        this.idLector = idLector;
        this.idUsuarioSistema = idUsuarioSistema;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
        this.fechaDevolucionReal = fechaDevolucionReal;
        this.estado = estado;
        this.observaciones = observaciones;
        this.multa = multa;
        this.createdAt = createdAt;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(int idLibro) {
        this.idLibro = idLibro;
    }

    public int getIdLector() {
        return idLector;
    }

    public void setIdLector(int idLector) {
        this.idLector = idLector;
    }

    public int getIdUsuarioSistema() {
        return idUsuarioSistema;
    }

    public void setIdUsuarioSistema(int idUsuarioSistema) {
        this.idUsuarioSistema = idUsuarioSistema;
    }

    public Date getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(Date fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public Date getFechaDevolucionEsperada() {
        return fechaDevolucionEsperada;
    }

    public void setFechaDevolucionEsperada(Date fechaDevolucionEsperada) {
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
    }

    public Date getFechaDevolucionReal() {
        return fechaDevolucionReal;
    }

    public void setFechaDevolucionReal(Date fechaDevolucionReal) {
        this.fechaDevolucionReal = fechaDevolucionReal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public BigDecimal getMulta() {
        return multa;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getLibroTitulo() {
        return libroTitulo;
    }

    public void setLibroTitulo(String libroTitulo) {
        this.libroTitulo = libroTitulo;
    }

    public String getLibroAutor() {
        return libroAutor;
    }

    public void setLibroAutor(String libroAutor) {
        this.libroAutor = libroAutor;
    }

    public String getLectorNombre() {
        return lectorNombre;
    }

    public void setLectorNombre(String lectorNombre) {
        this.lectorNombre = lectorNombre;
    }

    public String getLectorDocumento() {
        return lectorDocumento;
    }

    public void setLectorDocumento(String lectorDocumento) {
        this.lectorDocumento = lectorDocumento;
    }

    public String getBibliotecarioNombre() {
        return bibliotecarioNombre;
    }

    public void setBibliotecarioNombre(String bibliotecarioNombre) {
        this.bibliotecarioNombre = bibliotecarioNombre;
    }

    // Métodos utilitarios
    public boolean isVencido() {
        if (fechaDevolucionReal != null) return false; // Ya devuelto
        Date hoy = new Date(System.currentTimeMillis());
        return fechaDevolucionEsperada.before(hoy);
    }

    public long getDiasRetraso() {
        if (fechaDevolucionReal != null) return 0; // Ya devuelto
        Date hoy = new Date(System.currentTimeMillis());
        if (fechaDevolucionEsperada.before(hoy)) {
            long diff = hoy.getTime() - fechaDevolucionEsperada.getTime();
            return diff / (24 * 60 * 60 * 1000); // Convertir a días
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Prestamo{" +
                "id=" + id +
                ", idLibro=" + idLibro +
                ", idLector=" + idLector +
                ", fechaPrestamo=" + fechaPrestamo +
                ", fechaDevolucionEsperada=" + fechaDevolucionEsperada +
                ", estado='" + estado + '\'' +
                '}';
    }
}