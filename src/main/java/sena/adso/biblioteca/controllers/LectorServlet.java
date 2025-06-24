package sena.adso.biblioteca.controllers;

import sena.adso.biblioteca.dao.LectorDAO;
import sena.adso.biblioteca.dao.PrestamoDAO;
import sena.adso.biblioteca.dto.Lector;
import sena.adso.biblioteca.dto.Prestamo;
import sena.adso.biblioteca.util.ExcelGenerator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

/**
 * Servlet para gestión CRUD de lectores
 * @author ADSO
 */
@WebServlet(name = "LectorServlet", urlPatterns = {"/lectores"})
public class LectorServlet extends HttpServlet {

    private LectorDAO lectorDAO;
    private PrestamoDAO prestamoDAO;

    @Override
    public void init() throws ServletException {
        lectorDAO = new LectorDAO();
        prestamoDAO = new PrestamoDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        try {
            switch (action != null ? action : "list") {
                case "list":
                    listarLectores(request, response);
                    break;
                case "new":
                    mostrarFormularioNuevo(request, response);
                    break;
                case "edit":
                    mostrarFormularioEditar(request, response);
                    break;
                case "view":
                    verLector(request, response);
                    break;
                case "delete":
                    cambiarEstadoLector(request, response);
                    break;
                case "search":
                    buscarLectores(request, response);
                    break;
                case "export":
                    exportarLectores(request, response);
                    break;
                case "prestamos":
                    verPrestamosLector(request, response);
                    break;
                default:
                    listarLectores(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar la solicitud: " + e.getMessage());
            listarLectores(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        try {
            switch (action != null ? action : "") {
                case "create":
                    crearLector(request, response);
                    break;
                case "update":
                    actualizarLector(request, response);
                    break;
                case "search":
                    buscarLectores(request, response);
                    break;
                case "changeStatus":
                    cambiarEstadoLector(request, response);
                    break;
                default:
                    response.sendRedirect("lectores");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar la solicitud: " + e.getMessage());
            listarLectores(request, response);
        }
    }

    private void listarLectores(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<Lector> lectores = lectorDAO.obtenerTodos();
        
        request.setAttribute("lectores", lectores);
        request.setAttribute("totalLectores", lectores.size());
        
        // Estadísticas por estado
        long lectoresActivos = lectores.stream().filter(l -> "ACTIVO".equals(l.getEstado())).count();
        long lectoresSuspendidos = lectores.stream().filter(l -> "SUSPENDIDO".equals(l.getEstado())).count();
        long lectoresInactivos = lectores.stream().filter(l -> "INACTIVO".equals(l.getEstado())).count();
        
        request.setAttribute("lectoresActivos", lectoresActivos);
        request.setAttribute("lectoresSuspendidos", lectoresSuspendidos);
        request.setAttribute("lectoresInactivos", lectoresInactivos);
        
        request.getRequestDispatcher("/views/lectores/list.jsp").forward(request, response);
    }

    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setAttribute("isEdit", false);
        request.getRequestDispatcher("/views/lectores/form.jsp").forward(request, response);
    }

    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Lector lector = lectorDAO.obtenerPorId(id);
            
            if (lector != null) {
                request.setAttribute("lector", lector);
                request.setAttribute("isEdit", true);
                request.getRequestDispatcher("/views/lectores/form.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Lector no encontrado");
                listarLectores(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de lector inválido");
            listarLectores(request, response);
        }
    }

    private void verLector(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Lector lector = lectorDAO.obtenerPorId(id);
            
            if (lector != null) {
                // Obtener historial de préstamos del lector
                List<Prestamo> prestamos = prestamoDAO.obtenerPorLector(id);
                
                request.setAttribute("lector", lector);
                request.setAttribute("prestamos", prestamos);
                request.setAttribute("totalPrestamos", prestamos.size());
                
                // Estadísticas de préstamos
                long prestamosActivos = prestamos.stream().filter(p -> "ACTIVO".equals(p.getEstado())).count();
                long prestamosDevueltos = prestamos.stream().filter(p -> "DEVUELTO".equals(p.getEstado())).count();
                
                request.setAttribute("prestamosActivos", prestamosActivos);
                request.setAttribute("prestamosDevueltos", prestamosDevueltos);
                
                request.getRequestDispatcher("/views/lectores/view.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Lector no encontrado");
                listarLectores(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de lector inválido");
            listarLectores(request, response);
        }
    }

    private void crearLector(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            Lector lector = mapearLectorDesdeRequest(request);
            
            // Validaciones
            String validationError = validarLector(lector, 0);
            if (validationError != null) {
                request.setAttribute("error", validationError);
                request.setAttribute("lector", lector);
                mostrarFormularioNuevo(request, response);
                return;
            }
            
            if (lectorDAO.insertar(lector)) {
                request.setAttribute("success", "Lector registrado exitosamente");
            } else {
                request.setAttribute("error", "Error al registrar el lector");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar los datos: " + e.getMessage());
        }
        
        listarLectores(request, response);
    }

    private void actualizarLector(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Lector lector = mapearLectorDesdeRequest(request);
            lector.setId(id);
            
            // Validaciones
            String validationError = validarLector(lector, id);
            if (validationError != null) {
                request.setAttribute("error", validationError);
                request.setAttribute("lector", lector);
                request.setAttribute("isEdit", true);
                request.getRequestDispatcher("/views/lectores/form.jsp").forward(request, response);
                return;
            }
            
            if (lectorDAO.actualizar(lector)) {
                request.setAttribute("success", "Lector actualizado exitosamente");
            } else {
                request.setAttribute("error", "Error al actualizar el lector");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de lector inválido");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar los datos: " + e.getMessage());
        }
        
        listarLectores(request, response);
    }

    private void cambiarEstadoLector(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String nuevoEstado = request.getParameter("estado");
            
            if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
                nuevoEstado = "INACTIVO";
            }
            
            // Verificar si tiene préstamos activos antes de suspender/inactivar
            if (!"ACTIVO".equals(nuevoEstado) && prestamoDAO.tienePrestamosActivos(id)) {
                request.setAttribute("error", "No se puede cambiar el estado del lector porque tiene préstamos activos");
                listarLectores(request, response);
                return;
            }
            
            if (lectorDAO.cambiarEstado(id, nuevoEstado)) {
                request.setAttribute("success", "Estado del lector actualizado exitosamente");
            } else {
                request.setAttribute("error", "Error al cambiar el estado del lector");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de lector inválido");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al cambiar el estado: " + e.getMessage());
        }
        
        listarLectores(request, response);
    }

    private void buscarLectores(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String busqueda = request.getParameter("busqueda");
        
        List<Lector> lectores;
        
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            lectores = lectorDAO.buscar(busqueda.trim());
            request.setAttribute("busqueda", busqueda.trim());
        } else {
            lectores = lectorDAO.obtenerTodos();
        }
        
        request.setAttribute("lectores", lectores);
        request.setAttribute("totalLectores", lectores.size());
        
        request.getRequestDispatcher("/views/lectores/list.jsp").forward(request, response);
    }

    private void exportarLectores(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            List<Lector> lectores = lectorDAO.obtenerTodos();
            ExcelGenerator.generarReporteLectores(lectores, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al generar el reporte: " + e.getMessage());
            listarLectores(request, response);
        }
    }

    private void verPrestamosLector(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Lector lector = lectorDAO.obtenerPorId(id);
            
            if (lector != null) {
                List<Prestamo> prestamos = prestamoDAO.obtenerPorLector(id);
                
                request.setAttribute("lector", lector);
                request.setAttribute("prestamos", prestamos);
                request.getRequestDispatcher("/views/lectores/prestamos.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Lector no encontrado");
                listarLectores(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de lector inválido");
            listarLectores(request, response);
        }
    }

    private Lector mapearLectorDesdeRequest(HttpServletRequest request) {
        Lector lector = new Lector();
        
        lector.setNombres(request.getParameter("nombres"));
        lector.setApellidos(request.getParameter("apellidos"));
        lector.setDocumento(request.getParameter("documento"));
        lector.setEmail(request.getParameter("email"));
        lector.setTelefono(request.getParameter("telefono"));
        lector.setDireccion(request.getParameter("direccion"));
        
        // Fecha de nacimiento
        String fechaNacimiento = request.getParameter("fechaNacimiento");
        if (fechaNacimiento != null && !fechaNacimiento.trim().isEmpty()) {
            try {
                lector.setFechaNacimiento(Date.valueOf(fechaNacimiento));
            } catch (IllegalArgumentException e) {
                // Fecha inválida, se maneja en validación
            }
        }
        
        lector.setEstado(request.getParameter("estado"));
        
        return lector;
    }

    private String validarLector(Lector lector, int idExcluir) {
        if (lector.getNombres() == null || lector.getNombres().trim().isEmpty()) {
            return "Los nombres son obligatorios";
        }
        
        if (lector.getApellidos() == null || lector.getApellidos().trim().isEmpty()) {
            return "Los apellidos son obligatorios";
        }
        
        if (lector.getDocumento() == null || lector.getDocumento().trim().isEmpty()) {
            return "El documento es obligatorio";
        }
        
        // Validar documento único
        if (lectorDAO.existeDocumento(lector.getDocumento().trim(), idExcluir)) {
            return "Ya existe un lector con este documento";
        }
        
        // Validar email si se proporciona
        if (lector.getEmail() != null && !lector.getEmail().trim().isEmpty()) {
            if (!lector.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                return "El formato del email no es válido";
            }
        }
        
        // Validar fecha de nacimiento
        if (lector.getFechaNacimiento() != null) {
            Date hoy = new Date(System.currentTimeMillis());
            if (lector.getFechaNacimiento().after(hoy)) {
                return "La fecha de nacimiento no puede ser futura";
            }
        }
        
        return null;
    }
}