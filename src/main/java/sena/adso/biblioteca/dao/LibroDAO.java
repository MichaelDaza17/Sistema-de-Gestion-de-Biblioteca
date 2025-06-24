package sena.adso.biblioteca.dao;

import sena.adso.biblioteca.dto.Libro;
import sena.adso.biblioteca.model.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestionar libros
 * @author ADSO
 */
public class LibroDAO {

    /**
     * Obtiene todos los libros con información de categoría
     * @return Lista de libros
     */
    public List<Libro> obtenerTodos() {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT l.*, c.nombre as categoria_nombre FROM libros l " +
                    "INNER JOIN categorias c ON l.id_categoria = c.id " +
                    "ORDER BY l.titulo";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                libros.add(mapearLibro(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return libros;
    }

    /**
     * Obtiene libros disponibles para préstamo
     * @return Lista de libros disponibles
     */
    public List<Libro> obtenerDisponibles() {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT l.*, c.nombre as categoria_nombre FROM libros l " +
                    "INNER JOIN categorias c ON l.id_categoria = c.id " +
                    "WHERE l.estado = 'ACTIVO' AND l.cantidad_disponible > 0 " +
                    "ORDER BY l.titulo";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                libros.add(mapearLibro(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return libros;
    }

    /**
     * Obtiene un libro por ID
     * @param id ID del libro
     * @return Libro o null si no existe
     */
    public Libro obtenerPorId(int id) {
        String sql = "SELECT l.*, c.nombre as categoria_nombre FROM libros l " +
                    "INNER JOIN categorias c ON l.id_categoria = c.id " +
                    "WHERE l.id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearLibro(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Busca libros por título, autor o categoría
     * @param busqueda término de búsqueda
     * @return Lista de libros encontrados
     */
    public List<Libro> buscar(String busqueda) {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT l.*, c.nombre as categoria_nombre FROM libros l " +
                    "INNER JOIN categorias c ON l.id_categoria = c.id " +
                    "WHERE l.titulo LIKE ? OR l.autor LIKE ? OR c.nombre LIKE ? " +
                    "ORDER BY l.titulo";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String patron = "%" + busqueda + "%";
            ps.setString(1, patron);
            ps.setString(2, patron);
            ps.setString(3, patron);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    libros.add(mapearLibro(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return libros;
    }

    /**
     * Busca libros disponibles para consulta pública
     * @param busqueda término de búsqueda
     * @return Lista de libros disponibles encontrados
     */
    public List<Libro> buscarDisponibles(String busqueda) {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT l.*, c.nombre as categoria_nombre FROM libros l " +
                    "INNER JOIN categorias c ON l.id_categoria = c.id " +
                    "WHERE l.estado = 'ACTIVO' AND l.cantidad_disponible > 0 " +
                    "AND (l.titulo LIKE ? OR l.autor LIKE ? OR c.nombre LIKE ?) " +
                    "ORDER BY l.titulo";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String patron = "%" + busqueda + "%";
            ps.setString(1, patron);
            ps.setString(2, patron);
            ps.setString(3, patron);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    libros.add(mapearLibro(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return libros;
    }

    /**
     * Obtiene libros por categoría
     * @param idCategoria ID de la categoría
     * @return Lista de libros de la categoría
     */
    public List<Libro> obtenerPorCategoria(int idCategoria) {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT l.*, c.nombre as categoria_nombre FROM libros l " +
                    "INNER JOIN categorias c ON l.id_categoria = c.id " +
                    "WHERE l.id_categoria = ? ORDER BY l.titulo";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idCategoria);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    libros.add(mapearLibro(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return libros;
    }

    /**
     * Inserta un nuevo libro
     * @param libro Libro a insertar
     * @return true si se insertó correctamente
     */
    public boolean insertar(Libro libro) {
        String sql = "INSERT INTO libros (titulo, autor, editorial, año_publicacion, id_categoria, isbn, ubicacion, cantidad_total, cantidad_disponible, descripcion, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getEditorial());
            ps.setInt(4, libro.getAnoPublicacion());
            ps.setInt(5, libro.getIdCategoria());
            ps.setString(6, libro.getIsbn());
            ps.setString(7, libro.getUbicacion());
            ps.setInt(8, libro.getCantidadTotal());
            ps.setInt(9, libro.getCantidadDisponible());
            ps.setString(10, libro.getDescripcion());
            ps.setString(11, libro.getEstado());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualiza un libro
     * @param libro Libro a actualizar
     * @return true si se actualizó correctamente
     */
    public boolean actualizar(Libro libro) {
        String sql = "UPDATE libros SET titulo = ?, autor = ?, editorial = ?, año_publicacion = ?, id_categoria = ?, isbn = ?, ubicacion = ?, cantidad_total = ?, cantidad_disponible = ?, descripcion = ?, estado = ? WHERE id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getEditorial());
            ps.setInt(4, libro.getAnoPublicacion());
            ps.setInt(5, libro.getIdCategoria());
            ps.setString(6, libro.getIsbn());
            ps.setString(7, libro.getUbicacion());
            ps.setInt(8, libro.getCantidadTotal());
            ps.setInt(9, libro.getCantidadDisponible());
            ps.setString(10, libro.getDescripcion());
            ps.setString(11, libro.getEstado());
            ps.setInt(12, libro.getId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualiza la cantidad disponible de un libro
     * @param idLibro ID del libro
     * @param nuevaCantidad nueva cantidad disponible
     * @return true si se actualizó correctamente
     */
    public boolean actualizarCantidadDisponible(int idLibro, int nuevaCantidad) {
        String sql = "UPDATE libros SET cantidad_disponible = ? WHERE id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, nuevaCantidad);
            ps.setInt(2, idLibro);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Reduce la cantidad disponible en 1 (para préstamo)
     * @param idLibro ID del libro
     * @return true si se actualizó correctamente
     */
    public boolean reducirCantidadDisponible(int idLibro) {
        String sql = "UPDATE libros SET cantidad_disponible = cantidad_disponible - 1 WHERE id = ? AND cantidad_disponible > 0";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idLibro);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Aumenta la cantidad disponible en 1 (para devolución)
     * @param idLibro ID del libro
     * @return true si se actualizó correctamente
     */
    public boolean aumentarCantidadDisponible(int idLibro) {
        String sql = "UPDATE libros SET cantidad_disponible = cantidad_disponible + 1 WHERE id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idLibro);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina un libro (cambiar estado a INACTIVO)
     * @param id ID del libro a eliminar
     * @return true si se eliminó correctamente
     */
    public boolean eliminar(int id) {
        String sql = "UPDATE libros SET estado = 'INACTIVO' WHERE id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verifica si existe un ISBN
     * @param isbn ISBN a verificar
     * @param idExcluir ID a excluir de la búsqueda (para actualizaciones)
     * @return true si existe
     */
    public boolean existeIsbn(String isbn, int idExcluir) {
        if (isbn == null || isbn.trim().isEmpty()) return false;
        
        String sql = "SELECT COUNT(*) FROM libros WHERE isbn = ? AND id != ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, isbn);
            ps.setInt(2, idExcluir);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Mapea un ResultSet a Libro
     * @param rs ResultSet
     * @return Libro
     * @throws SQLException
     */
    private Libro mapearLibro(ResultSet rs) throws SQLException {
        Libro libro = new Libro();
        libro.setId(rs.getInt("id"));
        libro.setTitulo(rs.getString("titulo"));
        libro.setAutor(rs.getString("autor"));
        libro.setEditorial(rs.getString("editorial"));
        libro.setAnoPublicacion(rs.getInt("año_publicacion"));
        libro.setIdCategoria(rs.getInt("id_categoria"));
        libro.setIsbn(rs.getString("isbn"));
        libro.setUbicacion(rs.getString("ubicacion"));
        libro.setCantidadTotal(rs.getInt("cantidad_total"));
        libro.setCantidadDisponible(rs.getInt("cantidad_disponible"));
        libro.setDescripcion(rs.getString("descripcion"));
        libro.setEstado(rs.getString("estado"));
        libro.setCreatedAt(rs.getTimestamp("created_at"));
        
        // Campo adicional del join
        libro.setCategoriaNombre(rs.getString("categoria_nombre"));
        
        return libro;
    }
}