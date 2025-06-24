package sena.adso.biblioteca.util;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filtro para manejar cambios de idioma
 * @author ADSO
 */
@WebFilter("/*")
public class LocaleFilter implements Filter {

    public static final String LOCALE_SESSION_ATTRIBUTE = "locale";
    public static final String DEFAULT_LOCALE = "es";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialización del filtro
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Verificar si se está cambiando el idioma
        String locale = httpRequest.getParameter("locale");
        if (locale != null && !locale.trim().isEmpty()) {
            HttpSession session = httpRequest.getSession();
            session.setAttribute(LOCALE_SESSION_ATTRIBUTE, locale);
            
            // Obtener la URL de referencia para redirigir
            String referer = httpRequest.getHeader("Referer");
            if (referer != null && !referer.isEmpty()) {
                // Remover el parámetro locale de la URL de referencia
                String cleanReferer = referer.replaceAll("[?&]locale=[^&]*", "");
                httpResponse.sendRedirect(cleanReferer);
                return;
            } else {
                // Si no hay referencia, ir al dashboard o página principal
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/dashboard");
                return;
            }
        }

        // Continuar con la cadena de filtros
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Limpieza del filtro
    }
}