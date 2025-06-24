package sena.adso.biblioteca.controllers;

import sena.adso.biblioteca.dao.LibroDAO;
import sena.adso.biblioteca.dao.LectorDAO;
import sena.adso.biblioteca.dao.PrestamoDAO;
import sena.adso.biblioteca.dto.Prestamo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet para mostrar el dashboard principal
 * @author ADSO
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard", ""})
public class DashboardServlet extends HttpServlet {

    private LibroDAO libroDAO;
    private LectorDAO lectorDAO;
    private PrestamoDAO prestamoDAO;

    @Override
    public void init() throws ServletException {
        libroDAO = new LibroDAO();
        lectorDAO = new LectorDAO();
        prestamoDAO = new PrestamoDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Obtener estadísticas para el dashboard
            
            // Contar libros
            List<sena.adso.biblioteca.dto.Libro> todosLosLibros = libroDAO.obtenerTodos();
            List<sena.adso.biblioteca.dto.Libro> librosDisponibles = libroDAO.obtenerDisponibles();
            int totalLibros = todosLosLibros.size();
            int librosActivos = librosDisponibles.size();
            
            // Contar lectores
            List<sena.adso.biblioteca.dto.Lector> todosLosLectores = lectorDAO.obtenerTodos();
            List<sena.adso.biblioteca.dto.Lector> lectoresActivos = lectorDAO.obtenerActivos();
            int totalLectores = todosLosLectores.size();
            int lectoresActivosCount = lectoresActivos.size();
            
            // Contar préstamos
            int prestamosActivos = prestamoDAO.contarPrestamosActivos();
            int prestamosVencidos = prestamoDAO.contarPrestamosVencidos();
            
            // Obtener préstamos recientes (últimos 5)
            List<Prestamo> prestamosRecientes = prestamoDAO.obtenerActivos();
            if (prestamosRecientes.size() > 5) {
                prestamosRecientes = prestamosRecientes.subList(0, 5);
            }
            
            // Obtener préstamos vencidos para alerta
            List<Prestamo> prestamosVencidosList = prestamoDAO.obtenerVencidos();
            if (prestamosVencidosList.size() > 5) {
                prestamosVencidosList = prestamosVencidosList.subList(0, 5);
            }
            
            // Enviar datos a la vista
            request.setAttribute("totalLibros", totalLibros);
            request.setAttribute("librosDisponibles", librosActivos);
            request.setAttribute("totalLectores", totalLectores);
            request.setAttribute("lectoresActivos", lectoresActivosCount);
            request.setAttribute("prestamosActivos", prestamosActivos);
            request.setAttribute("prestamosVencidos", prestamosVencidos);
            request.setAttribute("prestamosRecientes", prestamosRecientes);
            request.setAttribute("prestamosVencidosList", prestamosVencidosList);
            
            // Calcular porcentajes para gráficos
            double porcentajeLibrosDisponibles = totalLibros > 0 ? 
                (double) librosActivos / totalLibros * 100 : 0;
            double porcentajeLectoresActivos = totalLectores > 0 ? 
                (double) lectoresActivosCount / totalLectores * 100 : 0;
            
            request.setAttribute("porcentajeLibrosDisponibles", Math.round(porcentajeLibrosDisponibles));
            request.setAttribute("porcentajeLectoresActivos", Math.round(porcentajeLectoresActivos));
            
            // Obtener libros más prestados (simulación - se podría implementar una consulta específica)
            List<sena.adso.biblioteca.dto.Libro> librosMasPrestados = librosDisponibles;
            if (librosMasPrestados.size() > 5) {
                librosMasPrestados = librosMasPrestados.subList(0, 5);
            }
            request.setAttribute("librosMasPrestados", librosMasPrestados);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al cargar las estadísticas del dashboard");
        }
        
        request.getRequestDispatcher("/views/dashboard/index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}