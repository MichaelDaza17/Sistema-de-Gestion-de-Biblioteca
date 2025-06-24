package sena.adso.biblioteca.controllers;

import sena.adso.biblioteca.dto.UsuarioSistema;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet para manejar el cierre de sesión
 * @author ADSO
 */
@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            // Obtener información del usuario para log
            UsuarioSistema usuario = (UsuarioSistema) session.getAttribute("usuario");
            if (usuario != null) {
                System.out.println("Logout para usuario: " + usuario.getUsername());
            }
            
            // Invalidar la sesión
            session.invalidate();
        }
        
        // Redirigir al login
        response.sendRedirect("login");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}