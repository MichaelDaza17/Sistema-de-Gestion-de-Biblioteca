package sena.adso.biblioteca.dto;

import java.sql.Timestamp;

/**
 * DTO para la tabla libros
 * @author ADSO
 */
public class Libro {
    private int id;
    private String titulo;
    private String autor;
    private String editorial;
    private int anoPublicacion;
    private int idCategoria;
    private String isbn;
    private String ubicacion;
    private int cantidadTotal;
    private int cantidadDisponible;
    private String descripcion;
    private String estado; // ACTIVO, INACTIVO, DAÑADO
    private Timestamp createdAt;
    
    // Campos adicionales para joins
    private String categoriaNombre;

    // Constructor vacío
    public Libro() {
    }

    // Constructor completo
    public Libro(int id, String titulo, String autor, String editorial, int anoPublicacion,
                int idCategoria, String isbn, String ubicacion, int cantidadTotal,
                int cantidadDisponible, String descripcion, String estado, Timestamp createdAt) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.anoPublicacion = anoPublicacion;
        this.idCategoria = idCategoria;
        this.isbn = isbn;
        this.ubicacion = ubicacion;
        this.cantidadTotal = cantidadTotal;
        this.cantidadDisponible = cantidadDisponible;
        this.descripcion = descripcion;
        this.estado = estado;
        this.createdAt = createdAt;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public int getAnoPublicacion() {
        return anoPublicacion;
    }

    public void setAnoPublicacion(int anoPublicacion) {
        this.anoPublicacion = anoPublicacion;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public int getCantidadTotal() {
        return cantidadTotal;
    }

    public void setCantidadTotal(int cantidadTotal) {
        this.cantidadTotal = cantidadTotal;
    }

    public int getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(int cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public void setCategoriaNombre(String categoriaNombre) {
        this.categoriaNombre = categoriaNombre;
    }

    public boolean isDisponible() {
        return cantidadDisponible > 0 && "ACTIVO".equals(estado);
    }

    @Override
    public String toString() {
        return "Libro{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", editorial='" + editorial + '\'' +
                ", anoPublicacion=" + anoPublicacion +
                ", cantidadDisponible=" + cantidadDisponible +
                ", estado='" + estado + '\'' +
                '}';
    }
}