package sena.adso.biblioteca.dao;

import sena.adso.biblioteca.dto.Prestamo;
import sena.adso.biblioteca.model.Conexion;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestionar préstamos
 * @author ADSO
 */
public class PrestamoDAO {

    /**
     * Obtiene todos los préstamos con información completa
     * @return Lista de préstamos
     */
    public List<Prestamo> obtenerTodos() {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT p.*, l.titulo as libro_titulo, l.autor as libro_autor, " +
                    "CONCAT(le.nombres, ' ', le.apellidos) as lector_nombre, le.documento as lector_documento, " +
                    "CONCAT(u.nombres, ' ', u.apellidos) as bibliotecario_nombre " +
                    "FROM prestamos p " +
                    "INNER JOIN libros l ON p.id_libro = l.id " +
                    "INNER JOIN lectores le ON p.id_lector = le.id " +
                    "INNER JOIN usuarios_sistema u ON p.id_usuario_sistema = u.id " +
                    "ORDER BY p.fecha_prestamo DESC";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                prestamos.add(mapearPrestamo(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prestamos;
    }

    /**
     * Obtiene préstamos activos
     * @return Lista de préstamos activos
     */
    public List<Prestamo> obtenerActivos() {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT p.*, l.titulo as libro_titulo, l.autor as libro_autor, " +
                    "CONCAT(le.nombres, ' ', le.apellidos) as lector_nombre, le.documento as lector_documento, " +
                    "CONCAT(u.nombres, ' ', u.apellidos) as bibliotecario_nombre " +
                    "FROM prestamos p " +
                    "INNER JOIN libros l ON p.id_libro = l.id " +
                    "INNER JOIN lectores le ON p.id_lector = le.id " +
                    "INNER JOIN usuarios_sistema u ON p.id_usuario_sistema = u.id " +
                    "WHERE p.estado = 'ACTIVO' " +
                    "ORDER BY p.fecha_devolucion_esperada ASC";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                prestamos.add(mapearPrestamo(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prestamos;
    }

    /**
     * Obtiene préstamos vencidos
     * @return Lista de préstamos vencidos
     */
    public List<Prestamo> obtenerVencidos() {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT p.*, l.titulo as libro_titulo, l.autor as libro_autor, " +
                    "CONCAT(le.nombres, ' ', le.apellidos) as lector_nombre, le.documento as lector_documento, " +
                    "CONCAT(u.nombres, ' ', u.apellidos) as bibliotecario_nombre " +
                    "FROM prestamos p " +
                    "INNER JOIN libros l ON p.id_libro = l.id " +
                    "INNER JOIN lectores le ON p.id_lector = le.id " +
                    "INNER JOIN usuarios_sistema u ON p.id_usuario_sistema = u.id " +
                    "WHERE p.estado = 'ACTIVO' AND p.fecha_devolucion_esperada < CURDATE() " +
                    "ORDER BY p.fecha_devolucion_esperada ASC";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                prestamos.add(mapearPrestamo(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prestamos;
    }

    /**
     * Obtiene préstamos de un lector específico
     * @param idLector ID del lector
     * @return Lista de préstamos del lector
     */
    public List<Prestamo> obtenerPorLector(int idLector) {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT p.*, l.titulo as libro_titulo, l.autor as libro_autor, " +
                    "CONCAT(le.nombres, ' ', le.apellidos) as lector_nombre, le.documento as lector_documento, " +
                    "CONCAT(u.nombres, ' ', u.apellidos) as bibliotecario_nombre " +
                    "FROM prestamos p " +
                    "INNER JOIN libros l ON p.id_libro = l.id " +
                    "INNER JOIN lectores le ON p.id_lector = le.id " +
                    "INNER JOIN usuarios_sistema u ON p.id_usuario_sistema = u.id " +
                    "WHERE p.id_lector = ? " +
                    "ORDER BY p.fecha_prestamo DESC";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idLector);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    prestamos.add(mapearPrestamo(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prestamos;
    }

    /**
     * Obtiene un préstamo por ID
     * @param id ID del préstamo
     * @return Prestamo o null si no existe
     */
    public Prestamo obtenerPorId(int id) {
        String sql = "SELECT p.*, l.titulo as libro_titulo, l.autor as libro_autor, " +
                    "CONCAT(le.nombres, ' ', le.apellidos) as lector_nombre, le.documento as lector_documento, " +
                    "CONCAT(u.nombres, ' ', u.apellidos) as bibliotecario_nombre " +
                    "FROM prestamos p " +
                    "INNER JOIN libros l ON p.id_libro = l.id " +
                    "INNER JOIN lectores le ON p.id_lector = le.id " +
                    "INNER JOIN usuarios_sistema u ON p.id_usuario_sistema = u.id " +
                    "WHERE p.id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPrestamo(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Registra un nuevo préstamo
     * @param prestamo Prestamo a registrar
     * @return ID del préstamo insertado o -1 si falló
     */
    public int registrarPrestamo(Prestamo prestamo) {
        String sql = "INSERT INTO prestamos (id_libro, id_lector, id_usuario_sistema, fecha_prestamo, fecha_devolucion_esperada, estado, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, prestamo.getIdLibro());
            ps.setInt(2, prestamo.getIdLector());
            ps.setInt(3, prestamo.getIdUsuarioSistema());
            ps.setDate(4, prestamo.getFechaPrestamo());
            ps.setDate(5, prestamo.getFechaDevolucionEsperada());
            ps.setString(6, prestamo.getEstado());
            ps.setString(7, prestamo.getObservaciones());
            
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Registra la devolución de un préstamo
     * @param idPrestamo ID del préstamo
     * @param fechaDevolucion fecha de devolución
     * @param observaciones observaciones de la devolución
     * @param multa multa aplicada
     * @return true si se registró correctamente
     */
    public boolean registrarDevolucion(int idPrestamo, Date fechaDevolucion, String observaciones, BigDecimal multa) {
        String sql = "UPDATE prestamos SET fecha_devolucion_real = ?, estado = 'DEVUELTO', observaciones = ?, multa = ? WHERE id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDate(1, fechaDevolucion);
            ps.setString(2, observaciones);
            ps.setBigDecimal(3, multa);
            ps.setInt(4, idPrestamo);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Renueva un préstamo (extiende la fecha de devolución)
     * @param idPrestamo ID del préstamo
     * @param nuevaFechaDevolucion nueva fecha de devolución esperada
     * @return true si se renovó correctamente
     */
    public boolean renovarPrestamo(int idPrestamo, Date nuevaFechaDevolucion) {
        String sql = "UPDATE prestamos SET fecha_devolucion_esperada = ? WHERE id = ? AND estado = 'ACTIVO'";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDate(1, nuevaFechaDevolucion);
            ps.setInt(2, idPrestamo);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Marca un préstamo como perdido
     * @param idPrestamo ID del préstamo
     * @param observaciones observaciones
     * @param multa multa aplicada
     * @return true si se marcó correctamente
     */
    public boolean marcarComoPerdido(int idPrestamo, String observaciones, BigDecimal multa) {
        String sql = "UPDATE prestamos SET estado = 'PERDIDO', observaciones = ?, multa = ? WHERE id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, observaciones);
            ps.setBigDecimal(2, multa);
            ps.setInt(3, idPrestamo);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verifica si un lector tiene préstamos activos
     * @param idLector ID del lector
     * @return true si tiene préstamos activos
     */
    public boolean tienePrestamosActivos(int idLector) {
        String sql = "SELECT COUNT(*) FROM prestamos WHERE id_lector = ? AND estado = 'ACTIVO'";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idLector);
            
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
     * Cuenta préstamos activos
     * @return número de préstamos activos
     */
    public int contarPrestamosActivos() {
        String sql = "SELECT COUNT(*) FROM prestamos WHERE estado = 'ACTIVO'";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Cuenta préstamos vencidos
     * @return número de préstamos vencidos
     */
    public int contarPrestamosVencidos() {
        String sql = "SELECT COUNT(*) FROM prestamos WHERE estado = 'ACTIVO' AND fecha_devolucion_esperada < CURDATE()";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Mapea un ResultSet a Prestamo
     * @param rs ResultSet
     * @return Prestamo
     * @throws SQLException
     */
    private Prestamo mapearPrestamo(ResultSet rs) throws SQLException {
        Prestamo prestamo = new Prestamo();
        prestamo.setId(rs.getInt("id"));
        prestamo.setIdLibro(rs.getInt("id_libro"));
        prestamo.setIdLector(rs.getInt("id_lector"));
        prestamo.setIdUsuarioSistema(rs.getInt("id_usuario_sistema"));
        prestamo.setFechaPrestamo(rs.getDate("fecha_prestamo"));
        prestamo.setFechaDevolucionEsperada(rs.getDate("fecha_devolucion_esperada"));
        prestamo.setFechaDevolucionReal(rs.getDate("fecha_devolucion_real"));
        prestamo.setEstado(rs.getString("estado"));
        prestamo.setObservaciones(rs.getString("observaciones"));
        prestamo.setMulta(rs.getBigDecimal("multa"));
        prestamo.setCreatedAt(rs.getTimestamp("created_at"));
        
        // Campos adicionales del join
        prestamo.setLibroTitulo(rs.getString("libro_titulo"));
        prestamo.setLibroAutor(rs.getString("libro_autor"));
        prestamo.setLectorNombre(rs.getString("lector_nombre"));
        prestamo.setLectorDocumento(rs.getString("lector_documento"));
        prestamo.setBibliotecarioNombre(rs.getString("bibliotecario_nombre"));
        
        return prestamo;
    }
}