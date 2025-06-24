package sena.adso.biblioteca.dto;

import java.sql.Timestamp;

/**
 * DTO para la tabla categorias
 * @author ADSO
 */
public class Categoria {
    private int id;
    private String nombre;
    private String descripcion;
    private Timestamp createdAt;

    // Constructor vacío
    public Categoria() {
    }

    // Constructor completo
    public Categoria(int id, String nombre, String descripcion, Timestamp createdAt) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.createdAt = createdAt;
    }

    // Constructor sin ID (para inserts)
    public Categoria(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}