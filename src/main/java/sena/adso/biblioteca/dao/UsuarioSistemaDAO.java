package sena.adso.biblioteca.dao;

import sena.adso.biblioteca.dto.UsuarioSistema;
import sena.adso.biblioteca.model.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestionar usuarios del sistema (bibliotecarios y administradores)
 * @author ADSO
 */
public class UsuarioSistemaDAO {

    /**
     * Autentica un usuario del sistema
     * @param username nombre de usuario
     * @param password contraseña
     * @return UsuarioSistema si las credenciales son válidas, null si no
     */
    public UsuarioSistema autenticar(String username, String password) {
        String sql = "SELECT * FROM usuarios_sistema WHERE username = ? AND password = ? AND activo = 1";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, password);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Obtiene todos los usuarios del sistema
     * @return Lista de usuarios
     */
    public List<UsuarioSistema> obtenerTodos() {
        List<UsuarioSistema> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios_sistema ORDER BY nombres, apellidos";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    /**
     * Obtiene un usuario por ID
     * @param id ID del usuario
     * @return UsuarioSistema o null si no existe
     */
    public UsuarioSistema obtenerPorId(int id) {
        String sql = "SELECT * FROM usuarios_sistema WHERE id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Inserta un nuevo usuario del sistema
     * @param usuario UsuarioSistema a insertar
     * @return true si se insertó correctamente
     */
    public boolean insertar(UsuarioSistema usuario) {
        String sql = "INSERT INTO usuarios_sistema (nombres, apellidos, documento, email, username, password, rol, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, usuario.getNombres());
            ps.setString(2, usuario.getApellidos());
            ps.setString(3, usuario.getDocumento());
            ps.setString(4, usuario.getEmail());
            ps.setString(5, usuario.getUsername());
            ps.setString(6, usuario.getPassword());
            ps.setString(7, usuario.getRol());
            ps.setBoolean(8, usuario.isActivo());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualiza un usuario del sistema
     * @param usuario UsuarioSistema a actualizar
     * @return true si se actualizó correctamente
     */
    public boolean actualizar(UsuarioSistema usuario) {
        String sql = "UPDATE usuarios_sistema SET nombres = ?, apellidos = ?, documento = ?, email = ?, username = ?, password = ?, rol = ?, activo = ? WHERE id = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, usuario.getNombres());
            ps.setString(2, usuario.getApellidos());
            ps.setString(3, usuario.getDocumento());
            ps.setString(4, usuario.getEmail());
            ps.setString(5, usuario.getUsername());
            ps.setString(6, usuario.getPassword());
            ps.setString(7, usuario.getRol());
            ps.setBoolean(8, usuario.isActivo());
            ps.setInt(9, usuario.getId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina un usuario del sistema (desactivar)
     * @param id ID del usuario a eliminar
     * @return true si se eliminó correctamente
     */
    public boolean eliminar(int id) {
        String sql = "UPDATE usuarios_sistema SET activo = 0 WHERE id = ?";
        
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
     * Verifica si existe un username
     * @param username username a verificar
     * @param idExcluir ID a excluir de la búsqueda (para actualizaciones)
     * @return true si existe
     */
    public boolean existeUsername(String username, int idExcluir) {
        String sql = "SELECT COUNT(*) FROM usuarios_sistema WHERE username = ? AND id != ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
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
     * Mapea un ResultSet a UsuarioSistema
     * @param rs ResultSet
     * @return UsuarioSistema
     * @throws SQLException
     */
    private UsuarioSistema mapearUsuario(ResultSet rs) throws SQLException {
        UsuarioSistema usuario = new UsuarioSistema();
        usuario.setId(rs.getInt("id"));
        usuario.setNombres(rs.getString("nombres"));
        usuario.setApellidos(rs.getString("apellidos"));
        usuario.setDocumento(rs.getString("documento"));
        usuario.setEmail(rs.getString("email"));
        usuario.setUsername(rs.getString("username"));
        usuario.setPassword(rs.getString("password"));
        usuario.setRol(rs.getString("rol"));
        usuario.setActivo(rs.getBoolean("activo"));
        usuario.setCreatedAt(rs.getTimestamp("created_at"));
        return usuario;
    }
}