package sena.adso.biblioteca.util;

import sena.adso.biblioteca.dto.UsuarioSistema;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filtro para verificar autenticación en páginas protegidas
 * @author ADSO
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialización del filtro
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String uri = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();

        // Páginas que no requieren autenticación
        boolean isPublicPage = uri.equals(contextPath + "/") ||
                              uri.equals(contextPath + "/login") ||
                              uri.equals(contextPath + "/consulta-publica") ||
                              uri.endsWith("/login.jsp") ||
                              uri.endsWith("/consulta-publica.jsp") ||
                              uri.endsWith("/error.jsp") ||
                              uri.contains("/css/") ||
                              uri.contains("/js/") ||
                              uri.contains("/images/") ||
                              uri.contains("/resources/");

        // Verificar si el usuario está autenticado
        UsuarioSistema usuario = (session != null) ? 
            (UsuarioSistema) session.getAttribute("usuario") : null;

        if (isPublicPage || usuario != null) {
            // Permitir acceso
            chain.doFilter(request, response);
        } else {
            // Redirigir al login
            httpResponse.sendRedirect(contextPath + "/login");
        }
    }

    @Override
    public void destroy() {
        // Limpieza del filtro
    }
}