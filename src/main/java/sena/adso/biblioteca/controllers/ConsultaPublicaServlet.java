package sena.adso.biblioteca.controllers;

import sena.adso.biblioteca.dao.LibroDAO;
import sena.adso.biblioteca.dao.CategoriaDAO;
import sena.adso.biblioteca.dto.Libro;
import sena.adso.biblioteca.dto.Categoria;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet para consulta pública de libros (sin autenticación)
 * @author ADSO
 */
@WebServlet(name = "ConsultaPublicaServlet", urlPatterns = {"/consulta-publica"})
public class ConsultaPublicaServlet extends HttpServlet {

    private LibroDAO libroDAO;
    private CategoriaDAO categoriaDAO;

    @Override
    public void init() throws ServletException {
        libroDAO = new LibroDAO();
        categoriaDAO = new CategoriaDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Obtener parámetros de búsqueda
            String busqueda = request.getParameter("busqueda");
            String categoriaId = request.getParameter("categoria");
            
            List<Libro> libros;
            
            if (busqueda != null && !busqueda.trim().isEmpty()) {
                // Búsqueda por texto
                libros = libroDAO.buscarDisponibles(busqueda.trim());
                request.setAttribute("busqueda", busqueda.trim());
                request.setAttribute("mensaje", "Resultados de búsqueda para: \"" + busqueda.trim() + "\"");
            } else if (categoriaId != null && !categoriaId.trim().isEmpty()) {
                try {
                    int idCategoria = Integer.parseInt(categoriaId);
                    // Filtrar por categoría (solo libros disponibles)
                    List<Libro> todosPorCategoria = libroDAO.obtenerPorCategoria(idCategoria);
                    libros = todosPorCategoria.stream()
                        .filter(libro -> libro.isDisponible())
                        .collect(java.util.stream.Collectors.toList());
                    
                    Categoria categoria = categoriaDAO.obtenerPorId(idCategoria);
                    if (categoria != null) {
                        request.setAttribute("mensaje", "Libros de la categoría: " + categoria.getNombre());
                    }
                    request.setAttribute("categoriaSeleccionada", categoriaId);
                } catch (NumberFormatException e) {
                    libros = libroDAO.obtenerDisponibles();
                    request.setAttribute("mensaje", "Todos los libros disponibles");
                }
            } else {
                // Mostrar todos los libros disponibles
                libros = libroDAO.obtenerDisponibles();
                request.setAttribute("mensaje", "Todos los libros disponibles");
            }
            
            // Obtener todas las categorías para el filtro
            List<Categoria> categorias = categoriaDAO.obtenerTodas();
            
            // Enviar datos a la vista
            request.setAttribute("libros", libros);
            request.setAttribute("categorias", categorias);
            request.setAttribute("totalResultados", libros.size());
            
            // Estadísticas básicas
            long librosActivos = libros.stream().filter(Libro::isDisponible).count();
            request.setAttribute("librosDisponibles", librosActivos);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al realizar la búsqueda. Intente nuevamente.");
            request.setAttribute("libros", List.of());
            request.setAttribute("categorias", List.of());
        }
        
        request.getRequestDispatcher("/views/public/consulta-publica.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Obtener parámetros del formulario de búsqueda
        String busqueda = request.getParameter("busqueda");
        String categoria = request.getParameter("categoria");
        
        // Construir URL de redirección
        StringBuilder url = new StringBuilder("consulta-publica");
        boolean hasParams = false;
        
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            url.append("?busqueda=").append(java.net.URLEncoder.encode(busqueda.trim(), "UTF-8"));
            hasParams = true;
        }
        
        if (categoria != null && !categoria.trim().isEmpty() && !categoria.equals("0")) {
            if (hasParams) {
                url.append("&");
            } else {
                url.append("?");
            }
            url.append("categoria=").append(categoria);
        }
        
        response.sendRedirect(url.toString());
    }
}