package sena.adso.biblioteca.controllers;

import sena.adso.biblioteca.dao.LibroDAO;
import sena.adso.biblioteca.dao.CategoriaDAO;
import sena.adso.biblioteca.dto.Libro;
import sena.adso.biblioteca.dto.Categoria;
import sena.adso.biblioteca.util.ExcelGenerator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet para gestión CRUD de libros
 * @author ADSO
 */
@WebServlet(name = "LibroServlet", urlPatterns = {"/libros"})
public class LibroServlet extends HttpServlet {

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
        
        String action = request.getParameter("action");
        
        try {
            switch (action != null ? action : "list") {
                case "list":
                    listarLibros(request, response);
                    break;
                case "new":
                    mostrarFormularioNuevo(request, response);
                    break;
                case "edit":
                    mostrarFormularioEditar(request, response);
                    break;
                case "view":
                    verLibro(request, response);
                    break;
                case "delete":
                    eliminarLibro(request, response);
                    break;
                case "search":
                    buscarLibros(request, response);
                    break;
                case "export":
                    exportarInventario(request, response);
                    break;
                default:
                    listarLibros(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar la solicitud: " + e.getMessage());
            listarLibros(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        try {
            switch (action != null ? action : "") {
                case "create":
                    crearLibro(request, response);
                    break;
                case "update":
                    actualizarLibro(request, response);
                    break;
                case "search":
                    buscarLibros(request, response);
                    break;
                default:
                    response.sendRedirect("libros");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar la solicitud: " + e.getMessage());
            listarLibros(request, response);
        }
    }

    private void listarLibros(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<Libro> libros = libroDAO.obtenerTodos();
        List<Categoria> categorias = categoriaDAO.obtenerTodas();
        
        request.setAttribute("libros", libros);
        request.setAttribute("categorias", categorias);
        request.setAttribute("totalLibros", libros.size());
        
        // Estadísticas
        long librosDisponibles = libros.stream().filter(Libro::isDisponible).count();
        long librosNoDisponibles = libros.size() - librosDisponibles;
        
        request.setAttribute("librosDisponibles", librosDisponibles);
        request.setAttribute("librosNoDisponibles", librosNoDisponibles);
        
        request.getRequestDispatcher("/views/libros/list.jsp").forward(request, response);
    }

    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<Categoria> categorias = categoriaDAO.obtenerTodas();
        request.setAttribute("categorias", categorias);
        request.setAttribute("isEdit", false);
        
        request.getRequestDispatcher("/views/libros/form.jsp").forward(request, response);
    }

    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Libro libro = libroDAO.obtenerPorId(id);
            
            if (libro != null) {
                List<Categoria> categorias = categoriaDAO.obtenerTodas();
                request.setAttribute("libro", libro);
                request.setAttribute("categorias", categorias);
                request.setAttribute("isEdit", true);
                
                request.getRequestDispatcher("/views/libros/form.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Libro no encontrado");
                listarLibros(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de libro inválido");
            listarLibros(request, response);
        }
    }

    private void verLibro(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Libro libro = libroDAO.obtenerPorId(id);
            
            if (libro != null) {
                request.setAttribute("libro", libro);
                request.getRequestDispatcher("/views/libros/view.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Libro no encontrado");
                listarLibros(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de libro inválido");
            listarLibros(request, response);
        }
    }

    private void crearLibro(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            Libro libro = mapearLibroDesdeRequest(request);
            
            // Validaciones
            String validationError = validarLibro(libro, 0);
            if (validationError != null) {
                request.setAttribute("error", validationError);
                request.setAttribute("libro", libro);
                mostrarFormularioNuevo(request, response);
                return;
            }
            
            if (libroDAO.insertar(libro)) {
                request.setAttribute("success", "Libro creado exitosamente");
            } else {
                request.setAttribute("error", "Error al crear el libro");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar los datos: " + e.getMessage());
        }
        
        listarLibros(request, response);
    }

    private void actualizarLibro(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Libro libro = mapearLibroDesdeRequest(request);
            libro.setId(id);
            
            // Validaciones
            String validationError = validarLibro(libro, id);
            if (validationError != null) {
                request.setAttribute("error", validationError);
                request.setAttribute("libro", libro);
                request.setAttribute("isEdit", true);
                List<Categoria> categorias = categoriaDAO.obtenerTodas();
                request.setAttribute("categorias", categorias);
                request.getRequestDispatcher("/views/libros/form.jsp").forward(request, response);
                return;
            }
            
            if (libroDAO.actualizar(libro)) {
                request.setAttribute("success", "Libro actualizado exitosamente");
            } else {
                request.setAttribute("error", "Error al actualizar el libro");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de libro inválido");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar los datos: " + e.getMessage());
        }
        
        listarLibros(request, response);
    }

    private void eliminarLibro(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            
            if (libroDAO.eliminar(id)) {
                request.setAttribute("success", "Libro eliminado exitosamente");
            } else {
                request.setAttribute("error", "Error al eliminar el libro");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de libro inválido");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al eliminar el libro: " + e.getMessage());
        }
        
        listarLibros(request, response);
    }

    private void buscarLibros(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String busqueda = request.getParameter("busqueda");
        String categoriaId = request.getParameter("categoria");
        
        List<Libro> libros;
        
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            libros = libroDAO.buscar(busqueda.trim());
            request.setAttribute("busqueda", busqueda.trim());
        } else if (categoriaId != null && !categoriaId.trim().isEmpty() && !categoriaId.equals("0")) {
            try {
                int idCategoria = Integer.parseInt(categoriaId);
                libros = libroDAO.obtenerPorCategoria(idCategoria);
                request.setAttribute("categoriaSeleccionada", categoriaId);
            } catch (NumberFormatException e) {
                libros = libroDAO.obtenerTodos();
            }
        } else {
            libros = libroDAO.obtenerTodos();
        }
        
        List<Categoria> categorias = categoriaDAO.obtenerTodas();
        request.setAttribute("libros", libros);
        request.setAttribute("categorias", categorias);
        request.setAttribute("totalLibros", libros.size());
        
        request.getRequestDispatcher("/views/libros/list.jsp").forward(request, response);
    }

    private void exportarInventario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            List<Libro> libros = libroDAO.obtenerTodos();
            ExcelGenerator.generarReporteInventario(libros, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al generar el reporte: " + e.getMessage());
            listarLibros(request, response);
        }
    }

    private Libro mapearLibroDesdeRequest(HttpServletRequest request) {
        Libro libro = new Libro();
        
        libro.setTitulo(request.getParameter("titulo"));
        libro.setAutor(request.getParameter("autor"));
        libro.setEditorial(request.getParameter("editorial"));
        
        try {
            libro.setAnoPublicacion(Integer.parseInt(request.getParameter("anoPublicacion")));
        } catch (NumberFormatException e) {
            libro.setAnoPublicacion(0);
        }
        
        try {
            libro.setIdCategoria(Integer.parseInt(request.getParameter("idCategoria")));
        } catch (NumberFormatException e) {
            libro.setIdCategoria(0);
        }
        
        libro.setIsbn(request.getParameter("isbn"));
        libro.setUbicacion(request.getParameter("ubicacion"));
        
        try {
            libro.setCantidadTotal(Integer.parseInt(request.getParameter("cantidadTotal")));
        } catch (NumberFormatException e) {
            libro.setCantidadTotal(1);
        }
        
        try {
            libro.setCantidadDisponible(Integer.parseInt(request.getParameter("cantidadDisponible")));
        } catch (NumberFormatException e) {
            libro.setCantidadDisponible(libro.getCantidadTotal());
        }
        
        libro.setDescripcion(request.getParameter("descripcion"));
        libro.setEstado(request.getParameter("estado"));
        
        return libro;
    }

    private String validarLibro(Libro libro, int idExcluir) {
        if (libro.getTitulo() == null || libro.getTitulo().trim().isEmpty()) {
            return "El título es obligatorio";
        }
        
        if (libro.getAutor() == null || libro.getAutor().trim().isEmpty()) {
            return "El autor es obligatorio";
        }
        
        if (libro.getEditorial() == null || libro.getEditorial().trim().isEmpty()) {
            return "La editorial es obligatoria";
        }
        
        if (libro.getAnoPublicacion() < 1500 || libro.getAnoPublicacion() > java.time.Year.now().getValue()) {
            return "El año de publicación no es válido";
        }
        
        if (libro.getIdCategoria() <= 0) {
            return "Debe seleccionar una categoría";
        }
        
        if (libro.getCantidadTotal() < 1) {
            return "La cantidad total debe ser mayor a 0";
        }
        
        if (libro.getCantidadDisponible() < 0 || libro.getCantidadDisponible() > libro.getCantidadTotal()) {
            return "La cantidad disponible no es válida";
        }
        
        // Validar ISBN único si se proporciona
        if (libro.getIsbn() != null && !libro.getIsbn().trim().isEmpty()) {
            if (libroDAO.existeIsbn(libro.getIsbn().trim(), idExcluir)) {
                return "Ya existe un libro con este ISBN";
            }
        }
        
        return null;
    }
}