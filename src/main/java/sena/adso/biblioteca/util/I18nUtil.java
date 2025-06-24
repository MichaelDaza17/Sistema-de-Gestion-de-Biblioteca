package sena.adso.biblioteca.util;

import java.util.Locale;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Utilidad para manejar la internacionalización (i18n) en la aplicación
 * @author ADSO
 */
public class I18nUtil {
    
    private static final String BUNDLE_NAME = "i18n.messages";
    
    /**
     * Obtiene el mensaje correspondiente a la clave especificada en el idioma actual
     * @param request La solicitud HTTP actual
     * @param key La clave del mensaje a obtener
     * @return El mensaje traducido o la clave si no se encuentra el mensaje
     */
    public static String getMessage(HttpServletRequest request, String key) {
        ResourceBundle bundle = getResourceBundle(request);
        
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            // Si no se encuentra la clave, devolver la clave misma
            return key;
        }
    }
    
    /**
     * Obtiene el código de idioma actual desde la sesión
     * @param request La solicitud HTTP actual
     * @return El código de idioma (ej. "es", "en")
     */
    public static String getCurrentLocale(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String locale = (String) session.getAttribute(LocaleFilter.LOCALE_SESSION_ATTRIBUTE);
        
        if (locale == null) {
            locale = LocaleFilter.DEFAULT_LOCALE;
            session.setAttribute(LocaleFilter.LOCALE_SESSION_ATTRIBUTE, locale);
        }
        
        return locale;
    }
    
    /**
     * Obtiene el ResourceBundle para el idioma actual
     * @param request La solicitud HTTP actual
     * @return El ResourceBundle correspondiente al idioma
     */
    private static ResourceBundle getResourceBundle(HttpServletRequest request) {
        String localeCode = getCurrentLocale(request);
        Locale locale = new Locale(localeCode);
        
        return ResourceBundle.getBundle(BUNDLE_NAME, locale);
    }
}