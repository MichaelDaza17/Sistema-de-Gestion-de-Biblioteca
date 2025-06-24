package sena.adso.biblioteca.controllers;

import sena.adso.biblioteca.dao.PrestamoDAO;
import sena.adso.biblioteca.dao.LibroDAO;
import sena.adso.biblioteca.dao.LectorDAO;
import sena.adso.biblioteca.dto.Prestamo;
import sena.adso.biblioteca.dto.Libro;
import sena.adso.biblioteca.dto.Lector;
import sena.adso.biblioteca.dto.UsuarioSistema;
import sena.adso.biblioteca.util.ExcelGenerator;
import sena.adso.biblioteca.util.PDFGenerator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Servlet para gestión completa de préstamos
 * @author ADSO
 */
@WebServlet(name = "PrestamoServlet", urlPatterns = {"/prestamos"})
public class PrestamoServlet extends HttpServlet {

    private PrestamoDAO prestamoDAO;
    private LibroDAO libroDAO;
    private LectorDAO lectorDAO;

    @Override
    public void init() throws ServletException {
        prestamoDAO = new PrestamoDAO();
        libroDAO = new LibroDAO();
        lectorDAO = new LectorDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        try {
            switch (action != null ? action : "list") {
                case "list":
                    listarPrestamos(request, response);
                    break;
                case "active":
                    listarPrestamosActivos(request, response);
                    break;
                case "overdue":
                    listarPrestamosVencidos(request, response);
                    break;
                case "new":
                    mostrarFormularioNuevo(request, response);
                    break;
                case "view":
                    verPrestamo(request, response);
                    break;
                case "return":
                    mostrarFormularioDevolucion(request, response);
                    break;
                case "renew":
                    renovarPrestamo(request, response);
                    break;
                case "export":
                    exportarPrestamos(request, response);
                    break;
                case "exportPdf":
                    exportarPrestamosActivosPDF(request, response);
                    break;
                case "receipt":
                    generarComprobante(request, response);
                    break;
                default:
                    listarPrestamos(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar la solicitud: " + e.getMessage());
            listarPrestamos(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        try {
            switch (action != null ? action : "") {
                case "create":
                    crearPrestamo(request, response);
                    break;
                case "return":
                    procesarDevolucion(request, response);
                    break;
                case "markLost":
                    marcarComoPerdido(request, response);
                    break;
                default:
                    response.sendRedirect("prestamos");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar la solicitud: " + e.getMessage());
            listarPrestamos(request, response);
        }
    }

    private void listarPrestamos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<Prestamo> prestamos = prestamoDAO.obtenerTodos();
        
        request.setAttribute("prestamos", prestamos);
        request.setAttribute("totalPrestamos", prestamos.size());
        
        // Estadísticas
        long prestamosActivos = prestamos.stream().filter(p -> "ACTIVO".equals(p.getEstado())).count();
        long prestamosDevueltos = prestamos.stream().filter(p -> "DEVUELTO".equals(p.getEstado())).count();
        long prestamosVencidos = prestamos.stream().filter(p -> "ACTIVO".equals(p.getEstado()) && p.isVencido()).count();
        long prestamosPerdidos = prestamos.stream().filter(p -> "PERDIDO".equals(p.getEstado())).count();
        
        request.setAttribute("prestamosActivos", prestamosActivos);
        request.setAttribute("prestamosDevueltos", prestamosDevueltos);
        request.setAttribute("prestamosVencidos", prestamosVencidos);
        request.setAttribute("prestamosPerdidos", prestamosPerdidos);
        
        request.getRequestDispatcher("/views/prestamos/list.jsp").forward(request, response);
    }

    private void listarPrestamosActivos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<Prestamo> prestamos = prestamoDAO.obtenerActivos();
        
        request.setAttribute("prestamos", prestamos);
        request.setAttribute("totalPrestamos", prestamos.size());
        request.setAttribute("soloActivos", true);
        
        request.getRequestDispatcher("/views/prestamos/list.jsp").forward(request, response);
    }

    private void listarPrestamosVencidos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<Prestamo> prestamos = prestamoDAO.obtenerVencidos();
        
        request.setAttribute("prestamos", prestamos);
        request.setAttribute("totalPrestamos", prestamos.size());
        request.setAttribute("soloVencidos", true);
        
        request.getRequestDispatcher("/views/prestamos/list.jsp").forward(request, response);
    }

    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<Libro> librosDisponibles = libroDAO.obtenerDisponibles();
        List<Lector> lectoresActivos = lectorDAO.obtenerActivos();
        
        request.setAttribute("libros", librosDisponibles);
        request.setAttribute("lectores", lectoresActivos);
        
        // Fecha por defecto (hoy + 14 días)
        LocalDate fechaDevolucion = LocalDate.now().plusDays(14);
        request.setAttribute("fechaDevolucionSugerida", fechaDevolucion.toString());
        
        request.getRequestDispatcher("/views/prestamos/form.jsp").forward(request, response);
    }

    private void verPrestamo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Prestamo prestamo = prestamoDAO.obtenerPorId(id);
            
            if (prestamo != null) {
                request.setAttribute("prestamo", prestamo);
                request.getRequestDispatcher("/views/prestamos/view.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Préstamo no encontrado");
                listarPrestamos(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de préstamo inválido");
            listarPrestamos(request, response);
        }
    }

    private void crearPrestamo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        UsuarioSistema usuario = (UsuarioSistema) session.getAttribute("usuario");
        
        try {
            int idLibro = Integer.parseInt(request.getParameter("idLibro"));
            int idLector = Integer.parseInt(request.getParameter("idLector"));
            String fechaDevolucionStr = request.getParameter("fechaDevolucion");
            String observaciones = request.getParameter("observaciones");
            
            // Validaciones
            Libro libro = libroDAO.obtenerPorId(idLibro);
            if (libro == null || !libro.isDisponible()) {
                request.setAttribute("error", "El libro seleccionado no está disponible");
                mostrarFormularioNuevo(request, response);
                return;
            }
            
            Lector lector = lectorDAO.obtenerPorId(idLector);
            if (lector == null || !"ACTIVO".equals(lector.getEstado())) {
                request.setAttribute("error", "El lector seleccionado no está activo");
                mostrarFormularioNuevo(request, response);
                return;
            }
            
            Date fechaDevolucion = Date.valueOf(fechaDevolucionStr);
            Date hoy = Date.valueOf(LocalDate.now());
            
            if (fechaDevolucion.before(hoy)) {
                request.setAttribute("error", "La fecha de devolución no puede ser anterior a hoy");
                mostrarFormularioNuevo(request, response);
                return;
            }
            
            // Crear préstamo
            Prestamo prestamo = new Prestamo();
            prestamo.setIdLibro(idLibro);
            prestamo.setIdLector(idLector);
            prestamo.setIdUsuarioSistema(usuario.getId());
            prestamo.setFechaPrestamo(hoy);
            prestamo.setFechaDevolucionEsperada(fechaDevolucion);
            prestamo.setEstado("ACTIVO");
            prestamo.setObservaciones(observaciones != null ? observaciones : "");
            prestamo.setMulta(BigDecimal.ZERO);
            
            // Transacción: crear préstamo y reducir cantidad disponible
            int prestamoId = prestamoDAO.registrarPrestamo(prestamo);
            if (prestamoId > 0 && libroDAO.reducirCantidadDisponible(idLibro)) {
                request.setAttribute("success", "Préstamo registrado exitosamente. ID: " + prestamoId);
                request.setAttribute("prestamoId", prestamoId);
            } else {
                request.setAttribute("error", "Error al registrar el préstamo");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Datos numéricos inválidos");
            mostrarFormularioNuevo(request, response);
            return;
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", "Fecha inválida");
            mostrarFormularioNuevo(request, response);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar el préstamo: " + e.getMessage());
        }
        
        listarPrestamos(request, response);
    }

    private void mostrarFormularioDevolucion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Prestamo prestamo = prestamoDAO.obtenerPorId(id);
            
            if (prestamo != null && "ACTIVO".equals(prestamo.getEstado())) {
                request.setAttribute("prestamo", prestamo);
                
                // Calcular multa si está vencido
                if (prestamo.isVencido()) {
                    long diasRetraso = prestamo.getDiasRetraso();
                    BigDecimal multaPorDia = new BigDecimal("1000"); // $1000 COP por día
                    BigDecimal multaTotal = multaPorDia.multiply(BigDecimal.valueOf(diasRetraso));
                    request.setAttribute("multaCalculada", multaTotal);
                    request.setAttribute("diasRetraso", diasRetraso);
                }
                
                request.getRequestDispatcher("/views/prestamos/return.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Préstamo no encontrado o ya devuelto");
                listarPrestamos(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de préstamo inválido");
            listarPrestamos(request, response);
        }
    }

    private void procesarDevolucion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String observaciones = request.getParameter("observaciones");
            String multaStr = request.getParameter("multa");
            
            BigDecimal multa = BigDecimal.ZERO;
            if (multaStr != null && !multaStr.trim().isEmpty()) {
                try {
                    multa = new BigDecimal(multaStr);
                } catch (NumberFormatException e) {
                    multa = BigDecimal.ZERO;
                }
            }
            
            Date fechaDevolucion = Date.valueOf(LocalDate.now());
            
            // Obtener préstamo para obtener el ID del libro
            Prestamo prestamo = prestamoDAO.obtenerPorId(id);
            if (prestamo == null || !"ACTIVO".equals(prestamo.getEstado())) {
                request.setAttribute("error", "Préstamo no válido para devolución");
                listarPrestamos(request, response);
                return;
            }
            
            // Transacción: registrar devolución y aumentar cantidad disponible
            if (prestamoDAO.registrarDevolucion(id, fechaDevolucion, observaciones, multa) &&
                libroDAO.aumentarCantidadDisponible(prestamo.getIdLibro())) {
                
                request.setAttribute("success", "Devolución registrada exitosamente");
            } else {
                request.setAttribute("error", "Error al registrar la devolución");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Datos inválidos");
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", "Fecha inválida");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar la devolución: " + e.getMessage());
        }
        
        listarPrestamos(request, response);
    }

    private void renovarPrestamo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String diasStr = request.getParameter("dias");
            
            int diasRenovacion = diasStr != null ? Integer.parseInt(diasStr) : 14;
            
            Prestamo prestamo = prestamoDAO.obtenerPorId(id);
            if (prestamo == null || !"ACTIVO".equals(prestamo.getEstado())) {
                request.setAttribute("error", "Préstamo no válido para renovación");
                listarPrestamos(request, response);
                return;
            }
            
            // Calcular nueva fecha de devolución
            LocalDate fechaActual = prestamo.getFechaDevolucionEsperada().toLocalDate();
            LocalDate nuevaFecha = fechaActual.plusDays(diasRenovacion);
            
            if (prestamoDAO.renovarPrestamo(id, Date.valueOf(nuevaFecha))) {
                request.setAttribute("success", "Préstamo renovado exitosamente hasta " + nuevaFecha);
            } else {
                request.setAttribute("error", "Error al renovar el préstamo");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Datos inválidos");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al renovar el préstamo: " + e.getMessage());
        }
        
        listarPrestamos(request, response);
    }

    private void marcarComoPerdido(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String observaciones = request.getParameter("observaciones");
            String multaStr = request.getParameter("multa");
            
            BigDecimal multa = new BigDecimal("50000"); // Multa por pérdida: $50,000 COP
            if (multaStr != null && !multaStr.trim().isEmpty()) {
                try {
                    multa = new BigDecimal(multaStr);
                } catch (NumberFormatException e) {
                    // Usar multa por defecto
                }
            }
            
            if (prestamoDAO.marcarComoPerdido(id, observaciones, multa)) {
                request.setAttribute("success", "Préstamo marcado como perdido");
            } else {
                request.setAttribute("error", "Error al marcar el préstamo como perdido");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Datos inválidos");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar la solicitud: " + e.getMessage());
        }
        
        listarPrestamos(request, response);
    }

    private void exportarPrestamos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            List<Prestamo> prestamos = prestamoDAO.obtenerTodos();
            ExcelGenerator.generarReportePrestamos(prestamos, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al generar el reporte: " + e.getMessage());
            listarPrestamos(request, response);
        }
    }

    private void exportarPrestamosActivosPDF(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            List<Prestamo> prestamos = prestamoDAO.obtenerActivos();
            PDFGenerator.generarReportePrestamosActivos(prestamos, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al generar el reporte PDF: " + e.getMessage());
            listarPrestamos(request, response);
        }
    }

    private void generarComprobante(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Prestamo prestamo = prestamoDAO.obtenerPorId(id);
            
            if (prestamo != null) {
                PDFGenerator.generarComprobantePrestamo(prestamo, response);
            } else {
                request.setAttribute("error", "Préstamo no encontrado");
                listarPrestamos(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de préstamo inválido");
            listarPrestamos(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al generar el comprobante: " + e.getMessage());
            listarPrestamos(request, response);
        }
    }
}