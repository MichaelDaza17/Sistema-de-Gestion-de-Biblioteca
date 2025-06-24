package sena.adso.biblioteca.controllers;

import sena.adso.biblioteca.dao.UsuarioSistemaDAO;
import sena.adso.biblioteca.dto.UsuarioSistema;
import sena.adso.biblioteca.util.CaptchaGenerator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet para manejar autenticación con CAPTCHA
 * @author ADSO
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private UsuarioSistemaDAO usuarioDAO;

    @Override
    public void init() throws ServletException {
        usuarioDAO = new UsuarioSistemaDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // Si ya está autenticado, redirigir al dashboard
        UsuarioSistema usuario = (UsuarioSistema) session.getAttribute("usuario");
        if (usuario != null) {
            response.sendRedirect("dashboard");
            return;
        }

        // Generar nuevo CAPTCHA
        String captchaText = CaptchaGenerator.generarTextoCaptcha();
        String captchaImage = CaptchaGenerator.generarImagenCaptcha(captchaText);
        
        // Guardar el texto del CAPTCHA en la sesión
        session.setAttribute("captchaText", captchaText);
        request.setAttribute("captchaImage", captchaImage);

        // Mostrar página de login
        request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        try {
            // Obtener parámetros del formulario
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String captchaInput = request.getParameter("captcha");
            
            // Obtener el CAPTCHA de la sesión
            String captchaSession = (String) session.getAttribute("captchaText");
            
            // Validar parámetros
            if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                captchaInput == null || captchaInput.trim().isEmpty()) {
                
                setErrorAndRedirect(request, response, "Todos los campos son obligatorios");
                return;
            }

            // Validar CAPTCHA
            if (!CaptchaGenerator.validarCaptcha(captchaInput, captchaSession)) {
                setErrorAndRedirect(request, response, "El código CAPTCHA es incorrecto");
                return;
            }

            // Autenticar usuario
            UsuarioSistema usuario = usuarioDAO.autenticar(username.trim(), password);
            
            if (usuario != null) {
                // Login exitoso
                session.setAttribute("usuario", usuario);
                session.removeAttribute("captchaText"); // Limpiar CAPTCHA
                
                // Registrar en log (opcional)
                System.out.println("Login exitoso para usuario: " + usuario.getUsername() + 
                                 " - Rol: " + usuario.getRol());
                
                // Redirigir al dashboard
                response.sendRedirect("dashboard");
                
            } else {
                // Credenciales inválidas
                setErrorAndRedirect(request, response, "Usuario o contraseña incorrectos");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            setErrorAndRedirect(request, response, "Error interno del servidor. Intente nuevamente.");
        }
    }

    /**
     * Establece un mensaje de error y redirige al login con nuevo CAPTCHA
     */
    private void setErrorAndRedirect(HttpServletRequest request, HttpServletResponse response, String mensaje)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // Generar nuevo CAPTCHA
        String captchaText = CaptchaGenerator.generarTextoCaptcha();
        String captchaImage = CaptchaGenerator.generarImagenCaptcha(captchaText);
        
        session.setAttribute("captchaText", captchaText);
        request.setAttribute("captchaImage", captchaImage);
        request.setAttribute("error", mensaje);
        
        // Mantener el username para que no se pierda
        String username = request.getParameter("username");
        if (username != null && !username.trim().isEmpty()) {
            request.setAttribute("username", username.trim());
        }
        
        request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
    }
}