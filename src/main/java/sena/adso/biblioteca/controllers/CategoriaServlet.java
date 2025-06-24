package sena.adso.biblioteca.controllers;

import sena.adso.biblioteca.dao.CategoriaDAO;
import sena.adso.biblioteca.dto.Categoria;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet para gestión CRUD de categorías
 * @author ADSO
 */
@WebServlet(name = "CategoriaServlet", urlPatterns = {"/categorias"})
public class CategoriaServlet extends HttpServlet {

    private CategoriaDAO categoriaDAO;

    @Override
    public void init() throws ServletException {
        categoriaDAO = new CategoriaDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        try {
            switch (action != null ? action : "list") {
                case "list":
                    listarCategorias(request, response);
                    break;
                case "new":
                    mostrarFormularioNuevo(request, response);
                    break;
                case "edit":
                    mostrarFormularioEditar(request, response);
                    break;
                case "delete":
                    eliminarCategoria(request, response);
                    break;
                default:
                    listarCategorias(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar la solicitud: " + e.getMessage());
            listarCategorias(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        try {
            switch (action != null ? action : "") {
                case "create":
                    crearCategoria(request, response);
                    break;
                case "update":
                    actualizarCategoria(request, response);
                    break;
                default:
                    response.sendRedirect("categorias");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar la solicitud: " + e.getMessage());
            listarCategorias(request, response);
        }
    }

    private void listarCategorias(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<Categoria> categorias = categoriaDAO.obtenerTodas();
        
        request.setAttribute("categorias", categorias);
        request.setAttribute("totalCategorias", categorias.size());
        
        request.getRequestDispatcher("/views/categorias/list.jsp").forward(request, response);
    }

    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setAttribute("isEdit", false);
        request.getRequestDispatcher("/views/categorias/form.jsp").forward(request, response);
    }

    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Categoria categoria = categoriaDAO.obtenerPorId(id);
            
            if (categoria != null) {
                request.setAttribute("categoria", categoria);
                request.setAttribute("isEdit", true);
                request.getRequestDispatcher("/views/categorias/form.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Categoría no encontrada");
                listarCategorias(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de categoría inválido");
            listarCategorias(request, response);
        }
    }

    private void crearCategoria(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            Categoria categoria = mapearCategoriaDesdeRequest(request);
            
            // Validaciones
            String validationError = validarCategoria(categoria, 0);
            if (validationError != null) {
                request.setAttribute("error", validationError);
                request.setAttribute("categoria", categoria);
                mostrarFormularioNuevo(request, response);
                return;
            }
            
            if (categoriaDAO.insertar(categoria)) {
                request.setAttribute("success", "Categoría creada exitosamente");
            } else {
                request.setAttribute("error", "Error al crear la categoría");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar los datos: " + e.getMessage());
        }
        
        listarCategorias(request, response);
    }

    private void actualizarCategoria(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Categoria categoria = mapearCategoriaDesdeRequest(request);
            categoria.setId(id);
            
            // Validaciones
            String validationError = validarCategoria(categoria, id);
            if (validationError != null) {
                request.setAttribute("error", validationError);
                request.setAttribute("categoria", categoria);
                request.setAttribute("isEdit", true);
                request.getRequestDispatcher("/views/categorias/form.jsp").forward(request, response);
                return;
            }
            
            if (categoriaDAO.actualizar(categoria)) {
                request.setAttribute("success", "Categoría actualizada exitosamente");
            } else {
                request.setAttribute("error", "Error al actualizar la categoría");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de categoría inválido");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar los datos: " + e.getMessage());
        }
        
        listarCategorias(request, response);
    }

    private void eliminarCategoria(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            
            // Verificar si tiene libros asociados
            if (categoriaDAO.tieneLibrosAsociados(id)) {
                request.setAttribute("error", "No se puede eliminar la categoría porque tiene libros asociados");
                listarCategorias(request, response);
                return;
            }
            
            if (categoriaDAO.eliminar(id)) {
                request.setAttribute("success", "Categoría eliminada exitosamente");
            } else {
                request.setAttribute("error", "Error al eliminar la categoría");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de categoría inválido");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al eliminar la categoría: " + e.getMessage());
        }
        
        listarCategorias(request, response);
    }

    private Categoria mapearCategoriaDesdeRequest(HttpServletRequest request) {
        Categoria categoria = new Categoria();
        
        categoria.setNombre(request.getParameter("nombre"));
        categoria.setDescripcion(request.getParameter("descripcion"));
        
        return categoria;
    }

    private String validarCategoria(Categoria categoria, int idExcluir) {
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            return "El nombre de la categoría es obligatorio";
        }
        
        if (categoria.getNombre().trim().length() < 3) {
            return "El nombre de la categoría debe tener al menos 3 caracteres";
        }
        
        if (categoria.getNombre().trim().length() > 100) {
            return "El nombre de la categoría no puede exceder 100 caracteres";
        }
        
        // Validar nombre único
        if (categoriaDAO.existeNombre(categoria.getNombre().trim(), idExcluir)) {
            return "Ya existe una categoría con este nombre";
        }
        
        // Validar descripción si se proporciona
        if (categoria.getDescripcion() != null && categoria.getDescripcion().trim().length() > 500) {
            return "La descripción no puede exceder 500 caracteres";
        }
        
        return null;
    }
}