package sena.adso.biblioteca.dao;

import sena.adso.biblioteca.dto.Lector;
import sena.adso.biblioteca.model.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestionar lectores
 * @author ADSO
 */
public class LectorDAO {

    /**
     * Obtiene todos los lectores
     * @return Lista de lectores
     */
    public List<Lector> obtenerTodos() {
        List<Lector> lectores = new ArrayList<>();
        String sql = "SELECT * FROM lectores ORDER BY nombres, apellidos";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                lectores.add(mapearLector(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lectores;
    }

    /**
     * Obtiene lectores activos
     * @return Lista de lectores activos
     */
    public List<Lector> obtenerActivos() {
        List<Lector> lectores = new ArrayList<>();
        String sql = "SELECT * FROM lectores WHERE estado = 'ACTIVO' ORDER BY nombres, apellidos";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                lectores.add(mapearLector(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lectores;
    }

    /**
     * Obtiene un lector por ID
     * @param id ID del lector
     * @return Lector o null si no existe
     */
    public Lector obtenerPorId(int id) {
        String sql = "SELECT * FROM lectores WHERE id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearLector(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Busca lectores por documento
     * @param documento documento a buscar
     * @return Lector o null si no existe
     */
    public Lector obtenerPorDocumento(String documento) {
        String sql = "SELECT * FROM lectores WHERE documento = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, documento);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearLector(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Busca lectores por nombre o documento
     * @param busqueda término de búsqueda
     * @return Lista de lectores encontrados
     */
    public List<Lector> buscar(String busqueda) {
        List<Lector> lectores = new ArrayList<>();
        String sql = "SELECT * FROM lectores WHERE nombres LIKE ? OR apellidos LIKE ? OR documento LIKE ? ORDER BY nombres, apellidos";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String patron = "%" + busqueda + "%";
            ps.setString(1, patron);
            ps.setString(2, patron);
            ps.setString(3, patron);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lectores.add(mapearLector(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lectores;
    }

    /**
     * Inserta un nuevo lector
     * @param lector Lector a insertar
     * @return true si se insertó correctamente
     */
    public boolean insertar(Lector lector) {
        String sql = "INSERT INTO lectores (nombres, apellidos, documento, email, telefono, direccion, fecha_nacimiento, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, lector.getNombres());
            ps.setString(2, lector.getApellidos());
            ps.setString(3, lector.getDocumento());
            ps.setString(4, lector.getEmail());
            ps.setString(5, lector.getTelefono());
            ps.setString(6, lector.getDireccion());
            ps.setDate(7, lector.getFechaNacimiento());
            ps.setString(8, lector.getEstado());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualiza un lector
     * @param lector Lector a actualizar
     * @return true si se actualizó correctamente
     */
    public boolean actualizar(Lector lector) {
        String sql = "UPDATE lectores SET nombres = ?, apellidos = ?, documento = ?, email = ?, telefono = ?, direccion = ?, fecha_nacimiento = ?, estado = ? WHERE id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, lector.getNombres());
            ps.setString(2, lector.getApellidos());
            ps.setString(3, lector.getDocumento());
            ps.setString(4, lector.getEmail());
            ps.setString(5, lector.getTelefono());
            ps.setString(6, lector.getDireccion());
            ps.setDate(7, lector.getFechaNacimiento());
            ps.setString(8, lector.getEstado());
            ps.setInt(9, lector.getId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cambia el estado de un lector
     * @param id ID del lector
     * @param estado nuevo estado
     * @return true si se actualizó correctamente
     */
    public boolean cambiarEstado(int id, String estado) {
        String sql = "UPDATE lectores SET estado = ? WHERE id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, estado);
            ps.setInt(2, id);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verifica si existe un documento
     * @param documento documento a verificar
     * @param idExcluir ID a excluir de la búsqueda (para actualizaciones)
     * @return true si existe
     */
    public boolean existeDocumento(String documento, int idExcluir) {
        String sql = "SELECT COUNT(*) FROM lectores WHERE documento = ? AND id != ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, documento);
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
     * Mapea un ResultSet a Lector
     * @param rs ResultSet
     * @return Lector
     * @throws SQLException
     */
    private Lector mapearLector(ResultSet rs) throws SQLException {
        Lector lector = new Lector();
        lector.setId(rs.getInt("id"));
        lector.setNombres(rs.getString("nombres"));
        lector.setApellidos(rs.getString("apellidos"));
        lector.setDocumento(rs.getString("documento"));
        lector.setEmail(rs.getString("email"));
        lector.setTelefono(rs.getString("telefono"));
        lector.setDireccion(rs.getString("direccion"));
        lector.setFechaNacimiento(rs.getDate("fecha_nacimiento"));
        lector.setEstado(rs.getString("estado"));
        lector.setFechaRegistro(rs.getDate("fecha_registro"));
        lector.setCreatedAt(rs.getTimestamp("created_at"));
        return lector;
    }
}