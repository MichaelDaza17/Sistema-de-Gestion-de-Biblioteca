package sena.adso.biblioteca.controllers;

import sena.adso.biblioteca.dao.UsuarioSistemaDAO;
import sena.adso.biblioteca.dto.UsuarioSistema;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Servlet para gestión CRUD de usuarios del sistema
 * @author ADSO
 */
@WebServlet(name = "UsuarioSistemaServlet", urlPatterns = {"/usuarios"})
public class UsuarioSistemaServlet extends HttpServlet {

    private UsuarioSistemaDAO usuarioDAO;

    @Override
    public void init() throws ServletException {
        usuarioDAO = new UsuarioSistemaDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verificar permisos de administrador
        if (!esAdministrador(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado. Solo administradores pueden gestionar usuarios.");
            return;
        }
        
        String action = request.getParameter("action");
        
        try {
            switch (action != null ? action : "list") {
                case "list":
                    listarUsuarios(request, response);
                    break;
                case "new":
                    mostrarFormularioNuevo(request, response);
                    break;
                case "edit":
                    mostrarFormularioEditar(request, response);
                    break;
                case "view":
                    verUsuario(request, response);
                    break;
                case "delete":
                    eliminarUsuario(request, response);
                    break;
                case "perfil":
                    verPerfil(request, response);
                    break;
                default:
                    listarUsuarios(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar la solicitud: " + e.getMessage());
            listarUsuarios(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        try {
            switch (action != null ? action : "") {
                case "create":
                    crearUsuario(request, response);
                    break;
                case "update":
                    actualizarUsuario(request, response);
                    break;
                case "updateProfile":
                    actualizarPerfil(request, response);
                    break;
                case "changeStatus":
                    cambiarEstado(request, response);
                    break;
                default:
                    response.sendRedirect("usuarios");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar la solicitud: " + e.getMessage());
            listarUsuarios(request, response);
        }
    }

    private void listarUsuarios(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<UsuarioSistema> usuarios = usuarioDAO.obtenerTodos();
        
        request.setAttribute("usuarios", usuarios);
        request.setAttribute("totalUsuarios", usuarios.size());
        
        // Estadísticas
        long usuariosActivos = usuarios.stream().filter(UsuarioSistema::isActivo).count();
        long usuariosInactivos = usuarios.size() - usuariosActivos;
        long administradores = usuarios.stream().filter(u -> "ADMINISTRADOR".equals(u.getRol())).count();
        long bibliotecarios = usuarios.stream().filter(u -> "BIBLIOTECARIO".equals(u.getRol())).count();
        
        request.setAttribute("usuariosActivos", usuariosActivos);
        request.setAttribute("usuariosInactivos", usuariosInactivos);
        request.setAttribute("administradores", administradores);
        request.setAttribute("bibliotecarios", bibliotecarios);
        
        request.getRequestDispatcher("/views/usuarios/list.jsp").forward(request, response);
    }

    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!esAdministrador(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Solo administradores pueden crear usuarios.");
            return;
        }
        
        request.setAttribute("isEdit", false);
        request.getRequestDispatcher("/views/usuarios/form.jsp").forward(request, response);
    }

    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!esAdministrador(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Solo administradores pueden editar usuarios.");
            return;
        }
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            UsuarioSistema usuario = usuarioDAO.obtenerPorId(id);
            
            if (usuario != null) {
                request.setAttribute("usuario", usuario);
                request.setAttribute("isEdit", true);
                request.getRequestDispatcher("/views/usuarios/form.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Usuario no encontrado");
                listarUsuarios(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de usuario inválido");
            listarUsuarios(request, response);
        }
    }

    private void verUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            UsuarioSistema usuario = usuarioDAO.obtenerPorId(id);
            
            if (usuario != null) {
                request.setAttribute("usuario", usuario);
                request.getRequestDispatcher("/views/usuarios/view.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Usuario no encontrado");
                listarUsuarios(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de usuario inválido");
            listarUsuarios(request, response);
        }
    }

    private void verPerfil(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        UsuarioSistema usuarioSesion = (UsuarioSistema) session.getAttribute("usuario");
        
        if (usuarioSesion != null) {
            // Obtener datos actualizados del usuario
            UsuarioSistema usuario = usuarioDAO.obtenerPorId(usuarioSesion.getId());
            request.setAttribute("usuario", usuario);
            request.getRequestDispatcher("/views/usuarios/profile.jsp").forward(request, response);
        } else {
            response.sendRedirect("login");
        }
    }

    private void crearUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!esAdministrador(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Solo administradores pueden crear usuarios.");
            return;
        }
        
        try {
            UsuarioSistema usuario = mapearUsuarioDesdeRequest(request);
            
            // Validaciones
            String validationError = validarUsuario(usuario, 0);
            if (validationError != null) {
                request.setAttribute("error", validationError);
                request.setAttribute("usuario", usuario);
                mostrarFormularioNuevo(request, response);
                return;
            }
            
            if (usuarioDAO.insertar(usuario)) {
                request.setAttribute("success", "Usuario creado exitosamente");
            } else {
                request.setAttribute("error", "Error al crear el usuario");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar los datos: " + e.getMessage());
        }
        
        listarUsuarios(request, response);
    }

    private void actualizarUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!esAdministrador(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Solo administradores pueden actualizar usuarios.");
            return;
        }
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            UsuarioSistema usuario = mapearUsuarioDesdeRequest(request);
            usuario.setId(id);
            
            // Validaciones
            String validationError = validarUsuario(usuario, id);
            if (validationError != null) {
                request.setAttribute("error", validationError);
                request.setAttribute("usuario", usuario);
                request.setAttribute("isEdit", true);
                request.getRequestDispatcher("/views/usuarios/form.jsp").forward(request, response);
                return;
            }
            
            if (usuarioDAO.actualizar(usuario)) {
                request.setAttribute("success", "Usuario actualizado exitosamente");
            } else {
                request.setAttribute("error", "Error al actualizar el usuario");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de usuario inválido");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar los datos: " + e.getMessage());
        }
        
        listarUsuarios(request, response);
    }

    private void actualizarPerfil(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        UsuarioSistema usuarioSesion = (UsuarioSistema) session.getAttribute("usuario");
        
        if (usuarioSesion == null) {
            response.sendRedirect("login");
            return;
        }
        
        try {
            // Solo permitir actualizar ciertos campos en el perfil
            UsuarioSistema usuario = usuarioDAO.obtenerPorId(usuarioSesion.getId());
            if (usuario != null) {
                usuario.setNombres(request.getParameter("nombres"));
                usuario.setApellidos(request.getParameter("apellidos"));
                usuario.setEmail(request.getParameter("email"));
                
                // Solo actualizar contraseña si se proporciona
                String nuevaPassword = request.getParameter("password");
                if (nuevaPassword != null && !nuevaPassword.trim().isEmpty()) {
                    usuario.setPassword(nuevaPassword);
                }
                
                if (usuarioDAO.actualizar(usuario)) {
                    // Actualizar datos en sesión
                    session.setAttribute("usuario", usuario);
                    request.setAttribute("success", "Perfil actualizado exitosamente");
                } else {
                    request.setAttribute("error", "Error al actualizar el perfil");
                }
            }
            
            verPerfil(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar los datos: " + e.getMessage());
            verPerfil(request, response);
        }
    }

    private void eliminarUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!esAdministrador(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Solo administradores pueden eliminar usuarios.");
            return;
        }
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            
            // No permitir eliminar el usuario actual
            HttpSession session = request.getSession();
            UsuarioSistema usuarioSesion = (UsuarioSistema) session.getAttribute("usuario");
            
            if (usuarioSesion.getId() == id) {
                request.setAttribute("error", "No puede eliminar su propio usuario");
                listarUsuarios(request, response);
                return;
            }
            
            if (usuarioDAO.eliminar(id)) {
                request.setAttribute("success", "Usuario eliminado exitosamente");
            } else {
                request.setAttribute("error", "Error al eliminar el usuario");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de usuario inválido");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al eliminar el usuario: " + e.getMessage());
        }
        
        listarUsuarios(request, response);
    }

    private void cambiarEstado(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!esAdministrador(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Solo administradores pueden cambiar estados.");
            return;
        }
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            boolean activo = Boolean.parseBoolean(request.getParameter("activo"));
            
            // Obtener usuario y cambiar estado
            UsuarioSistema usuario = usuarioDAO.obtenerPorId(id);
            if (usuario != null) {
                usuario.setActivo(activo);
                
                if (usuarioDAO.actualizar(usuario)) {
                    request.setAttribute("success", "Estado del usuario actualizado exitosamente");
                } else {
                    request.setAttribute("error", "Error al cambiar el estado del usuario");
                }
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de usuario inválido");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al cambiar el estado: " + e.getMessage());
        }
        
        listarUsuarios(request, response);
    }

    private UsuarioSistema mapearUsuarioDesdeRequest(HttpServletRequest request) {
        UsuarioSistema usuario = new UsuarioSistema();
        
        usuario.setNombres(request.getParameter("nombres"));
        usuario.setApellidos(request.getParameter("apellidos"));
        usuario.setDocumento(request.getParameter("documento"));
        usuario.setEmail(request.getParameter("email"));
        usuario.setUsername(request.getParameter("username"));
        usuario.setPassword(request.getParameter("password"));
        usuario.setRol(request.getParameter("rol"));
        usuario.setActivo(request.getParameter("activo") != null ? 
                        Boolean.parseBoolean(request.getParameter("activo")) : true);
        
        return usuario;
    }

    private String validarUsuario(UsuarioSistema usuario, int idExcluir) {
        if (usuario.getNombres() == null || usuario.getNombres().trim().isEmpty()) {
            return "Los nombres son obligatorios";
        }
        
        if (usuario.getApellidos() == null || usuario.getApellidos().trim().isEmpty()) {
            return "Los apellidos son obligatorios";
        }
        
        if (usuario.getDocumento() == null || usuario.getDocumento().trim().isEmpty()) {
            return "El documento es obligatorio";
        }
        
        if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
            return "El nombre de usuario es obligatorio";
        }
        
        if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
            return "La contraseña es obligatoria";
        }
        
        if (usuario.getPassword().length() < 6) {
            return "La contraseña debe tener al menos 6 caracteres";
        }
        
        if (usuario.getRol() == null || usuario.getRol().trim().isEmpty()) {
            return "El rol es obligatorio";
        }
        
        // Validar username único
        if (usuarioDAO.existeUsername(usuario.getUsername().trim(), idExcluir)) {
            return "Ya existe un usuario con este nombre de usuario";
        }
        
        // Validar email si se proporciona
        if (usuario.getEmail() != null && !usuario.getEmail().trim().isEmpty()) {
            if (!usuario.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                return "El formato del email no es válido";
            }
        }
        
        return null;
    }

    private boolean esAdministrador(HttpServletRequest request) {
        HttpSession session = request.getSession();
        UsuarioSistema usuario = (UsuarioSistema) session.getAttribute("usuario");
        return usuario != null && "ADMINISTRADOR".equals(usuario.getRol());
    }
}